package org.jphototagger.program.view.popupmenus;

import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.tree.TreePath;

import org.jphototagger.domain.favorites.Favorite;
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
public final class FavoritesPopupMenu extends JPopupMenu {

    private static final long serialVersionUID = -7344945087460562958L;
    public static final FavoritesPopupMenu INSTANCE = new FavoritesPopupMenu();
    private final JMenuItem itemInsertFavorite = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.DisplayName.Action.InsertFavorite"), AppLookAndFeel.ICON_NEW);
    private final JMenuItem itemUpdateFavorite = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.DisplayName.Action.UpdateFavorite"), AppLookAndFeel.ICON_EDIT);
    private final JMenuItem itemRenameFilesystemFolder = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.DisplayName.Action.RenameFilesystemFolder"), AppLookAndFeel.ICON_RENAME);
    private final JMenuItem itemRefresh = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.DisplayName.Action.Refresh"), AppLookAndFeel.ICON_REFRESH);
    private final JMenuItem itemOpenInFolders = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.DisplayName.Action.OpenInFolders"), AppLookAndFeel.getIcon("icon_folder.png"));
    private final JMenuItem itemMoveUp = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.DisplayName.Action.MoveUp"), AppLookAndFeel.getIcon("icon_arrow_up.png"));
    private final JMenuItem itemMoveDown = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.DisplayName.Action.MoveDown"), AppLookAndFeel.getIcon("icon_arrow_down.png"));
    private final JMenuItem itemExpandAllSubitems = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.ItemExpand"));
    private final JMenuItem itemDeleteFilesystemFolder = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.DisplayName.Action.DeleteFilesystemFolder"), AppLookAndFeel.ICON_DELETE);
    private final JMenuItem itemDeleteFavorite = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.DisplayName.Action.DeleteFavorite"), AppLookAndFeel.ICON_DELETE);
    private final JMenuItem itemCollapseAllSubitems = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.ItemCollapse"));
    private final JMenuItem itemAddFilesystemFolder = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.DisplayName.Action.AddFilesystemFolder"), AppLookAndFeel.getIcon("icon_folder_new.png"));
    private transient Favorite favoriteDirectory;
    private TreePath treePath;

    private FavoritesPopupMenu() {
        init();
    }

    public JMenuItem getItemDeleteFavorite() {
        return itemDeleteFavorite;
    }

    public JMenuItem getItemInsertFavorite() {
        return itemInsertFavorite;
    }

    public JMenuItem getItemUpdateFavorite() {
        return itemUpdateFavorite;
    }

    public JMenuItem getItemOpenInFolders() {
        return itemOpenInFolders;
    }

    public JMenuItem getItemRefresh() {
        return itemRefresh;
    }

    public JMenuItem getItemAddFilesystemFolder() {
        return itemAddFilesystemFolder;
    }

    public JMenuItem getItemDeleteFilesystemFolder() {
        return itemDeleteFilesystemFolder;
    }

    public JMenuItem getItemMoveDown() {
        return itemMoveDown;
    }

    public JMenuItem getItemMoveUp() {
        return itemMoveUp;
    }

    public JMenuItem getItemRenameFilesystemFolder() {
        return itemRenameFilesystemFolder;
    }

    public JMenuItem getItemCollapseAllSubitems() {
        return itemCollapseAllSubitems;
    }

    public JMenuItem getItemExpandAllSubitems() {
        return itemExpandAllSubitems;
    }

    public TreePath getTreePath() {
        return treePath;
    }

    public void setTreePath(TreePath treePath) {
        this.treePath = treePath;
    }

    public Favorite getFavorite() {
        return favoriteDirectory;
    }

    public void setFavoriteDirectory(Favorite favoriteDirectory) {
        this.favoriteDirectory = favoriteDirectory;
    }

    private void init() {
        addItems();
        setAccelerators();
    }

    private void addItems() {
        add(itemInsertFavorite);
        add(itemUpdateFavorite);
        add(itemDeleteFavorite);
        add(itemMoveUp);
        add(itemMoveDown);
        add(new Separator());
        add(itemOpenInFolders);
        add(new Separator());
        add(itemAddFilesystemFolder);
        add(itemRenameFilesystemFolder);
        add(itemDeleteFilesystemFolder);
        add(new Separator());
        add(itemExpandAllSubitems);
        add(itemCollapseAllSubitems);
        add(new Separator());
        add(itemRefresh);
    }

    private void setAccelerators() {
        itemUpdateFavorite.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_E));
        itemInsertFavorite.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_I));
        itemOpenInFolders.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_O));
        itemAddFilesystemFolder.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_N));
        itemDeleteFavorite.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_DELETE));
        itemDeleteFilesystemFolder.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_DELETE));
        itemRenameFilesystemFolder.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_F2));
        itemRefresh.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_F5));
    }
}
