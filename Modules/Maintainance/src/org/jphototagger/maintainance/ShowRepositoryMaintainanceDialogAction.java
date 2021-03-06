package org.jphototagger.maintainance;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MenuUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public final class ShowRepositoryMaintainanceDialogAction extends AbstractAction implements MenuItemProvider {

    private static final long serialVersionUID = 1L;

    public ShowRepositoryMaintainanceDialogAction() {
        super(Bundle.getString(ShowRepositoryMaintainanceDialogAction.class, "ShowRepositoryMaintainanceDialogAction.Name"));
        putValue(SMALL_ICON, org.jphototagger.resources.Icons.getIcon("icon_database.png"));
        putValue(ACCELERATOR_KEY, KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_D));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        ComponentUtil.show(RepositoryMaintainanceDialog.INSTANCE);
    }

    @Override
    public JMenuItem getMenuItem() {
        JMenuItem menuItem = UiFactory.menuItem(this);
        MenuUtil.setMnemonics(menuItem);
        return menuItem;
    }

    @Override
    public boolean isSeparatorBefore() {
        return false;
    }

    @Override
    public int getPosition() {
        return 1;
    }
}
