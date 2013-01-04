package org.jphototagger.findduplicates;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public final class FindDuplicatesAction extends AbstractAction implements MenuItemProvider {

    static final FindDuplicatesAction INSTANCE = new FindDuplicatesAction();
    private static final long serialVersionUID = 1L;
    public static final JMenuItem MENU_ITEM = new JMenuItem(new FindDuplicatesAction());

    private FindDuplicatesAction() {
        super(Bundle.getString(FindDuplicatesAction.class, "FindDuplicatesAction.Name"));
        putValue(Action.SMALL_ICON, IconUtil.getImageIcon(FindDuplicatesAction.class, "icon.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FindDuplicatesDialog.INSTANCE.setVisible(true);
    }

    @Override
    public JMenuItem getMenuItem() {
        return MENU_ITEM;
    }

    @Override
    public int getPosition() {
        return 20000;
    }

    @Override
    public boolean isSeparatorBefore() {
        return false;
    }
}
