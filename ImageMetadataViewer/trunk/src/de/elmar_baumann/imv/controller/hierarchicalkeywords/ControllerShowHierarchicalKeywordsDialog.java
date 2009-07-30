package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.HierarchicalKeywordsDialog;
import de.elmar_baumann.imv.view.frames.AppFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listens to the menu item {@link AppFrame#getMenuItemHierarchicalKeywords()}
 * and shows the {@link HierarchicalKeywordsDialog} on action performed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-30
 */
public final class ControllerShowHierarchicalKeywordsDialog
        implements ActionListener {

    public ControllerShowHierarchicalKeywordsDialog() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppFrame().getMenuItemHierarchicalKeywords().
                addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showDialog();
    }

    private void showDialog() {
        HierarchicalKeywordsDialog dlg = HierarchicalKeywordsDialog.INSTANCE;
        if (dlg.isVisible()) {
            dlg.toFront();
        } else {
            dlg.setVisible(true);
        }
    }
}
