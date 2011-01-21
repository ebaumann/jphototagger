package org.jphototagger.program.controller.misc;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.SynonymsDialog;
import org.jphototagger.lib.componentutil.ComponentUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerShowSynonymsDialog implements ActionListener {
    public ControllerShowSynonymsDialog() {
        listen();
    }

    private void listen() {
        GUI.getAppFrame().getMenuItemSynonyms().addActionListener(
            this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        ComponentUtil.show(SynonymsDialog.INSTANCE);
    }
}
