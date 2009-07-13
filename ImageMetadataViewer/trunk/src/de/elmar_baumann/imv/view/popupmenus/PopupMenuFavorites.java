package de.elmar_baumann.imv.view.popupmenus;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.resource.Bundle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;

/**
 * Menü für Aktionen in der Liste mit den Favoritenverzeichnissen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/23
 */
public final class PopupMenuFavorites extends JPopupMenu {

    private static final String DISPLAY_NAME_ACTION_INSERT_FAVORITE =
            Bundle.getString(
            "PopupMenuFavorites.DisplayName.Action.InsertFavorite"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_UPDATE_FAVORITE =
            Bundle.getString(
            "PopupMenuFavorites.DisplayName.Action.UpdateFavorite"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_DELETE_FAVORITE =
            Bundle.getString(
            "PopupMenuFavorites.DisplayName.Action.DeleteFavorite"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_MOVE_UP =
            Bundle.getString("PopupMenuFavorites.DisplayName.Action.MoveUp"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_MOVE_DOWN =
            Bundle.getString("PopupMenuFavorites.DisplayName.Action.MoveDown"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_ADD_FILESYSTEM_FOLDER =
            Bundle.getString(
            "PopupMenuFavorites.DisplayName.Action.AddFilesystemFolder"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_RENAME_FILESYSTEM_FOLDER =
            Bundle.getString(
            "PopupMenuFavorites.DisplayName.Action.RenameFilesystemFolder"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_DELETE_FILESYSTEM_FOLDER =
            Bundle.getString(
            "PopupMenuFavorites.DisplayName.Action.DeleteFilesystemFolder"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_OPEN_IN_FOLDERS =
            Bundle.getString(
            "PopupMenuFavorites.DisplayName.Action.OpenInFolders"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_REFRESH =
            Bundle.getString("PopupMenuFavorites.DisplayName.Action.Refresh"); // NOI18N
    private final JMenuItem itemInsertFavorite =
            new JMenuItem(DISPLAY_NAME_ACTION_INSERT_FAVORITE);
    private final JMenuItem itemUpdateFavorite =
            new JMenuItem(DISPLAY_NAME_ACTION_UPDATE_FAVORITE);
    private final JMenuItem itemDeleteFavorite =
            new JMenuItem(DISPLAY_NAME_ACTION_DELETE_FAVORITE);
    private final JMenuItem itemOpenInFolders =
            new JMenuItem(DISPLAY_NAME_ACTION_OPEN_IN_FOLDERS);
    private final JMenuItem itemRefresh =
            new JMenuItem(DISPLAY_NAME_ACTION_REFRESH);
    private final JMenuItem itemMoveUp =
            new JMenuItem(DISPLAY_NAME_ACTION_MOVE_UP);
    private final JMenuItem itemAddFilesystemFolder =
            new JMenuItem(DISPLAY_NAME_ACTION_ADD_FILESYSTEM_FOLDER);
    private final JMenuItem itemRenameFilesystemFolder =
            new JMenuItem(DISPLAY_NAME_ACTION_RENAME_FILESYSTEM_FOLDER);
    private final JMenuItem itemDeleteFilesystemFolder =
            new JMenuItem(DISPLAY_NAME_ACTION_DELETE_FILESYSTEM_FOLDER);
    private final JMenuItem itemMoveDown =
            new JMenuItem(DISPLAY_NAME_ACTION_MOVE_DOWN);
    private TreePath treePath;
    private FavoriteDirectory favoriteDirectory;
    public static final PopupMenuFavorites INSTANCE =
            new PopupMenuFavorites();

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

    public TreePath getTreePath() {
        return treePath;
    }

    public void setTreePath(TreePath treePath) {
        this.treePath = treePath;
    }

    public FavoriteDirectory getFavoriteDirectory() {
        return favoriteDirectory;
    }

    public void setFavoriteDirectory(FavoriteDirectory favoriteDirectory) {
        this.favoriteDirectory = favoriteDirectory;
    }

    private PopupMenuFavorites() {
        init();
    }

    private void init() {
        addItems();
        setIcons();
        setAccelerators();
    }

    private void addItems() {
        add(itemInsertFavorite);
        add(itemUpdateFavorite);
        add(itemDeleteFavorite);
        add(itemMoveUp);
        add(itemMoveDown);
        add(new JSeparator());
        add(itemOpenInFolders);
        add(new JSeparator());
        add(itemAddFilesystemFolder);
        add(itemRenameFilesystemFolder);
        add(itemDeleteFilesystemFolder);
        add(new JSeparator());
        add(itemRefresh);
    }

    private void setIcons() {
        itemDeleteFavorite.setIcon(AppIcons.getIcon("icon_remove.png")); // NOI18N
        itemInsertFavorite.setIcon(AppIcons.getIcon("icon_add.png")); // NOI18N
        itemMoveDown.setIcon(AppIcons.getIcon("icon_move_down.png")); // NOI18N
        itemMoveUp.setIcon(AppIcons.getIcon("icon_move_up.png")); // NOI18N
        itemOpenInFolders.setIcon(AppIcons.getIcon("icon_folder.png")); // NOI18N
        itemRefresh.setIcon(AppIcons.getIcon("icon_refresh.png")); // NOI18N
        itemAddFilesystemFolder.setIcon(AppIcons.getIcon("icon_folder_add.png")); // NOI18N
        itemRenameFilesystemFolder.setIcon(
                AppIcons.getIcon("icon_folder_rename.png")); // NOI18N
        itemDeleteFilesystemFolder.setIcon(
                AppIcons.getIcon("icon_folder_delete.png")); // NOI18N
        itemUpdateFavorite.setIcon(AppIcons.getIcon("icon_edit.png")); // NOI18N
    }

    private void setAccelerators() {
        itemUpdateFavorite.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
        itemInsertFavorite.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
        itemOpenInFolders.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        itemAddFilesystemFolder.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        itemDeleteFavorite.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        itemDeleteFilesystemFolder.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        itemRenameFilesystemFolder.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        itemRefresh.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
    }
}
