/*
 * @(#)ScheduledTasks.java    Created on 2008-10-05
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.tasks;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.event.listener.UpdateMetadataCheckListener;
import org.jphototagger.program.event.UpdateMetadataCheckEvent;
import org.jphototagger.program.event.UpdateMetadataCheckEvent.Type;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.dialogs.SettingsDialog;
import org.jphototagger.lib.concurrent.SerialExecutor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * Runs scheduled tasks after {@link UserSettings#getMinutesToStartScheduledTasks()}.
 *
 * To work initially, {@link #run()} has to be called.
 *
 * @author  Elmar Baumann
 */
public final class ScheduledTasks
        implements ActionListener, UpdateMetadataCheckListener {
    public static final ScheduledTasks          INSTANCE             =
        new ScheduledTasks();
    private static final Map<ButtonState, Icon> ICON_OF_BUTTON_STATE =
        new HashMap<ButtonState, Icon>();
    private static final Map<ButtonState, String> TOOLTIP_TEXT_OF_BUTTON_STATE =
        new HashMap<ButtonState, String>();

    static {
        ICON_OF_BUTTON_STATE.put(
            ButtonState.START,
            AppLookAndFeel.getIcon("icon_start_scheduled_tasks.png"));
        TOOLTIP_TEXT_OF_BUTTON_STATE.put(
            ButtonState.START,
            JptBundle.INSTANCE.getString("ScheduledTasks.TooltipText.Start"));
        TOOLTIP_TEXT_OF_BUTTON_STATE.put(
            ButtonState.STOP,
            JptBundle.INSTANCE.getString("ScheduledTasks.TooltipText.Stop"));
        ICON_OF_BUTTON_STATE.put(
            ButtonState.STOP,
            AppLookAndFeel.getIcon("icon_stop_scheduled_tasks_enabled.png"));
    }

    private final SerialExecutor executor =
        new SerialExecutor(Executors.newCachedThreadPool());
    private final JButton button =
        SettingsDialog.INSTANCE.getButtonScheduledTasks();
    private final long MINUTES_WAIT_BEFORE_PERFORM =
        UserSettings.INSTANCE.getMinutesToStartScheduledTasks();
    private volatile boolean isRunning;

    private enum ButtonState { START, STOP }

    private ScheduledTasks() {
        if (MINUTES_WAIT_BEFORE_PERFORM <= 0) {
            setButtonState(ButtonState.START);
        }

        button.addActionListener(this);
    }

    /**
     * Runs the tasks after {@link UserSettings#getMinutesToStartScheduledTasks()}.
     */
    public synchronized void run() {
        if (isRunning || (MINUTES_WAIT_BEFORE_PERFORM <= 0)) {
            return;
        }

        isRunning = true;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.MINUTES.sleep(MINUTES_WAIT_BEFORE_PERFORM);
                    setStart();
                    startUpdate();
                } catch (Exception ex) {
                    AppLogger.logSevere(getClass(), ex);
                }
            }
        });

        thread.setName("Scheduled tasks");
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
    public void stopCurrentTasks() {
        executor.shutdown();
        setButtonState(ButtonState.START);
        isRunning = false;
    }

    private void startUpdate() {
        List<InsertImageFilesIntoDatabase> updaters =
            ScheduledTaskInsertImageFilesIntoDatabase.getThreads();

        for (InsertImageFilesIntoDatabase updater : updaters) {
            executor.execute(updater);
        }

        if (updaters.size() > 0) {
            updaters.get(updaters.size()
                         - 1).addUpdateMetadataCheckListener(this);
        }
    }

    @Override
    public void actionPerformed(UpdateMetadataCheckEvent evt) {
        if (evt.getType().equals(Type.CHECK_FINISHED)) {
            setButtonState(ButtonState.START);
            isRunning = false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        synchronized (button) {
            if (isRunning) {
                stopCurrentTasks();
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
        button.setIcon(ICON_OF_BUTTON_STATE.get(state));
        button.setToolTipText(TOOLTIP_TEXT_OF_BUTTON_STATE.get(state));
    }
}
