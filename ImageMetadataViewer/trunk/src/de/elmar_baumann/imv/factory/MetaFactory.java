package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.lib.persistence.PersistentComponentSizes;
import de.elmar_baumann.lib.persistence.PersistentSettings;
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

    @Override
    public void run() {
        init();
    }

    synchronized private void init() {
        Util.checkInit(MetaFactory.class, init);
        if (!init) {
            init = true;
            readPersistentAppFrame();
            startDisplayProgressInProgressbarBar();
            LateConnectionsFactory.INSTANCE.init();
            ModelFactory.INSTANCE.init();
            ActionListenerFactory.INSTANCE.init();
            MouseListenerFactory.INSTANCE.init();
            RendererFactory.INSTANCE.init();
            ControllerFactory.INSTANCE.init();
            readPersistentAppPanel();
            stopDisplayProgressInProgressbarBar();
        }
    }

    private void readPersistentAppFrame() {
        AppFrame appFrame = Panels.getInstance().getAppFrame();
        PersistentComponentSizes.getSizeAndLocation(appFrame);
        appFrame.pack();
    }

    private void readPersistentAppPanel() {
        AppPanel appPanel = Panels.getInstance().getAppPanel();
        PersistentSettings.getInstance().getComponent(
                appPanel,
                appPanel.getPersistentSettingsHints());
    }

    private void startDisplayProgressInProgressbarBar() {
        JProgressBar progressbar = Panels.getInstance().getAppPanel().getProgressBarCreateMetadataOfCurrentThumbnails();
        progressbar.setStringPainted(true);
        progressbar.setString(Bundle.getString("MetaFactory.Message.Init"));
        progressbar.setIndeterminate(true);
    }

    private void stopDisplayProgressInProgressbarBar() {
        JProgressBar progressbar = Panels.getInstance().getAppPanel().getProgressBarCreateMetadataOfCurrentThumbnails();
        progressbar.setIndeterminate(false);
        progressbar.setString(""); // NOI18N
        progressbar.setStringPainted(false);
    }
}
