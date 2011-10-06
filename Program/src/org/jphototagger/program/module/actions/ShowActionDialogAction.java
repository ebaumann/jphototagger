package org.jphototagger.program.module.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.jphototagger.lib.componentutil.ComponentUtil;

/**
 * Displays the dialog {@code ActionsDialog} when the menu item
 * {@code AppFrame#getMenuItemActions()} was klicked or the accelerator key
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
