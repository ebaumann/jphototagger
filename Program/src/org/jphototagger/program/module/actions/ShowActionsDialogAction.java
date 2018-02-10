package org.jphototagger.program.module.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.resources.Icons;

/**
 * @author Elmar Baumann
 */
public final class ShowActionsDialogAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public ShowActionsDialogAction() {
        super(Bundle.getString(ShowActionsDialogAction.class, "ShowActionsDialogAction.Name"));
        putValue(SMALL_ICON, Icons.getIcon("icon_action.png"));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        ComponentUtil.show(ActionsDialog.INSTANCE);
    }
}
