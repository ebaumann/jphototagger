package org.jphototagger.program.controller.keywords.tree;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.frames.AppFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listens to the menu item {@link AppFrame#getMenuItemInputHelper()}
 * and shows the {@link InputHelperDialog} on action performed.
 *
 * @author Elmar Baumann
 */
public final class ControllerShowKeywordsDialog implements ActionListener {
    public ControllerShowKeywordsDialog() {
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
