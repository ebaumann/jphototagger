package org.jphototagger.program.module.keywords.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.misc.InputHelperDialog;

/**
 * Listens to the menu item {@code AppFrame#getMenuItemInputHelper()}
 * and shows the {@code InputHelperDialog} on action performed.
 *
 * @author Elmar Baumann
 */
public final class ShowKeywordsDialogController implements ActionListener {

    public ShowKeywordsDialogController() {
        listen();
    }

    private void listen() {
        GUI.getAppFrame().getMenuItemInputHelper().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        showDialog();
    }

    private void showDialog() {
        ComponentUtil.show(InputHelperDialog.INSTANCE);
    }
}
