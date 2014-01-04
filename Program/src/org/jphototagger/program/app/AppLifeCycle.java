package org.jphototagger.program.app;

import java.awt.event.WindowAdapter;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bushe.swing.event.EventBus;
import org.jphototagger.api.applifecycle.AppExitTask;
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
import org.openide.util.Lookup;

/**
 * Life cycle of the application.
 *
 * @author Elmar Baumann
 */
public final class AppLifeCycle {

    public static final AppLifeCycle INSTANCE = new AppLifeCycle();
    private static final Logger LOGGER = Logger.getLogger(AppLifeCycle.class.getName());
    private final Set<Object> saveObjects = new HashSet<>();
    private final Set<FinalTask> finalTasks = new LinkedHashSet<>();
    private AppFrame appFrame;
    private boolean started;

    /**
     * Has be to called <em>once</em> after the {@code AppFrame} has been created.
     *
     * @param appFrame the application's frame
     */
    public void started(AppFrame appFrame) {
        if (appFrame == null) {
            throw new NullPointerException("appFrame == null");
        }
        synchronized (this) {
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

    private void listenForQuit() {
        appFrame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                quit();
            }
        });
    }

    /**
     * Adds a thread that will be executed before the application window becomes invisible and the application exists
     * the VM.
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

    public interface FinalTaskListener {

    /**
         * Will be called after the task has been finished.
         */
        void finished();
    }

    public static abstract class FinalTask {

        private final ListenerSupport<FinalTaskListener> ls = new ListenerSupport<>();

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

    private void executeFinalTasksAndQuit() {

        final FinalTaskListener listenerForQuitVm = new FinalTaskListener() {

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
            Set<FinalTask> tasks = new HashSet<>(finalTasks);
            GUI.getAppFrame().setEnabled(false);
            for (FinalTask task : tasks) {
                task.addListener(listenerForQuitVm);
                finalTasks.remove(task);
                task.execute();
            }
        }
    }

    /**
     * Adds an object which is currently or will soon saving data. As long as data is to save, the app does not exit.
     *
     * <em>Do not forget removing a save object via {@code #removeSaveObject(java.lang.Object)}, otherwise the
     * application terminates after a timeout and some data may not be saved!</em>
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

    /**
     * Exits the VM.
     */
    public void quit() {
        if (ensureSerialTasksFinished()) {
            EventBus.publish(new AppWillExitEvent(this));
            persistAppFrame();
            waitUntilAllSaveObjectsSaved();
            executeExitTasks();
            removeModules();
            synchronized (finalTasks) {
                if (finalTasks.isEmpty()) {
                    quitVm();
                } else {
                    executeFinalTasksAndQuit();
                }
            }
        }
    }

    private void removeModules() {
        Collection<? extends Module> modules = Lookup.getDefault().lookupAll(Module.class);
        for (Module module : modules) {
            module.remove();
        }
    }

    public static void quitBeforeGuiWasCreated() {
        LOGGER.info("Quitting before the GUI was created.");
        shutdownRepository();
        AppStartupLock.unlock();
        System.exit(1);
    }

    private void quitVm() {
        CancelOtherTasks.cancel();
        shutdownRepository();
        appFrame.dispose();
        AppStartupLock.unlock();
        System.exit(0);
    }

    private void executeExitTasks() {
        for (AppExitTask task : Lookup.getDefault().lookupAll(AppExitTask.class)) {
            task.execute();
        }
    }

    private boolean ensureSerialTasksFinished() {
        SerialTaskExecutor executor = Lookup.getDefault().lookup(SerialTaskExecutor.class);
        boolean finished = executor.getTaskCount() <= 0;
        if (finished) {
            return true;
        }
        String message = Bundle.getString(AppLifeCycle.class, "AppLifeCycle.Confirm.QuitOnUserTasks");
        return MessageDisplayer.confirmYesNo(appFrame, message);
    }

    private void waitUntilAllSaveObjectsSaved() {
        long elapsedMilliseconds = 0;
        long terminationTimeoutMilliSeconds = 120 * 1000;
        long checkIntervalMilliSeconds = 2 * 1000;
        if (hasSaveObjects()) {
            LOGGER.log(Level.INFO, "Application waits until those objects have saved data: {0}", saveObjects);
            while (hasSaveObjects() && (elapsedMilliseconds < terminationTimeoutMilliSeconds)) {
                try {
                    elapsedMilliseconds += checkIntervalMilliSeconds;
                    Thread.sleep(checkIntervalMilliSeconds);
                } catch (Throwable t) {
                    Logger.getLogger(AppLifeCycle.class.getName()).log(Level.SEVERE, null, t);
                }
                if (elapsedMilliseconds >= terminationTimeoutMilliSeconds) {
                    String message = Bundle.getString(AppLifeCycle.class, "AppLifeCycle.Error.ExitDataNotSaved.MaxWaitTimeExceeded",
                            terminationTimeoutMilliSeconds / 1000);
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

    private void persistAppFrame() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        String key = appFrame.getClass().getName();
        prefs.setSize(key, appFrame);
        prefs.setLocation(key, appFrame);
    }

    private static void shutdownRepository() {
        Repository repo = Lookup.getDefault().lookup(Repository.class);
        repo.shutdown();
    }

    private static class CancelOtherTasks {

        /**
         * Sleep time in milliseconds before giving control to the caller, so that the threads can complete their
         * current action before they check for interruption.
         */
        private static final long MILLISECONDS_SLEEP = 2000;

        public static void cancel() {
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
            } catch (Throwable t) {
                Logger.getLogger(CancelOtherTasks.class.getName()).log(Level.SEVERE, null, t);
            }
        }

        private CancelOtherTasks() {
        }
    }

    private final class AppLifeCycleShutdownHook extends Thread {

        private AppLifeCycleShutdownHook() {
            super("JPhotoTagger: App life cycle shoudown Hook");
        }

        @Override
        public void run() {
            try {
                Repository repo = Lookup.getDefault().lookup(Repository.class);
                if (repo != null && repo.isInit()) {
                    Logger.getLogger(AppLifeCycleShutdownHook.class.getName()).severe("Database has not been shutdown; now shutting it down");
                    repo.shutdown();
                }
            } catch (Throwable t) {
                Logger.getLogger(AppLifeCycleShutdownHook.class.getName()).log(Level.SEVERE, null, t);
            }
        }

    }

    private AppLifeCycle() {
        Runtime.getRuntime().addShutdownHook(new AppLifeCycleShutdownHook());
    }
}
