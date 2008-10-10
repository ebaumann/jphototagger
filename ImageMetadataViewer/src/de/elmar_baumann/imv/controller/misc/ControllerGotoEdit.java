package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.MetadataEditPanelsArray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * Controls the action: go to the edit panel (set focus to first edit text
 * field).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/10
 */
public class ControllerGotoEdit extends Controller implements ActionListener {
    
    AppPanel appPanel = Panels.getInstance().getAppPanel();
    MetadataEditPanelsArray editPanels = appPanel.getEditPanelsArray();
    JTabbedPane tabbedPane = appPanel.getTabbedPaneMetadata();
    JPanel panelMetadataEdit = appPanel.getMetadataEditTab();

    public ControllerGotoEdit() {
        Panels.getInstance().getAppFrame().getMenuItemGotoEdit().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            selectEditPanel();
        }
    }

    private void selectEditPanel() {
        tabbedPane.setSelectedComponent(panelMetadataEdit);
        editPanels.setFocusToLastFocussedComponent();
    }
}
