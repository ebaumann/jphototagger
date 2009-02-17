package de.elmar_baumann.imv.controller.metadata;

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
public final class ControllerEmptyMetadata implements ActionListener {

    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final JButton buttonEmpty = appPanel.getButtonEmptyMetadata();
    private final EditMetadataPanelsArray editPanels = appPanel.getEditPanelsArray();

    public ControllerEmptyMetadata() {
        listen();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        editPanels.emptyPanels(true);
    }

    private void listen() {
        buttonEmpty.addActionListener(this);
    }
}
