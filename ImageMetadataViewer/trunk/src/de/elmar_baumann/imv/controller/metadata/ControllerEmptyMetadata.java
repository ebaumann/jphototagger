package de.elmar_baumann.imv.controller.metadata;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/22
 */
public final class ControllerEmptyMetadata implements ActionListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JButton buttonEmpty = appPanel.getButtonEmptyMetadata();
    private final EditMetadataPanelsArray editPanels = appPanel.
            getEditPanelsArray();

    public ControllerEmptyMetadata() {
        listen();
    }

    private void listen() {
        buttonEmpty.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                editPanels.emptyPanels(true);
            }
        });
    }
}
