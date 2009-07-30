package de.elmar_baumann.imv.controller.metadata;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.TextSelectionDialog;
import de.elmar_baumann.imv.view.frames.AppFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listens to the menu item {@link AppFrame#getMenuItemLastEditedWords()}
 * and shows the {@link TextSelectionDialog} on action performed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-30
 */
public final class ControllerShowTextSelectionDialog
        implements ActionListener {

    public ControllerShowTextSelectionDialog() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppFrame().getMenuItemLastEditedWords().
                addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showDialog();
    }

    private void showDialog() {
        // App frame's menu item text as title
        TextSelectionDialog.INSTANCE.setTitle(
                Bundle.getString("AppFrame.menuItemLastEditedWords.text")); // NOI18N
        TextSelectionDialog.INSTANCE.setVisible(true);
    }
}
