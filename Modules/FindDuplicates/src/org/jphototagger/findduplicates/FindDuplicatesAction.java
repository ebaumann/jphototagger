package org.jphototagger.findduplicates;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public final class FindDuplicatesAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private static FindDuplicatesDialog dialog;

    FindDuplicatesAction() {
        super(Bundle.getString(FindDuplicatesAction.class, "FindDuplicatesAction.Name"));
        putValue(Action.SMALL_ICON, IconUtil.getImageIcon(FindDuplicatesAction.class, "icon.png"));
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
