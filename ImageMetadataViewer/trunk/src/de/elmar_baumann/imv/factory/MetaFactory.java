package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.ProgressBarAutomaticTasks;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.imv.view.panels.AppPanel;
import javax.swing.JProgressBar;

/**
 * Initalizes all other factories in the right order and sets the persistent
 * settings to the application's frame and panel.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-29
 */
public final class MetaFactory implements Runnable {

    public static final MetaFactory INSTANCE = new MetaFactory();
    private boolean init = false;
    private final ProgressBarAutomaticTasks progressBarRessource =
            ProgressBarAutomaticTasks.INSTANCE;

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
        MiscFactory.INSTANCE.init();
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
        JProgressBar progressBar = progressBarRessource.getResource(this);
        if (progressBar == null) {
            AppLog.logInfo(getClass(), "ProgressBar.Locked", getClass(), // NOI18N
                    progressBarRessource.getOwner());
        } else {
            progressBar.setStringPainted(true);
            progressBar.setString(Bundle.getString("MetaFactory.Init")); // NOI18N
            progressBar.setIndeterminate(true);
            progressBarRessource.releaseResource(this);
        }
    }

    private void stopDisplayProgressInProgressbarBar() {
        JProgressBar progressBar = progressBarRessource.getResource(this);
        if (progressBar != null) {
            progressBar.setIndeterminate(false);
            progressBar.setString(""); // NOI18N
            progressBar.setStringPainted(false);
            progressBarRessource.releaseResource(this);
        }
    }
}
