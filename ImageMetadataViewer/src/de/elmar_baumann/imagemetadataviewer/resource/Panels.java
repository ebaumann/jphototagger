package de.elmar_baumann.imagemetadataviewer.resource;

import de.elmar_baumann.imagemetadataviewer.view.frames.AppFrame;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;

/**
 * Panels.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public class Panels {

    private static Panels instance = new Panels();
    private AppPanel appPanel;
    private AppFrame appFrame;

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
