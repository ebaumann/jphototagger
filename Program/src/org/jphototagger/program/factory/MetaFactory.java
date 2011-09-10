package org.jphototagger.program.factory;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.api.core.Storage;
import org.jphototagger.api.modules.Module;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.app.AppWindowPersistence;
import org.jphototagger.program.app.update.UpdateDownload;
import org.openide.util.Lookup;

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
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                TerminateFactory.INSTANCE.init();
                appPersistence.readAppPanelFromProperties();
            }
        });
        installModules();
        checkForDownload();
    }

    private void checkForDownload() {
        UpdateDownload.askOnceCheckForNewerVersion();
        if (isCheckForUpdates()) {

            // Returning immediately
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(60 * 1000);
                        UpdateDownload.checkForNewerVersion();
                    } catch (Exception ex) {
                        Logger.getLogger(MetaFactory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }, "JPhotoTagger: Checking for a newer version").start();
        }
    }

    private boolean isCheckForUpdates() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        return storage.containsKey(Storage.KEY_CHECK_FOR_UPDATES)
                ? storage.getBoolean(Storage.KEY_CHECK_FOR_UPDATES)
                : true;
    }

    private void installModules() {
        Collection<? extends Module> modules = Lookup.getDefault().lookupAll(Module.class);

        for (Module module : modules) {
            module.start();
        }
    }
}
