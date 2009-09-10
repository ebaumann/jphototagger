package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.event.CheckForUpdateMetadataEvent;
import de.elmar_baumann.imv.event.CheckForUpdateMetadataEvent.Type;
import de.elmar_baumann.imv.event.listener.CheckingForUpdateMetadataListener;
import de.elmar_baumann.imv.helper.InsertImageFilesIntoDatabase;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.lib.concurrent.SerialExecutor;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Kontrolliert: Regelmäßiger Task, der Verzeichnisse nach modifizierten
 * Metadaten scannt und bei Funden die Datenbank aktualisiert. Arbeitet
 * erst durch Aufruf von {@link #run()}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ScheduledTasks implements CheckingForUpdateMetadataListener {

    public static final ScheduledTasks INSTANCE =
            new ScheduledTasks();
    private final SerialExecutor executor =
            new SerialExecutor(Executors.newCachedThreadPool());
    private static final long MINUTES_WAIT_BEFORE_PERFORM =
            UserSettings.INSTANCE.getMinutesToStartScheduledTasks();

    /**
     * Runs the tasks.
     */
    public synchronized void run() {
        if (MINUTES_WAIT_BEFORE_PERFORM <= 0) return;
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    TimeUnit.MINUTES.sleep(MINUTES_WAIT_BEFORE_PERFORM);
                    startUpdate();
                } catch (Exception ex) {
                    AppLog.logSevere(getClass(), ex);
                }
            }
        });
        thread.setName(
                "Scheduled tasks waiting for start @ " + getClass().getName()); // NOI18N
        thread.start();
    }

    /**
     * Returns the count of scheduled tasks.
     *
     * @return count of scheduled tasks
     */
    public int getCount() {
        return executor.getCount();
    }

    /**
     * Removes all added user tasks and calls {@link Thread#interrupt()} of the
     * currently running runnable if it's an instance of
     * <code>java.lang.Thread</code>.
     *
     * This means: The currently running task stops only when it is a thread
     * that will periodically check {@link Thread#isInterrupted()}.
     *
     * If the active runnable has a method named <strong>cancel</strong> with
     * no parameters, it will be invoked instead of <strong>interrupt</strong>.
     */
    public void shutdown() {
        executor.shutdown();
        setEnabledStopButton(false);
    }

    private void startUpdate() {
        List<InsertImageFilesIntoDatabase> updaters =
                ScheduledTaskInsertImageFilesIntoDatabase.getThreads();
        setEnabledStopButton(true);
        for (InsertImageFilesIntoDatabase updater : updaters) {
            executor.execute(updater);
        }
        if (updaters.size() > 0) {
            updaters.get(updaters.size() - 1).addActionListener(this);
        }
    }

    @Override
    public void actionPerformed(CheckForUpdateMetadataEvent evt) {
        if (evt.getType().equals(Type.CHECK_FINISHED)) {
            setEnabledStopButton(false);
        }
    }

    public void setEnabledStopButton(boolean enabled) {
        GUI.INSTANCE.getAppPanel().getButtonStopScheduledTasks().setEnabled(
                enabled);
    }

    private ScheduledTasks() {
    }
}
