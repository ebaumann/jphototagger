package org.jphototagger.userdefinedfilters;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public final class ShowUserDefinedFileFiltersDialogAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public ShowUserDefinedFileFiltersDialogAction() {
        super(Bundle.getString(ShowUserDefinedFileFiltersDialogAction.class, "ShowUserDefinedFileFiltersDialogAction.Name"));
        putValue(SMALL_ICON, IconUtil.getImageIcon(ShowUserDefinedFileFiltersDialogAction.class, "filter.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        UserDefinedFileFiltersDialog dialog = new UserDefinedFileFiltersDialog();
        dialog.setVisible(true);
    }
}
