package org.jphototagger.program.factory;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;

import org.jphototagger.api.branding.AppProperties;
import org.jphototagger.api.modules.Module;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.startup.AppUpdater;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Version;
import org.jphototagger.program.settings.AppPreferencesKeys;
import org.jphototagger.program.app.ui.AppWindowPersistence;
import org.jphototagger.program.app.update.UpdateDownload;

/**
 * Initalizes all other factories in the right order and sets the persistent
 * settings to the application's frame and panel.
 *
 * @author Elmar Baumann
 */
public final class MetaFactory implements Runnable {

    public static final MetaFactory INSTANCE = new MetaFactory();
    private Collection<? extends Module> modules;
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
        notifyUpdaters();
        checkForDownload();
    }

    private void notifyUpdaters() {
        Collection<? extends AppUpdater> appUpdaters = Lookup.getDefault().lookupAll(AppUpdater.class);
        AppProperties appProperties = Lookup.getDefault().lookup(AppProperties.class);
        Version version = Version.parseVersion(appProperties.getAppVersionString(), ".");
        int major = version.getMajor();
        int minor1 = version.getMinor1();
        int minor2 = version.getMinor2();

        for (AppUpdater appUpdater : appUpdaters) {
            appUpdater.updateToVersion(major, minor1, minor2);
        }
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
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(AppPreferencesKeys.KEY_CHECK_FOR_UPDATES)
                ? storage.getBoolean(AppPreferencesKeys.KEY_CHECK_FOR_UPDATES)
                : true;
    }

    private void installModules() {
        modules = Lookup.getDefault().lookupAll(Module.class);

        for (Module module : modules) {
            module.init();
        }
    }

    public Collection<Module> getModules() {
        return modules == null
                ? Collections.<Module>emptyList()
                : Collections.unmodifiableCollection(modules);
    }
}
