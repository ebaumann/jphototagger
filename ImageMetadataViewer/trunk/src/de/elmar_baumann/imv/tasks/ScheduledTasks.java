package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppLookAndFeel;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.event.CheckForUpdateMetadataEvent;
import de.elmar_baumann.imv.event.CheckForUpdateMetadataEvent.Type;
import de.elmar_baumann.imv.event.listener.CheckingForUpdateMetadataListener;
import de.elmar_baumann.imv.helper.InsertImageFilesIntoDatabase;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.lib.concurrent.SerialExecutor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;

/**
 * Runs scheduled tasks after {@link UserSettings#getMinutesToStartScheduledTasks()}.
 *
 * To work initially, {@link #run()} has to be called.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ScheduledTasks implements ActionListener,
                                             CheckingForUpdateMetadataListener {

    public static final ScheduledTasks INSTANCE = new ScheduledTasks();
    private final SerialExecutor executor =
            new SerialExecutor(Executors.newCachedThreadPool());
    private final JButton button =
            GUI.INSTANCE.getAppPanel().getButtonStopScheduledTasks();
    private final long MINUTES_WAIT_BEFORE_PERFORM =
            UserSettings.INSTANCE.getMinutesToStartScheduledTasks();
    private volatile boolean isRunning = false;

    private enum ButtonState {

        START,
        STOP
    }

    /**
     * Runs the tasks after {@link UserSettings#getMinutesToStartScheduledTasks()}.
     */
    public synchronized void run() {
        if (isRunning || MINUTES_WAIT_BEFORE_PERFORM <= 0) return;
        isRunning = true;
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    TimeUnit.MINUTES.sleep(MINUTES_WAIT_BEFORE_PERFORM);
                    setStart();
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
        setButtonState(ButtonState.START);
    }

    private void startUpdate() {
        List<InsertImageFilesIntoDatabase> updaters =
                ScheduledTaskInsertImageFilesIntoDatabase.getThreads();
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
            setButtonState(ButtonState.START);
            isRunning = false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        synchronized (button) {
            if (isRunning) {
                shutdown();
            } else {
                setStart();
                startUpdate();
            }
        }
    }

    private void setStart() {
        synchronized (button) {
            isRunning = true;
            setButtonState(ButtonState.STOP);
        }
    }

    private void setButtonState(ButtonState state) {
        button.setEnabled(true);
        if (state.equals(ButtonState.START)) {
            button.setIcon(AppLookAndFeel.getIcon("icon_start_scheduled_tasks.png"));
        } else if (state.equals(ButtonState.STOP)) {
            button.setIcon(AppLookAndFeel.getIcon(
                    "icon_stop_scheduled_tasks_enabled.png"));
        } else {
            assert false : "Unhandled state!";
        }
    }

    private ScheduledTasks() {
        if (MINUTES_WAIT_BEFORE_PERFORM <= 0) {
            setButtonState(ButtonState.START);
        }
        button.addActionListener(this);
    }
}
