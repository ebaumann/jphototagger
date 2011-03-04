package org.jphototagger.program.factory;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppWindowPersistence;
import org.jphototagger.program.app.update.UpdateDownload;
import org.jphototagger.program.tasks.ScheduledTaskBackupDatabase;
import org.jphototagger.program.UserSettings;

import java.awt.EventQueue;

/**
 * Initalizes all other factories in the right order and sets the persistent
 * settings to the application's frame and panel.
 *
 * @author Elmar Baumann
 */
public final class MetaFactory implements Runnable {
    public static final MetaFactory INSTANCE = new MetaFactory();
    private boolean init = false;

    @Override
    public void run() {
        init();
    }

    private void init() {
        synchronized (this) {
            if (!Support.checkInit(getClass(), init)) {
                return;
            }

            init = true;
        }

        final AppWindowPersistence appPersistence = new AppWindowPersistence();

        appPersistence.readAppFrameFromProperties();
        ModelFactory.INSTANCE.init();
        RendererFactory.INSTANCE.init();
        ControllerFactory.INSTANCE.init();
        ActionKeyListenerFactory.INSTANCE.init();
        MouseListenerFactory.INSTANCE.init();

        // No other factory after:
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                TerminateFactory.INSTANCE.init();
                appPersistence.readAppPanelFromProperties();
            }
        });
        checkForDownload();
        ScheduledTaskBackupDatabase.INSTANCE.setBackup();
    }

    private void checkForDownload() {
        UpdateDownload.askOnceCheckForNewerVersion();

        if (UserSettings.INSTANCE.isAutoDownloadNewerVersions()) {

            // Returning immediately
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(60 * 1000);
                        UpdateDownload.checkForNewerVersion();
                    } catch (Exception ex) {
                        AppLogger.logSevere(getClass(), ex);
                    }
                }
            }, "JPhotoTagger: Checking for a newer version").start();
        }
    }
}
