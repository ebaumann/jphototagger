package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.lib.persistence.PersistentAppSizes;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import javax.swing.JProgressBar;

/**
 * Factory mit Kenntnis über alle Factories. Erzeugt diese in der richtigen
 * Reihenfolge.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public class MetaFactory extends Thread {

    private static MetaFactory instance = new MetaFactory();

    public static MetaFactory getInstance() {
        return instance;
    }

    synchronized public void stopController() {
        ControllerFactory.getInstance().setControl(false);
    }

    private MetaFactory() {
        start();
    }

    @Override
    public void run() {
        createFactories();
    }

    synchronized private void createFactories() {
        setAppFrame();
        showProgress();
        LateConnectionsFactory.getInstance();
        ModelFactory.getInstance();
        ControllerFactory.getInstance();
        ActionListenerFactory.getInstance();
        MouseListenerFactory.getInstance();
        RendererFactory.getInstance();
        ControllerFactory.getInstance().setControl(true);
        setAppPanel();
        stopProgress();
    }

    private void setAppPanel() {
        AppPanel appPanel = Panels.getInstance().getAppPanel();
        PersistentSettings.getInstance().getComponent(
            appPanel,
            appPanel.getPersistentSettingsHints());
    }

    private void setAppFrame() {
        AppFrame appFrame = Panels.getInstance().getAppFrame();
        PersistentAppSizes.getSizeAndLocation(appFrame);
        appFrame.pack();
    }

    private void showProgress() {
        JProgressBar progressbar = Panels.getInstance().getAppPanel().getProgressBarCreateMetaDataOfCurrentThumbnails();
        progressbar.setStringPainted(true);
        progressbar.setString(Bundle.getString("MetaFactory.Message.Init"));
        progressbar.setIndeterminate(true);
    }

    private void stopProgress() {
        JProgressBar progressbar = Panels.getInstance().getAppPanel().getProgressBarCreateMetaDataOfCurrentThumbnails();
        progressbar.setIndeterminate(false);
        progressbar.setString(""); // NOI18N
        progressbar.setStringPainted(false);
    }
}
