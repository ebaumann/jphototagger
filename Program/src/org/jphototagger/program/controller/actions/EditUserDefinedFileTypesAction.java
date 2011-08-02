package org.jphototagger.program.controller.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.view.dialogs.UserDefinedFileTypesDialog;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class EditUserDefinedFileTypesAction extends AbstractAction {
    private static final long serialVersionUID = 3268061132637908576L;

    public EditUserDefinedFileTypesAction() {
        super(Bundle.getString(EditUserDefinedFileTypesAction.class, "EditUserDefinedFileTypesAction.Name"));
        putValue(Action.SMALL_ICON, AppLookAndFeel.getIcon("icon_user_defined_filetype.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        UserDefinedFileTypesDialog dlg = new UserDefinedFileTypesDialog();

        dlg.setVisible(true);
    }

}
