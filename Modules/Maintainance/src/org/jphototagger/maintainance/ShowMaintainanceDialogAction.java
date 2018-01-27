package org.jphototagger.maintainance;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.lib.api.AppIconProvider;
import org.jphototagger.lib.swing.util.MenuUtil;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ShowMaintainanceDialogAction extends AbstractAction implements MenuItemProvider {

    private static final long serialVersionUID = 1L;

    public ShowMaintainanceDialogAction() {
        super(Bundle.getString(ShowMaintainanceDialogAction.class, "ShowMaintainanceDialogAction.Name"));
        putValue(Action.SMALL_ICON, Lookup.getDefault().lookup(AppIconProvider.class).getIcon("icon_settings.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MaintainanceDialog dialog = new MaintainanceDialog();
        dialog.setVisible(true);
    }

    @Override
    public JMenuItem getMenuItem() {
        JMenuItem menuItem = new JMenuItem(this);
        MenuUtil.setMnemonics(menuItem);
        return menuItem;
    }

    @Override
    public boolean isSeparatorBefore() {
        return true;
    }

    @Override
    public int getPosition() {
        return 2;
    }
}
