package de.elmar_baumann.imv.view.popupmenus;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.resource.Bundle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/24
 */
public final class PopupMenuDirectories extends JPopupMenu {

    private static final String DISPLAY_NAME_ACTION_ADD_TO_FAVORITES =
            Bundle.getString(
            "PopupMenuDirectories.DisplayName.Action.AddToFavoriteDirectories");
    private static final String DISPLAY_NAME_ACTION_CREATE_FOLDER =
            Bundle.getString(
            "PopupMenuDirectories.DisplayName.Action.CreateDirectory");
    private static final String DISPLAY_NAME_ACTION_RENAME_FODER =
            Bundle.getString(
            "PopupMenuDirectories.DisplayName.Action.RenameDirectory");
    private static final String DISPLAY_NAME_ACTION_DELETE_FOLDER =
            Bundle.getString(
            "PopupMenuDirectories.DisplayName.Action.DeleteDirectory");
    private static final String DISPLAY_NAME_ACTION_REFRESH =
            Bundle.getString("PopupMenuDirectories.DisplayName.Action.Refresh");
    private final JMenuItem itemAddToFavorites =
            new JMenuItem(DISPLAY_NAME_ACTION_ADD_TO_FAVORITES);
    private final JMenuItem itemCreateDirectory =
            new JMenuItem(DISPLAY_NAME_ACTION_CREATE_FOLDER);
    private final JMenuItem itemRenameDirectory =
            new JMenuItem(DISPLAY_NAME_ACTION_RENAME_FODER);
    private final JMenuItem itemDeleteDirectory =
            new JMenuItem(DISPLAY_NAME_ACTION_DELETE_FOLDER);
    private final JMenuItem itemRefresh =
            new JMenuItem(DISPLAY_NAME_ACTION_REFRESH);
    private TreePath path;
    private String directoryName;
    private boolean treeSelected = false;
    public static final PopupMenuDirectories INSTANCE =
            new PopupMenuDirectories();

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

    /**
     * Liefert den ausgewählten Verzeichnisnamen.
     * 
     * @return Name oder null, wenn nicht auf ein Verzeichnis geklickt wurde
     */
    public String getDirectoryName() {
        return directoryName;
    }

    /**
     * Setzt den Verzeichnisnamen, auf den geklickt wurde.
     * 
     * @param directoryName  Verzeichnisname
     */
    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public void setTreePath(TreePath path) {
        this.path = path;
    }

    public TreePath getTreePath() {
        return path;
    }

    private PopupMenuDirectories() {
        init();
    }

    private void init() {
        addItems();
        setAccelerators();
        setIcons();
    }

    public boolean isTreeSelected() {
        return treeSelected;
    }

    public void setTreeSelected(boolean treeSelected) {
        this.treeSelected = treeSelected;
    }

    private void addItems() {
        add(itemAddToFavorites);
        add(new JSeparator());
        add(itemCreateDirectory);
        add(itemRenameDirectory);
        add(itemDeleteDirectory);
        add(new JSeparator());
        add(itemRefresh);
    }

    private void setIcons() {
        itemAddToFavorites.setIcon(AppIcons.getIcon("icon_favorite.png"));
        itemCreateDirectory.setIcon(AppIcons.getIcon("icon_folder_add.png"));
        itemDeleteDirectory.setIcon(AppIcons.getIcon("icon_folder_delete.png"));
        itemRenameDirectory.setIcon(AppIcons.getIcon("icon_folder_rename.png"));
        itemRefresh.setIcon(AppIcons.getIcon("icon_refresh.png"));
    }

    private void setAccelerators() {
        itemCreateDirectory.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        itemDeleteDirectory.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        itemRenameDirectory.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        itemRefresh.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
    }
}
