package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.lib.dialog.SystemOutputDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-05-31
 */
public final class ControllerShowSystemOutput implements ActionListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JButton buttonSystemOutput = appPanel.getButtonSystemOutput();

    public ControllerShowSystemOutput() {
        listen();
    }

    private void listen() {
        buttonSystemOutput.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showDialog();
    }

    private void showDialog() {
        SystemOutputDialog dlg = SystemOutputDialog.INSTANCE;
        if (dlg.isVisible()) {
            dlg.toFront();
        } else {
            dlg.setVisible(true);
        }
    }
}
