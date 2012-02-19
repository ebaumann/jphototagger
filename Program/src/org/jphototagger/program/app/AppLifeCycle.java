package org.jphototagger.program.app;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.EventBus;

import org.openide.util.Lookup;

import org.jphototagger.api.applifecycle.AppWillExitEvent;
import org.jphototagger.api.concurrent.ReplaceableTask;
import org.jphototagger.api.concurrent.SerialTaskExecutor;
import org.jphototagger.api.modules.Module;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.event.listener.ListenerSupport;
import org.jphototagger.domain.repository.Repository;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.AppFrame;
import org.jphototagger.program.factory.MetaFactory;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.tasks.ScheduledTasks;

/**
 * Life cycle of the application.
 *
 * @author Elmar Baumann
 */
public final class AppLifeCycle {

    public static final AppLifeCycle INSTANCE = new AppLifeCycle();
    private final Set<Object> saveObjects = new HashSet<Object>();
    private final Set<FinalTask> finalTasks = new LinkedHashSet<FinalTask>();
    private AppFrame appFrame;
    private boolean started;
    private static final Logger LOGGER = Logger.getLogger(AppLifeCycle.class.getName());

    private AppLifeCycle() {
    }

    /**
     * Has be to called <em>once</em> after the {@code AppFrame} has been
     * created.
     *
     * @param appFrame the application's frame
     */
    public void started(AppFrame appFrame) {
        if (appFrame == null) {
            throw new NullPointerException("appFrame == null");
        }

        synchronized (this) {
            assert !started;

            if (started) {
                return;
            }

            started = true;
        }

        this.appFrame = appFrame;

        Thread thread = new Thread(MetaFactory.INSTANCE, "JPhotoTagger: Initializing meta factory");

        thread.start();
        listenForQuit();
    }

    /**
     * Adds a thread that will be executed before the application window becomes
     * invisible and the application exists the VM.
     * <p>
     * <em>At this time, the repository has been shutdown, so that repository
     * operations are not possible!</em>
     *
     * @param task task
     */
    public void addFinalTask(FinalTask task) {
        if (task == null) {
            throw new NullPointerException("task == null");
        }

        synchronized (finalTasks) {
            finalTasks.add(task);
        }
    }

    public void removeFinalTask(FinalTask task) {
        if (task == null) {
            throw new NullPointerException("task == null");
        }

        synchronized (finalTasks) {
            finalTasks.remove(task);
        }
    }

    /**
     * Adds an object which is currently or will soon saving data. As long as
     * data is to save, the app does not exit.
     *
     * <em>Do not forget removing a save object via
     * {@code #removeSaveObject(java.lang.Object)}, otherwise the app terminates
     * after a timeout and does not know wheter data was saved!</em>
     *
     * @param saveObject object that saves data.
     */
    public void addSaveObject(Object saveObject) {
        if (saveObject == null) {
            throw new NullPointerException("saveObject == null");
        }

        synchronized (saveObjects) {
            saveObjects.add(saveObject);
        }
    }

    /**
     * Removes a save object added via {@code #addSaveObject(java.lang.Object)}.
     *
     * @param saveObject save object to remove
     */
    public void removeSaveObject(Object saveObject) {
        if (saveObject == null) {
            throw new NullPointerException("saveObject == null");
        }

        synchronized (saveObjects) {
            saveObjects.remove(saveObject);
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
    }

    /**
     * Exits the VM.
     */
    public void quit() {
        if (ensureTasksFinished()) {
            EventBus.publish(new AppWillExitEvent(this));
            notifyModulesForClose();
            writeProperties();
            checkDataToSave();
            Cleanup.shutdown();
            Repository repo = Lookup.getDefault().lookup(Repository.class);

            repo.shutdown();

            synchronized (finalTasks) {
                if (finalTasks.isEmpty()) {
                    quitVm();
                } else {
                    executeFinalTasksAndQuit();
                }
            }
        }
    }

    private void notifyModulesForClose() {
        Collection<? extends Module> modules = Lookup.getDefault().lookupAll(Module.class);

        for (Module module : modules) {
            module.remove();
        }
    }

    public static void quitBeforeGuiWasCreated() {
        Lookup.getDefault().lookup(Repository.class).shutdown();
        AppStartupLock.unlock();
        System.exit(1);
    }

    private void quitVm() {
        appFrame.dispose();
        AppStartupLock.unlock();
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

            GUI.getAppFrame().setEnabled(false);

            for (FinalTask task : tasks) {
                task.addListener(listener);
                finalTasks.remove(task);
                task.execute();
            }
        }
    }

