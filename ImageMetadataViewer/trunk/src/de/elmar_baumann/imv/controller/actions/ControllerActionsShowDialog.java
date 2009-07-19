package de.elmar_baumann.imv.controller.actions;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.ActionsDialog;
import de.elmar_baumann.imv.view.frames.AppFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Displays the dialog {@link de.elmar_baumann.imv.view.dialogs.ActionsDialog}
 * when the menu item {@link AppFrame#getMenuItemActions()} was klicked or the
 * accelerator keys (F4) were pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-06
 */
public final class ControllerActionsShowDialog implements ActionListener {

    public ControllerActionsShowDialog() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppFrame().getMenuItemActions().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ActionsDialog dialog = ActionsDialog.INSTANCE;
        if (dialog.isVisible()) {
            dialog.toFront();
        } else {
            dialog.setVisible(true);
        }
    }
}
