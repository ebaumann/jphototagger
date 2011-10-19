package org.jphototagger.synonyms;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import javax.swing.KeyStroke;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public final class ShowSynonymsDialogAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public ShowSynonymsDialogAction() {
        super(Bundle.getString(ShowSynonymsDialogAction.class, "ShowSynonymsDialogAction.Name"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, 0));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        ComponentUtil.show(SynonymsDialog.INSTANCE);
    }
}
