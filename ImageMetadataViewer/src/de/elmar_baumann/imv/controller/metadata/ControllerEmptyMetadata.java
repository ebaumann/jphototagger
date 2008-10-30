package de.elmar_baumann.imv.controller.metadata;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/22
 */
public class ControllerEmptyMetadata extends Controller implements ActionListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JButton buttonEmpty = appPanel.getButtonEmptyMetadata();
    private EditMetadataPanelsArray editPanels = appPanel.getEditPanelsArray();

    public ControllerEmptyMetadata() {
        buttonEmpty.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            editPanels.emptyPanels(true);
        }
    }
}
