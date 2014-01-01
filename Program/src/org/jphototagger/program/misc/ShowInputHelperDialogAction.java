package org.jphototagger.program.misc;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public final class ShowInputHelperDialogAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public ShowInputHelperDialogAction() {
        super(Bundle.getString(ShowInputHelperDialogAction.class, "ShowInputHelperDialogAction.Name"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));
        putValue(SMALL_ICON, IconUtil.getImageIcon(ShowInputHelperDialogAction.class, "inputhelper.png"));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        ComponentUtil.show(InputHelperDialog.INSTANCE);
    }
}
