package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.imv.view.panels.AppPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 *
 * @author  Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-16
 */
public final class ControllerToggleKeywordOverlay implements ActionListener {

    private final AppFrame appFrame = GUI.INSTANCE.getAppFrame();

    public ControllerToggleKeywordOverlay() {
        listen();
    }

    private void listen() {
        appFrame.getCheckBoxMenuItemKeywordOverlay().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        toggleKeywordOverlay();
    }

    private void toggleKeywordOverlay() {
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();
        boolean active = !appPanel.getPanelThumbnails().isKeywordsOverlay();
        appPanel.getPanelThumbnails().setKeywordsOverlay(active);
        appFrame.getCheckBoxMenuItemKeywordOverlay().setSelected(active);
    }
}
