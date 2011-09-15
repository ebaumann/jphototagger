package org.jphototagger.program.controller.misc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.SynonymsDialog;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ShowSynonymsDialogController implements ActionListener {

    public ShowSynonymsDialogController() {
        listen();
    }

    private void listen() {
        GUI.getAppFrame().getMenuItemSynonyms().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        ComponentUtil.show(SynonymsDialog.INSTANCE);
    }
}
