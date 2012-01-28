package org.jphototagger.program.tasks;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JButton;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.metadata.event.UpdateMetadataCheckEvent;
import org.jphototagger.domain.metadata.event.UpdateMetadataCheckEvent.Type;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.concurrent.SerialExecutor;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.program.misc.SaveToOrUpdateFilesInRepositoryImpl;
import org.jphototagger.program.settings.AppPreferencesKeys;
import org.jphototagger.program.settings.SettingsDialog;

/**
 * Runs scheduled tasks after
 * {@code UserSettings#getMinutesToStartScheduledTasks()}.
 *
 * To work initially, {@code #run()} has to be called.
 *
 * @author Elmar Baumann
 */
public final class ScheduledTasks implements ActionListener {

    private static final int DEFAULT_MINUTES_TO_START_SCHEDULED_TASKS = 5;
    private static final Map<ButtonState, Icon> ICON_OF_BUTTON_STATE = new EnumMap<ButtonState, Icon>(ButtonState.class);
    private static final Map<ButtonState, String> TOOLTIP_TEXT_OF_BUTTON_STATE = new EnumMap<ButtonState, String>(ButtonState.class);
    public static final ScheduledTasks INSTANCE = new ScheduledTasks();
    private final SerialExecutor executor = new SerialExecutor(Executors.newCachedThreadPool());
    private final JButton button = SettingsDialog.INSTANCE.getButtonScheduledTasks();
    private final long MINUTES_WAIT_BEFORE_PERFORM = getMinutesToStartScheduledTasks();
    private volatile boolean isRunning;
    private volatile boolean runnedManual;

    private enum ButtonState {

        START, CANCEL
    }

    private ScheduledTasks() {
        init();
        listen();
    }

    private static void init() {
        TOOLTIP_TEXT_OF_BUTTON_STATE.put(ButtonState.START, Bundle.getString(ScheduledTasks.class, "ScheduledTasks.TooltipText.Start"));
        TOOLTIP_TEXT_OF_BUTTON_STATE.put(ButtonState.CANCEL, Bundle.getString(ScheduledTasks.class, "ScheduledTasks.TooltipText.Cancel"));
        ICON_OF_BUTTON_STATE.put(ButtonState.START, AppLookAndFeel.ICON_START);
        ICON_OF_BUTTON_STATE.put(ButtonState.CANCEL, AppLookAndFeel.ICON_CANCEL);
    }

    private void listen() {
        button.addActionListener(this);
        AnnotationProcessor.process(this);
    }

    public static int getMinutesToStartScheduledTasks() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);
        int minutes = storage.getInt(AppPreferencesKeys.KEY_SCHEDULED_TASKS_MINUTES_TO_START_SCHEDULED_TASKS);

        return (minutes > 0)
                ? minutes
                : DEFAULT_MINUTES_TO_START_SCHEDULED_TASKS;
    }

    /**
     * Runs the tasks after
     * {@code UserSettings#getMinutesToStartScheduledTasks()}.
     */
    public synchronized void run() {
        if (isRunning || runnedManual || (MINUTES_WAIT_BEFORE_PERFORM <= 0)) {
            return;
        }

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    TimeUnit.MINUTES.sleep(MINUTES_WAIT_BEFORE_PERFORM);

                    if (!runnedManual) {
                        setStart();
                        startUpdate();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ScheduledTasks.class.getName()).log(Level.SEVERE, null, this);
                }
            }
        }, "JPhotoTagger: Scheduled tasks");

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
     * Removes all added user tasks.
     * <p>
     * If the active runnable implements
     * {@code org.jphototagger.lib.concurrent.Cancelable}, its method
     * {@code org.jphototagger.lib.concurrent.Cancelable#cancel()} will be
     * called. If it does not implement that interface and it is an instance of
     * {@code Thread}, {@code Thread#interrupt()} will be called.
     */
    public void cancelCurrentTasks() {
        executor.cancel();
        setButtonState(ButtonState.START);
        isRunning = false;
    }

    private void startUpdate() {

        // Thread because the button does not redraw until the longer operation
        // to gather the files has been finished
        new Thread(new Runnable() {

            @Override
            public void run() {
                SaveToOrUpdateFilesInRepositoryImpl inserter = InsertImageFilesIntoRepositoryScheduledTask.getThread();

                if (inserter != null) {
                    executor.execute(inserter);
                }
            }
        }, "JPhotoTagger: Inserting image files into repository").start();
    }

    @EventSubscriber(eventClass = UpdateMetadataCheckEvent.class)
    public void checkForUpdate(UpdateMetadataCheckEvent evt) {
        if (evt.getType().equals(Type.CHECK_FINISHED)) {
            setButtonState(ButtonState.START);
            isRunning = false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        synchronized (button) {
            if (isRunning) {
                cancelCurrentTasks();
            } else {
                runnedManual = true;
                setStart();
                startUpdate();
            }
        }
    }

    private void setStart() {
        synchronized (button) {
            isRunning = true;
            setButtonState(ButtonState.CANCEL);
        }
    }

    private void setButtonState(final ButtonState state) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                button.setIcon(ICON_OF_BUTTON_STATE.get(state));
                button.setToolTipText(TOOLTIP_TEXT_OF_BUTTON_STATE.get(state));
            }
        });
    }
}
