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

import org.jphototagger.lib.concurrent.SerialExecutor;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.event.listener.UpdateMetadataCheckListener;
import org.jphototagger.program.event.UpdateMetadataCheckEvent;
import org.jphototagger.program.event.UpdateMetadataCheckEvent.Type;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.dialogs.SettingsDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * Runs scheduled tasks after
 * {@link UserSettings#getMinutesToStartScheduledTasks()}.
 *
 * To work initially, {@link #run()} has to be called.
 *
 * @author  Elmar Baumann
 */
public final class ScheduledTasks
        implements ActionListener, UpdateMetadataCheckListener {
    private static final Map<ButtonState, Icon> ICON_OF_BUTTON_STATE =
        new EnumMap<ButtonState, Icon>(ButtonState.class);
    private static final Map<ButtonState, String> TOOLTIP_TEXT_OF_BUTTON_STATE =
        new EnumMap<ButtonState, String>(ButtonState.class);
    public static final ScheduledTasks INSTANCE = new ScheduledTasks();
    private final SerialExecutor       executor =
        new SerialExecutor(Executors.newCachedThreadPool());
    private final JButton button =
        SettingsDialog.INSTANCE.getButtonScheduledTasks();
    private final long MINUTES_WAIT_BEFORE_PERFORM =
        UserSettings.INSTANCE.getMinutesToStartScheduledTasks();
    private volatile boolean isRunning;
    private volatile boolean runnedManual;

    private enum ButtonState { START, CANCEL }

    private ScheduledTasks() {
        init();
        button.addActionListener(this);
    }

    private static void init() {
        TOOLTIP_TEXT_OF_BUTTON_STATE.put(
            ButtonState.START,
            JptBundle.INSTANCE.getString("ScheduledTasks.TooltipText.Start"));
        TOOLTIP_TEXT_OF_BUTTON_STATE.put(
            ButtonState.CANCEL,
            JptBundle.INSTANCE.getString("ScheduledTasks.TooltipText.Cancel"));
        ICON_OF_BUTTON_STATE.put(ButtonState.START, AppLookAndFeel.ICON_START);
        ICON_OF_BUTTON_STATE.put(ButtonState.CANCEL,
                                 AppLookAndFeel.ICON_CANCEL);
    }

    /**
     * Runs the tasks after
     * {@link UserSettings#getMinutesToStartScheduledTasks()}.
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
                    AppLogger.logSevere(getClass(), ex);
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
     * {@link org.jphototagger.lib.concurrent.Cancelable}, its method
     * {@link org.jphototagger.lib.concurrent.Cancelable#cancel()} will be
     * called. If it does not implement that interface and it is an instance of
     * {@link Thread}, {@link Thread#interrupt()} will be called.
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
                InsertImageFilesIntoDatabase inserter =
                    ScheduledTaskInsertImageFilesIntoDatabase.getThread();

                if (inserter != null) {
                    inserter.addUpdateMetadataCheckListener(INSTANCE);
                    executor.execute(inserter);
                }
            }
        }, "JPhotoTagger: Inserting image files into database").start();
    }

    @Override
    public void actionPerformed(UpdateMetadataCheckEvent evt) {
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

    private void setButtonState(ButtonState state) {
        button.setIcon(ICON_OF_BUTTON_STATE.get(state));
        button.setToolTipText(TOOLTIP_TEXT_OF_BUTTON_STATE.get(state));
    }
}
