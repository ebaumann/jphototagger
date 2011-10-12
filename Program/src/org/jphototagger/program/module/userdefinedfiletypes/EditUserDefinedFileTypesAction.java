package org.jphototagger.program.module.userdefinedfiletypes;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.AppLookAndFeel;

/**
 * @author Elmar Baumann
 */
public final class EditUserDefinedFileTypesAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

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
