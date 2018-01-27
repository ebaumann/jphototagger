package org.jphototagger.userdefinedfilters;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jphototagger.lib.api.AppIconProvider;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ShowUserDefinedFileFiltersDialogAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public ShowUserDefinedFileFiltersDialogAction() {
        super(Bundle.getString(ShowUserDefinedFileFiltersDialogAction.class, "ShowUserDefinedFileFiltersDialogAction.Name"));
        putValue(SMALL_ICON, Lookup.getDefault().lookup(AppIconProvider.class).getIcon("icon_filter.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        UserDefinedFileFiltersDialog dialog = new UserDefinedFileFiltersDialog();
        dialog.setVisible(true);
    }
}
