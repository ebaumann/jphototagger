package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.ProgressBarCreateMetadataOfCurrentThumbnails;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.imv.view.panels.AppPanel;
import javax.swing.JProgressBar;

/**
 * Initalizes all other factories in the right order and sets the persistent
 * settings to the application's frame and panel.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public final class MetaFactory implements Runnable {

    public static final MetaFactory INSTANCE = new MetaFactory();
    private boolean init = false;
    private final ProgressBarCreateMetadataOfCurrentThumbnails progressBarProvider =
            ProgressBarCreateMetadataOfCurrentThumbnails.INSTANCE;

    @Override
    public void run() {
        init();
    }

    private synchronized void init() {
        Util.checkInit(MetaFactory.class, init);
        init = true;
        readAppFrameFromProperties();
        startDisplayProgressInProgressbarBar();
        ControllerFactory.INSTANCE.init();
        LateConnectionsFactory.INSTANCE.init();
        ModelFactory.INSTANCE.init();
        ActionListenerFactory.INSTANCE.init();
        MouseListenerFactory.INSTANCE.init();
        RendererFactory.INSTANCE.init();
        readAppPanelFromProperties();
        stopDisplayProgressInProgressbarBar();
    }

    private void readAppFrameFromProperties() {
        AppFrame appFrame = GUI.INSTANCE.getAppFrame();
        UserSettings.INSTANCE.getSettings().getSizeAndLocation(appFrame);
        appFrame.pack();
    }

    private void readAppPanelFromProperties() {
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();
        UserSettings.INSTANCE.getSettings().getComponent(
                appPanel,
                appPanel.getPersistentSettingsHints());
    }

    private void startDisplayProgressInProgressbarBar() {
        JProgressBar progressbar = (JProgressBar) progressBarProvider.
                getResource(this);
        if (progressbar != null) {
            progressbar.setStringPainted(true);
            progressbar.setString(Bundle.getString(
                    "MetaFactory.Message.Init"));
            progressbar.setIndeterminate(true);
            progressBarProvider.releaseResource(this);
        }
    }

    private void stopDisplayProgressInProgressbarBar() {
        JProgressBar progressbar = (JProgressBar) progressBarProvider.
                getResource(this);
        if (progressbar != null) {
            progressbar.setIndeterminate(false);
            progressbar.setString(""); // NOI18N
            progressbar.setStringPainted(false);
            progressBarProvider.releaseResource(this);
        }
    }
}
