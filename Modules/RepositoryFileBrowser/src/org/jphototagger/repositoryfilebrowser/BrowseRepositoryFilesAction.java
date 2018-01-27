package org.jphototagger.repositoryfilebrowser;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.lib.api.AppIconProvider;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class BrowseRepositoryFilesAction extends AbstractAction implements MenuItemProvider {

    private static final long serialVersionUID = 1L;
    public static final BrowseRepositoryFilesAction INSTANCE = new BrowseRepositoryFilesAction();
    public static final JMenuItem MENU_ITEM = new JMenuItem(new BrowseRepositoryFilesAction());

    private BrowseRepositoryFilesAction() {
        super(Bundle.getString(BrowseRepositoryFilesAction.class, "DisplayFileBrowserAction.Name"));
        putValue(Action.SMALL_ICON, Lookup.getDefault().lookup(AppIconProvider.class).getIcon("icon_database.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RepositoryFileBrowserDialog dialog = new RepositoryFileBrowserDialog(null);
        dialog.setVisible(true);
    }

    @Override
    public JMenuItem getMenuItem() {
        return MENU_ITEM;
    }

    @Override
    public int getPosition() {
        return 10000;
    }

    @Override
    public boolean isSeparatorBefore() {
        return true;
    }
}
