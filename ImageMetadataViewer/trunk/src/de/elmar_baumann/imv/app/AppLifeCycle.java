package de.elmar_baumann.imv.app;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.database.DatabaseMaintainance;
import de.elmar_baumann.imv.event.listener.AppExitListener;
import de.elmar_baumann.imv.factory.MetaFactory;
import de.elmar_baumann.imv.helper.Cleanup;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.frames.AppFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JFrame;

/**
 * Life cycle of the application.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-30
 */
public final class AppLifeCycle {

    public static AppLifeCycle INSTANCE = new AppLifeCycle();
    private final Set<Object> saveObjects =
            Collections.synchronizedSet(new HashSet<Object>());
    private final List<AppExitListener> exitListeners =
            new ArrayList<AppExitListener>();
    private AppFrame appFrame;
    private boolean started;

    /**
     * Has be to call <em>once</em> after the {@link AppFrame} has been created.
     *
     * @param appFrame the application's frame
     */
    public synchronized void started(AppFrame appFrame) {
        assert !started;
        if (!started) {
            this.appFrame = appFrame;
            Thread thread = new Thread(MetaFactory.INSTANCE);
            thread.setName("Initializing meta factory" + " @ " + // NOI18N
                    getClass().getName());
            thread.start();
            addAppExitListener(appFrame.getAppPanel());
            listenForQuit();
        }
    }

    /**
     * Sets whether an object saving data. As long as data is to save, the app
     * does not exit.
     *
     * <em>Do not forget removing a save object via
     * {@link #removeSaveObject(java.lang.Object)}, otherwise the app terminates
     * after a timeout and does not know wheter data was saved!</em>
     *
     * @param saveObject object that saves data.
     */
    public void addSaveObject(Object saveObject) {
        saveObjects.add(saveObject);
    }

    /**
     * Removes a save object added via {@link #addSaveObject(java.lang.Object)}.
     *
     * @param saveObject save object to remove
     */
    public void removeSaveObject(Object saveObject) {
        saveObjects.remove(saveObject);
    }

    /**
     * Adds a listener to notify when the application will exit.
     *
     * @param listener listener
     */
    public void addAppExitListener(AppExitListener listener) {
        synchronized (exitListeners) {
            exitListeners.add(listener);
        }
    }

    /**
     * Removes a listener added by
     * {@link #addAppExitListener(de.elmar_baumann.imv.event.listener.AppExitListener)}.
     *
     * @param listener listener
     */
    public void removeAppExitListener(AppExitListener listener) {
        synchronized (exitListeners) {
            exitListeners.remove(listener);
        }
    }

    private void notifyExitListeners() {
        synchronized (exitListeners) {
            for (AppExitListener listener : exitListeners) {
                listener.appWillExit();
            }
        }
    }

    private void listenForQuit() {
        appFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
     * Disposes the {@link AppFrame} and exits the VM.
     */
    public void quit() {
        notifyExitListeners();
        writeProperties();
        checkDataToSave();
        Cleanup.shutdown();
        DatabaseMaintainance.INSTANCE.shutdown();
        appFrame.dispose();
        AppLock.unlock();
        System.exit(0);
    }

    private void checkDataToSave() {
        long elapsedMilliseconds = 0;
        long timeoutMilliSeconds = 120 * 1000;
        long checkIntervalMilliSeconds = 2000;
        if (saveObjects.size() > 0) {
            AppLog.logInfo(getClass(), Bundle.getString(
                    "AppLifeCycle.Info.SaveObjectsExisting", saveObjects)); // NOI18N
            while (saveObjects.size() > 0 &&
                    elapsedMilliseconds < timeoutMilliSeconds) {
                try {
                    elapsedMilliseconds += checkIntervalMilliSeconds;
                    Thread.sleep(checkIntervalMilliSeconds);
                } catch (InterruptedException ex) {
                    AppLog.logSevere(getClass(), ex);
                }
                if (elapsedMilliseconds >= timeoutMilliSeconds) {
                    MessageDisplayer.error(appFrame,
                            "AppLifeCycle.Error.ExitDataNotSaved.MaxWaitTimeExceeded", // NOI18N
                            timeoutMilliSeconds);
                }
            }
        }
    }

    private void writeProperties() {
        UserSettings.INSTANCE.getSettings().setSizeAndLocation(appFrame);
        UserSettings.INSTANCE.writeToFile();
    }

    private AppLifeCycle() {
    }
}
