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
import org.jphototagger.api.storage.Storage;
import org.jphototagger.api.modules.Module;
import org.jphototagger.domain.event.AppWillExitEvent;
import org.jphototagger.domain.event.listener.ListenerSupport;
import org.jphototagger.domain.repository.RepositoryMaintainance;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.factory.MetaFactory;
import org.jphototagger.program.helper.Cleanup;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.tasks.UserTasks;
import org.jphototagger.program.view.frames.AppFrame;
import org.openide.util.Lookup;

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
     * Has be to called <em>once</em> after the {@link AppFrame} has been
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
     * <em>At this time, the database has been shutdown, so that database
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
     * {@link #removeSaveObject(java.lang.Object)}, otherwise the app terminates
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
     * Removes a save object added via {@link #addSaveObject(java.lang.Object)}.
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
        appFrame.getMenuItemExit().addActionListener(new java.awt.event.ActionListener() {

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
            EventBus.publish(new AppWillExitEvent(this));
            notifyModulesForClose();
            writeProperties();
            checkDataToSave();
            Cleanup.shutdown();
            RepositoryMaintainance repo = Lookup.getDefault().lookup(RepositoryMaintainance.class);

            repo.shutdownRepository();

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
            module.close();
        }
    }

    public static void quitBeforeGuiWasCreated() {
        Lookup.getDefault().lookup(RepositoryMaintainance.class).shutdownRepository();
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

            GUI.getAppFrame().setEnabled(false);

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
        Storage storage = Lookup.getDefault().lookup(Storage.class);
        String key = appFrame.getClass().getName();

        storage.setSize(key, appFrame);
        storage.setLocation(key, appFrame);
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
}
