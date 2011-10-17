package org.jphototagger.maintainance;

import java.awt.event.ActionEvent;

import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;

import javax.swing.JMenuItem;
import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MenuUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public final class ShowUpdateMetadataOfDirectoriesDialogAction extends AbstractAction implements MenuItemProvider {

    private static final long serialVersionUID = 1L;

    public ShowUpdateMetadataOfDirectoriesDialogAction() {
        super(Bundle.getString(ShowUpdateMetadataOfDirectoriesDialogAction.class, "ShowUpdateMetadataOfDirectoriesDialogAction.Name"));
        putValue(SMALL_ICON, IconUtil.getImageIcon(ShowUpdateMetadataOfDirectoriesDialogAction.class, "folder.png"));
        putValue(ACCELERATOR_KEY, KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_R));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        ComponentUtil.show(UpdateMetadataOfDirectoriesDialog.INSTANCE);
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
        return 0;
    }
}
