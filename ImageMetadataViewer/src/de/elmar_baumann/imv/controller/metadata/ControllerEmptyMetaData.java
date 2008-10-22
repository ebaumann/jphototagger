package de.elmar_baumann.imv.controller.metadata;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.MetadataEditPanelsArray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/22
 */
public class ControllerEmptyMetaData extends Controller implements ActionListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JButton buttonEmpty = appPanel.getButtonEmptyMetadata();
    private MetadataEditPanelsArray editPanels = appPanel.getEditPanelsArray();

    public ControllerEmptyMetaData() {
        buttonEmpty.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            editPanels.emptyPanels();
        }
    }
}
