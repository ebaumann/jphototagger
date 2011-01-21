package org.jphototagger.program.controller.actions;

import org.jphototagger.program.view.dialogs.ActionsDialog;
import org.jphototagger.program.view.frames.AppFrame;
import org.jphototagger.lib.componentutil.ComponentUtil;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 * Displays the dialog {@link ActionsDialog} when the menu item
 * {@link AppFrame#getMenuItemActions()} was klicked or the accelerator key
 * F4 was pressed.
 *
 * @author Elmar Baumann
 */
public final class ShowActionDialogAction extends AbstractAction {

    private static final long serialVersionUID = -1527748280703337890L;

    @Override
    public void actionPerformed(ActionEvent evt) {
        ComponentUtil.show(ActionsDialog.INSTANCE);
    }
}
