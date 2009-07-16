package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.lib.comparator.FileSort;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

/**
 * 
 *
 * @author  Martin Pohlack <martinp@gmx.de>
 * @version 2009/07/16
 */
public final class ControllerToggleKeywordOverlay implements ActionListener {

    private final ImageFileThumbnailsPanel thumbnailsPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final AppFrame appFrame = GUI.INSTANCE.getAppFrame();

    public ControllerToggleKeywordOverlay() {
        listen();
    }

    private void listen() {
        appFrame.getMenuItemKeywordOverlay().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        appFrame.toggleKeywordOverlay();
    }
}
