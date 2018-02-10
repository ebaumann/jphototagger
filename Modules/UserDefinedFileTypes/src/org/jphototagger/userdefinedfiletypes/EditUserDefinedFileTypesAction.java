package org.jphototagger.userdefinedfiletypes;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public final class EditUserDefinedFileTypesAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public EditUserDefinedFileTypesAction() {
        super(Bundle.getString(EditUserDefinedFileTypesAction.class, "EditUserDefinedFileTypesAction.Name"));
        putValue(Action.SMALL_ICON, org.jphototagger.resources.Icons.getIcon("icon_file.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        UserDefinedFileTypesDialog dlg = new UserDefinedFileTypesDialog();

        dlg.setVisible(true);
    }
}
