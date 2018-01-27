package org.jphototagger.userdefinedfiletypes;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.jphototagger.lib.api.AppIconProvider;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class EditUserDefinedFileTypesAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public EditUserDefinedFileTypesAction() {
        super(Bundle.getString(EditUserDefinedFileTypesAction.class, "EditUserDefinedFileTypesAction.Name"));
        putValue(Action.SMALL_ICON, Lookup.getDefault().lookup(AppIconProvider.class).getIcon("icon_file.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        UserDefinedFileTypesDialog dlg = new UserDefinedFileTypesDialog();

        dlg.setVisible(true);
    }
}
