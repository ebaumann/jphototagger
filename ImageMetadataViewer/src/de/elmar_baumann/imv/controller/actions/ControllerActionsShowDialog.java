package de.elmar_baumann.imv.controller.actions;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.dialogs.ActionsDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Displays the dialog {@link de.elmar_baumann.imv.view.dialogs.ActionsDialog}. 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/06
 */
public class ControllerActionsShowDialog extends Controller implements ActionListener {

    public ControllerActionsShowDialog() {
        Panels.getInstance().getAppFrame().getMenuItemActions().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            ActionsDialog dialog = ActionsDialog.getInstance();
            if (dialog.isVisible()) {
                dialog.toFront();
            } else {
                dialog.setVisible(true);
            }
        }
    }
}
