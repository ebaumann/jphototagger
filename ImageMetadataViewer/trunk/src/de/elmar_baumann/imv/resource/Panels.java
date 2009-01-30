package de.elmar_baumann.imv.resource;

import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.imv.view.panels.AppPanel;

/**
 * Panels.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public final class Panels {

    private AppPanel appPanel;
    private AppFrame appFrame;
    private static final Panels instance = new Panels();

    public static Panels getInstance() {
        return instance;
    }

    public void setAppPanel(AppPanel panel) {
        appPanel = panel;
    }

    public void setAppFrame(AppFrame frame) {
        appFrame = frame;
    }

    public AppPanel getAppPanel() {
        return appPanel;
    }

    public AppFrame getAppFrame() {
        return appFrame;
    }
}
