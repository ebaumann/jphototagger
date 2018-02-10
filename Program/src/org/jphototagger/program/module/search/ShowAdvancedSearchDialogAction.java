package org.jphototagger.program.module.search;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.Icons;

/**
 * @author Elmar Baumann
 */
public final class ShowAdvancedSearchDialogAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public ShowAdvancedSearchDialogAction() {
        super(Bundle.getString(ShowAdvancedSearchDialogAction.class, "ShowAdvancedSearchDialogAction.Name"));
        putValue(SMALL_ICON, Icons.getIcon("icon_search.png"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
    }

    @Override
    public void actionPerformed(ActionEvent ignored) {
        ComponentUtil.show(AdvancedSearchDialog.INSTANCE);
    }
}
