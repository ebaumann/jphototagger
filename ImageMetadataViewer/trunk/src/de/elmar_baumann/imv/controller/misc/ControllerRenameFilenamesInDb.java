package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.RenameFilenamesInDbDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

/**
 * Shows the dialog
 * {@link de.elmar_baumann.imv.view.dialogs.RenameFilenamesInDbDialog}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/16
 */
public final class ControllerRenameFilenamesInDb implements ActionListener {

    private final JMenuItem menuItem = GUI.INSTANCE.getAppFrame().
            getMenuItemRenameFilenamesInDb();

    public ControllerRenameFilenamesInDb() {
        listen();
    }

    private void listen() {
        menuItem.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RenameFilenamesInDbDialog dlg = new RenameFilenamesInDbDialog(
                GUI.INSTANCE.getAppFrame());
        dlg.setVisible(true);
    }
}
