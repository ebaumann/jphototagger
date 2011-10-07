package org.jphototagger.program.module.synonyms;

import org.jphototagger.program.module.synonyms.SynonymsDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.program.resource.GUI;

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
