/*
 * JPhotoTagger tags and finds images fast.
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

package de.elmar_baumann.jpt.app;

import de.elmar_baumann.jpt.database.DatabaseMaintainance;
import de.elmar_baumann.jpt.event.listener.AppExitListener;
import de.elmar_baumann.jpt.event.listener.impl.ListenerSupport;
import de.elmar_baumann.jpt.factory.MetaFactory;
import de.elmar_baumann.jpt.helper.Cleanup;
import de.elmar_baumann.jpt.tasks.UserTasks;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.frames.AppFrame;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Life cycle of the application.
 *
 * @author  Elmar Baumann
 * @version 2009-07-30
 */
public final class AppLifeCycle {
    public static final AppLifeCycle               INSTANCE        =
        new AppLifeCycle();
    private final Set<Object>                      saveObjects     =
        new HashSet<Object>();
    private final ListenerSupport<AppExitListener> listenerSupport =
        new ListenerSupport<AppExitListener>();
    private final Set<FinalTask> finalTasks = new LinkedHashSet<FinalTask>();
    private AppFrame             appFrame;
    private boolean              started;

    private AppLifeCycle() {}

    /**
     * Has be to call <em>once</em> after the {@link AppFrame} has been created.
     *
     * @param appFrame the application's frame
     */
    public void started(AppFrame appFrame) {
        synchronized (this) {
            assert !started;

            if (started) {
                return;
            }

            started = true;
        }

        this.appFrame = appFrame;

        Thread thread = new Thread(MetaFactory.INSTANCE);

        thread.setName("Initializing meta factory @ "
                       + getClass().getSimpleName());
        thread.start();
        listenForQuit();
    }

    /**
     * Adds a thread that will be executed before the application window becomes
     * invisible and the application exists the VM.
     * <p>
     * <em>At this time, the database has been shutdown, so that database
     * operations are not possible!</em>
     *
     * @param task task
     */
    public void addFinalTask(FinalTask task) {
        synchronized (finalTasks) {
            finalTasks.add(task);
        }
    }

    public void removeFinalTask(FinalTask task) {
        synchronized (finalTasks) {
            finalTasks.remove(task);
        }
    }

    /**
     * Adds an object which is currently or will soon saving data. As long as
     * data is to save, the app does not exit.
     *
     * <em>Do not forget removing a save object via
     * {@link #removeSaveObject(java.lang.Object)}, otherwise the app terminates
     * after a timeout and does not know wheter data was saved!</em>
     *
     * @param saveObject object that saves data.
     */
    public void addSaveObject(Object saveObject) {
        synchronized (saveObjects) {
            saveObjects.add(saveObject);
        }
    }

    /**
     * Removes a save object added via {@link #addSaveObject(java.lang.Object)}.
     *
     * @param saveObject save object to remove
     */
    public void removeSaveObject(Object saveObject) {
        synchronized (saveObjects) {
            saveObjects.remove(saveObject);
        }
    }

    /**
     * Adds a listener to notify when the application will exit.
     *
     * @param listener listener
     */
    public void addAppExitListener(AppExitListener listener) {
        listenerSupport.add(listener);
    }

    /**
     * Removes a listener added by
     * {@link #addAppExitListener(de.elmar_baumann.jpt.event.listener.AppExitListener)}.
     *
     * @param listener listener
     */
    public void removeAppExitListener(AppExitListener listener) {
        listenerSupport.remove(listener);
    }

    private void notifyExitListeners() {
        synchronized (listenerSupport) {
            for (AppExitListener listener : listenerSupport.get()) {
                listener.appWillExit();
            }
        }
    }

    private void listenForQuit() {
        appFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent evt) {
                quit();
            }
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                quit();
            }
        });
        appFrame.getMenuItemExit().addActionListener(
            new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quit();
            }
        });
    }

    /**
     * Exits the VM.
     */
    public void quit() {
        if (ensureUserTasksFinished()) {
            notifyExitListeners();
            writeProperties();
            checkDataToSave();
            Cleanup.shutdown();
            DatabaseMaintainance.INSTANCE.shutdown();

            synchronized (finalTasks) {
                if (finalTasks.isEmpty()) {
                    quitVm();
                } else {
                    executeFinalTasksAndQuit();
                }
            }
        }
    }

    public static void quitBeforeGuiWasCreated() {
        DatabaseMaintainance.INSTANCE.shutdown();
        AppLock.unlock();
        System.exit(1);
    }

    private void quitVm() {
        appFrame.dispose();
        AppLock.unlock();
        System.exit(0);
    }

    private void executeFinalTasksAndQuit() {
        FinalTaskListener listener = new FinalTaskListener() {
            @Override
            public void finished() {
                synchronized (finalTasks) {
                    if (finalTasks.isEmpty()) {
                        quitVm();
                    }
                }
            }
        };

        synchronized (finalTasks) {
            Set<FinalTask> tasks = new HashSet<FinalTask>(finalTasks);
            GUI.INSTANCE.getAppFrame().setEnabled(false);

            for (FinalTask task : tasks) {
                task.addListener(listener);
                finalTasks.remove(task);
                task.execute();
            }
        }
    }

    private boolean ensureUserTasksFinished() {
        boolean finished = UserTasks.INSTANCE.getCount() <= 0;

        if (finished) {
            return true;
        }

        return MessageDisplayer.confirmYesNo(appFrame,
                "AppLifeCycle.Confirm.QuitOnUserTasks");
    }

    private void checkDataToSave() {
        long elapsedMilliseconds       = 0;
        long timeoutMilliSeconds       = 120 * 1000;
        long checkIntervalMilliSeconds = 2 * 1000;

        if (hasSaveObjects()) {
            AppLogger.logInfo(getClass(),
                              "AppLifeCycle.Info.SaveObjectsExisting",
                              saveObjects);

            while (hasSaveObjects()
                    && (elapsedMilliseconds < timeoutMilliSeconds)) {
                try {
                    elapsedMilliseconds += checkIntervalMilliSeconds;
                    Thread.sleep(checkIntervalMilliSeconds);
                } catch (Exception ex) {
                    AppLogger.logSevere(getClass(), ex);
                }

                if (elapsedMilliseconds >= timeoutMilliSeconds) {
                    MessageDisplayer.error(
                        null,
                        "AppLifeCycle.Error.ExitDataNotSaved.MaxWaitTimeExceeded",
                        timeoutMilliSeconds / 1000);
                }
            }
        }
    }

    private boolean hasSaveObjects() {
        synchronized (saveObjects) {
            return saveObjects.size() > 0;
        }
    }

    private void writeProperties() {
        UserSettings.INSTANCE.getSettings().setSizeAndLocation(appFrame);
        UserSettings.INSTANCE.writeToFile();
    }

    public interface FinalTaskListener {

        /**
         * Will be called after the task has been finished.
         */
        void finished();
    }


    public static abstract class FinalTask {
        private final ListenerSupport<FinalTaskListener> listenerSupport =
            new ListenerSupport<FinalTaskListener>();

        public void addListener(FinalTaskListener listener) {
            listenerSupport.add(listener);
        }

        public void removeListener(FinalTaskListener listener) {
            listenerSupport.remove(listener);
        }

        protected void notifyFinished() {
            Set<FinalTaskListener> listeners = listenerSupport.get();

            synchronized (listeners) {
                for (FinalTaskListener listener : listeners) {
                    listener.finished();
                }
            }
        }

        public abstract void execute();
    }
}
