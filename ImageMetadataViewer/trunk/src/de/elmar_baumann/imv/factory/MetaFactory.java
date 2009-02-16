package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.lib.persistence.PersistentComponentSizes;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import javax.swing.JProgressBar;

/**
 * Factory mit Kenntnis über alle Factories. Erzeugt diese in der richtigen
 * Reihenfolge.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public final class MetaFactory extends Thread {

    public static final MetaFactory INSTANCE = new MetaFactory();
    private boolean init = false;

    synchronized public void stopController() {
        ControllerFactory.INSTANCE.setControl(false);
    }

    private MetaFactory() {
        start();
    }

    @Override
    public void run() {
        init();
    }

    synchronized public void init() {
        if (!init) {
            init = true;
            setAppFrame();
            showProgress();
            LateConnectionsFactory.INSTANCE.init();
            ModelFactory.INSTANCE.init();
            ControllerFactory.INSTANCE.init();
            ActionListenerFactory.INSTANCE.init();
            MouseListenerFactory.INSTANCE.init();
            RendererFactory.INSTANCE.init();
            ControllerFactory.INSTANCE.setControl(true);
            setAppPanel();
            stopProgress();
        }
    }

    private void setAppPanel() {
        AppPanel appPanel = Panels.getInstance().getAppPanel();
        PersistentSettings.getInstance().getComponent(
                appPanel,
                appPanel.getPersistentSettingsHints());
    }

    private void setAppFrame() {
        AppFrame appFrame = Panels.getInstance().getAppFrame();
        PersistentComponentSizes.getSizeAndLocation(appFrame);
        appFrame.pack();
    }

    private void showProgress() {
        JProgressBar progressbar = Panels.getInstance().getAppPanel().getProgressBarCreateMetadataOfCurrentThumbnails();
        progressbar.setStringPainted(true);
        progressbar.setString(Bundle.getString("MetaFactory.Message.Init"));
        progressbar.setIndeterminate(true);
    }

    private void stopProgress() {
        JProgressBar progressbar = Panels.getInstance().getAppPanel().getProgressBarCreateMetadataOfCurrentThumbnails();
        progressbar.setIndeterminate(false);
        progressbar.setString(""); // NOI18N
        progressbar.setStringPainted(false);
    }
}
