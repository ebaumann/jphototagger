package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-26
 */
public final class ControllerHidePanels implements ActionListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JMenuItem itemSelectionPanel =
            GUI.INSTANCE.getAppFrame().getMenuItemHideSelectionPanel();
    private final JMenuItem itemMetdataPanel =
            GUI.INSTANCE.getAppFrame().getMenuItemHideMetadataPanel();

    public ControllerHidePanels() {
        listen();
        setItemTexts();
    }

    private void listen() {
        itemSelectionPanel.addActionListener(this);
        itemMetdataPanel.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == itemMetdataPanel) {
            appPanel.toggleHideMetadataPanel();
        } else if (e.getSource() == itemSelectionPanel) {
            appPanel.toggleHideSelectionPanel();
        }
        setItemTexts();
    }

    private void setItemTexts() {
        itemSelectionPanel.setText(appPanel.isSelectionPanelHidden()
                                   ? Bundle.getString(
                "MenuItemHideSelectionPanel.DisplayName.Hidden")
                                   : Bundle.getString(
                "MenuItemHideSelectionPanel.DisplayName.Visible"));
        itemMetdataPanel.setText(appPanel.isMetadataPanelHidden()
                                 ? Bundle.getString(
                "MenuItemHideMetadataPanel.DisplayName.Hidden")
                                 : Bundle.getString(
                "MenuItemHideMetadataPanel.DisplayName.Visible"));
    }
}
