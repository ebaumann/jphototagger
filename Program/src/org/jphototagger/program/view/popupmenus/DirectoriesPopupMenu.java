package org.jphototagger.program.view.popupmenus;

import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.tree.TreePath;

import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppLookAndFeel;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@link org.jphototagger.lib.event.listener.PopupMenuTree} as e.g.
 * {@link org.jphototagger.program.view.popupmenus.MiscMetadataPopupMenu} does.
 *
 * @author Elmar Baumann
 */
public final class DirectoriesPopupMenu extends JPopupMenu {

    private static final long serialVersionUID = 4574458335277932153L;
    public static final DirectoriesPopupMenu INSTANCE = new DirectoriesPopupMenu();
    private final JMenuItem itemAddToFavorites = new JMenuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.DisplayName.Action.AddToFavoriteDirectories"), AppLookAndFeel.getIcon("icon_favorite.png"));
    private final JMenuItem itemCreateDirectory = new JMenuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.DisplayName.Action.CreateDirectory"), AppLookAndFeel.getIcon("icon_folder_new.png"));
    private final JMenuItem itemRenameDirectory = new JMenuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.DisplayName.Action.RenameDirectory"), AppLookAndFeel.ICON_RENAME);
    private final JMenuItem itemRefresh = new JMenuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.DisplayName.Action.Refresh"), AppLookAndFeel.ICON_REFRESH);
    private final JMenuItem itemDeleteDirectory = new JMenuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.DisplayName.Action.DeleteDirectory"), AppLookAndFeel.ICON_DELETE);
    private final JMenuItem menuItemExpandAllSubitems = new JMenuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.ItemExpand"));
    private final JMenuItem menuItemCollapseAllSubitems = new JMenuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.ItemCollapse"));
    private boolean treeSelected = false;
    private File directory;
    private TreePath path;

    private DirectoriesPopupMenu() {
        init();
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public void setTreePath(TreePath path) {
        this.path = path;
    }

    public TreePath getTreePath() {
        return path;
    }

    public JMenuItem getItemAddToFavorites() {
        return itemAddToFavorites;
    }

    public JMenuItem getItemCreateDirectory() {
        return itemCreateDirectory;
    }

    public JMenuItem getItemRenameDirectory() {
        return itemRenameDirectory;
    }

    public JMenuItem getItemDeleteDirectory() {
        return itemDeleteDirectory;
    }

    public JMenuItem getItemRefresh() {
        return itemRefresh;
    }

    public JMenuItem getItemCollapseAllSubitems() {
        return menuItemCollapseAllSubitems;
    }

    public JMenuItem getItemExpandAllSubitems() {
        return menuItemExpandAllSubitems;
    }

    private void init() {
        addItems();
        setAccelerators();
    }

    public boolean isTreeSelected() {
        return treeSelected;
    }

    public void setTreeSelected(boolean treeSelected) {
        this.treeSelected = treeSelected;
    }

    private void addItems() {
        add(itemAddToFavorites);
        add(new Separator());
        add(itemCreateDirectory);
        add(itemRenameDirectory);
        add(itemDeleteDirectory);
        add(new Separator());
        add(menuItemExpandAllSubitems);
        add(menuItemCollapseAllSubitems);
        add(new Separator());
        add(itemRefresh);
    }

    private void setAccelerators() {
        itemCreateDirectory.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_N));
        itemDeleteDirectory.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_DELETE));
        itemRenameDirectory.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_F2));
        itemRefresh.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_F5));
    }
}
