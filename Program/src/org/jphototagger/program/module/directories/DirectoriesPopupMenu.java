package org.jphototagger.program.module.directories;

import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.tree.TreePath;

import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.AppLookAndFeel;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@code org.jphototagger.lib.event.listener.PopupMenuTree} as e.g.
 * {@code org.jphototagger.program.view.popupmenus.MiscMetadataPopupMenu} does.
 *
 * @author Elmar Baumann
 */
public final class DirectoriesPopupMenu extends JPopupMenu {

    private static final long serialVersionUID = 1L;
    public static final DirectoriesPopupMenu INSTANCE = new DirectoriesPopupMenu();
    private final JMenuItem itemAddToFavorites = new JMenuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.DisplayName.Action.AddToFavoriteDirectories"), AppLookAndFeel.getIcon("icon_favorite.png"));
    private final JMenuItem itemCreateDirectory = new JMenuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.DisplayName.Action.CreateDirectory"), AppLookAndFeel.getIcon("icon_folder_new.png"));
    private final JMenuItem itemRenameDirectory = new JMenuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.DisplayName.Action.RenameDirectory"), AppLookAndFeel.ICON_RENAME);
    private final JMenuItem itemRefresh = new JMenuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.DisplayName.Action.Refresh"), AppLookAndFeel.ICON_REFRESH);
    private final JMenuItem itemDeleteDirectory = new JMenuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.DisplayName.Action.DeleteDirectory"), AppLookAndFeel.ICON_DELETE);
    private final JMenuItem itemExpandAllSubitems = new JMenuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.ItemExpand"));
    private final JMenuItem itemCollapseAllSubitems = new JMenuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.ItemCollapse"));
    private final JMenuItem itemOpenInDesktop = new JMenuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.Action.OpenInDesktop"));
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
        return itemCollapseAllSubitems;
    }

    public JMenuItem getItemExpandAllSubitems() {
        return itemExpandAllSubitems;
    }

    public JMenuItem getItemOpenInDesktop() {
        return itemOpenInDesktop;
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
        add(itemOpenInDesktop);
        add(new Separator());
        add(itemCreateDirectory);
        add(itemRenameDirectory);
        add(itemDeleteDirectory);
        add(new Separator());
        add(itemExpandAllSubitems);
        add(itemCollapseAllSubitems);
        add(new Separator());
        add(itemRefresh);
    }

    private void setAccelerators() {
        itemOpenInDesktop.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_O));
        itemCreateDirectory.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_N));
        itemDeleteDirectory.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_DELETE));
        itemRenameDirectory.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_F2));
        itemRefresh.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_F5));
    }
}
