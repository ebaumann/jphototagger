package org.jphototagger.program.module.maintainance;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

import org.jphototagger.api.windows.MainWindowMenuItem;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MenuUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public final class ShowRepositoryMaintainanceDialogAction extends AbstractAction implements MainWindowMenuItem {

    private static final long serialVersionUID = 1L;

    public ShowRepositoryMaintainanceDialogAction() {
        super(Bundle.getString(ShowRepositoryMaintainanceDialogAction.class, "ShowRepositoryMaintainanceDialogAction.Name"));
        putValue(SMALL_ICON, IconUtil.getImageIcon(ShowRepositoryMaintainanceDialogAction.class, "repository.png"));
        putValue(ACCELERATOR_KEY, KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_D));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        ComponentUtil.show(RepositoryMaintainanceDialog.INSTANCE);
    }

    @Override
    public JMenuItem getMenuItem() {
        JMenuItem menuItem = new JMenuItem(this);
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
