package org.jphototagger.findduplicates;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.jphototagger.lib.api.AppIconProvider;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class FindDuplicatesAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private static FindDuplicatesDialog dialog;

    FindDuplicatesAction() {
        super(Bundle.getString(FindDuplicatesAction.class, "FindDuplicatesAction.Name"));
        putValue(Action.SMALL_ICON, Lookup.getDefault().lookup(AppIconProvider.class).getIcon("icon_duplicates.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        createDialog();
        dialog.setVisible(true);
    }

    private static synchronized void createDialog() {
        if (dialog == null) {
             dialog = new FindDuplicatesDialog();
        }
    }
}