    private boolean ensureTasksFinished() {
        SerialTaskExecutor executor = Lookup.getDefault().lookup(SerialTaskExecutor.class);
        boolean finished = executor.getTaskCount() <= 0;

        if (finished) {
            return true;
        }

        String message = Bundle.getString(AppLifeCycle.class, "AppLifeCycle.Confirm.QuitOnUserTasks");

        return MessageDisplayer.confirmYesNo(appFrame, message);
    }

    private void checkDataToSave() {
        long elapsedMilliseconds = 0;
        long timeoutMilliSeconds = 120 * 1000;
        long checkIntervalMilliSeconds = 2 * 1000;

        if (hasSaveObjects()) {
            LOGGER.log(Level.INFO, "Application waits until those objects have saved data: {0}", saveObjects);

            while (hasSaveObjects() && (elapsedMilliseconds < timeoutMilliSeconds)) {
                try {
                    elapsedMilliseconds += checkIntervalMilliSeconds;
                    Thread.sleep(checkIntervalMilliSeconds);
                } catch (Exception ex) {
                    Logger.getLogger(AppLifeCycle.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (elapsedMilliseconds >= timeoutMilliSeconds) {
                    String message = Bundle.getString(AppLifeCycle.class,
                            "AppLifeCycle.Error.ExitDataNotSaved.MaxWaitTimeExceeded", timeoutMilliSeconds / 1000);
                    MessageDisplayer.error(null, message);
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
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        String key = appFrame.getClass().getName();

        prefs.setSize(key, appFrame);
        prefs.setLocation(key, appFrame);
    }

    public interface FinalTaskListener {

        /**
         * Will be called after the task has been finished.
         */
        void finished();
    }

    public static abstract class FinalTask {

        private final ListenerSupport<FinalTaskListener> ls = new ListenerSupport<FinalTaskListener>();

        public void addListener(FinalTaskListener listener) {
            if (listener == null) {
                throw new NullPointerException("listener == null");
            }

            ls.add(listener);
        }

        public void removeListener(FinalTaskListener listener) {
            if (listener == null) {
                throw new NullPointerException("listener == null");
            }

            ls.remove(listener);
        }

        protected void notifyFinished() {
            for (FinalTaskListener listener : ls.get()) {
                listener.finished();
            }
        }

        public abstract void execute();
    }

    private static class Cleanup {

        /**
         * Sleep time in milliseconds before giving control to the caller, so that
         * the threads can complete their current action before they check for
         * interruption.
         */
        private static long MILLISECONDS_SLEEP = 2000;

        /**
         * Shuts down all Tasks.
         */
        public static void shutdown() {
            SerialTaskExecutor serialTaskExecutor = Lookup.getDefault().lookup(SerialTaskExecutor.class);
            ReplaceableTask replaceableTask = Lookup.getDefault().lookup(ReplaceableTask.class);

            ScheduledTasks.INSTANCE.cancelCurrentTasks();
            replaceableTask.cancelRunningTask();
            serialTaskExecutor.cancelAllTasks();

            boolean serialTasksRunning = serialTaskExecutor.getTaskCount() > 0;
            boolean sleep = (ScheduledTasks.INSTANCE.getCount() > 0) || serialTasksRunning;

            if (sleep) {
                sleep();
            }
        }

        private static void sleep() {
            try {

                // Let the tasks a little bit time to complete until they can interrupt
                Thread.sleep(MILLISECONDS_SLEEP);
            } catch (Exception ex) {
                Logger.getLogger(Cleanup.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private Cleanup() {
        }
    }
}
