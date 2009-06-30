package de.elmar_baumann.imv.view.popupmenus;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.resource.Bundle;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/24
 */
public final class PopupMenuTreeDirectories extends JPopupMenu {

    private final String actionAddToFavoriteDirectories = Bundle.getString(
            "PopupMenuTreeDirectories.Action.AddToFavoriteDirectories");
    private final String actionCreateDirectory = Bundle.getString(
            "PopupMenuTreeDirectories.Action.CreateDirectory");
    private final String actionRenameDirectory = Bundle.getString(
            "PopupMenuTreeDirectories.Action.RenameDirectory");
    private final String actionDeleteDirectory = Bundle.getString(
            "PopupMenuTreeDirectories.Action.DeleteDirectory");
    private static final String ACTION_REFRESH = Bundle.getString(
            "PopupMenuTreeDirectories.Action.Refresh");
    private final JMenuItem itemAddToFavoriteDirectories = new JMenuItem(
            actionAddToFavoriteDirectories);
    private final JMenuItem itemCreateDirectory = new JMenuItem(
            actionCreateDirectory);
    private final JMenuItem itemRenameDirectory = new JMenuItem(
            actionRenameDirectory);
    private final JMenuItem itemDeleteDirectory = new JMenuItem(
            actionDeleteDirectory);
    private final JMenuItem itemRefresh = new JMenuItem(ACTION_REFRESH);
    private final List<JMenuItem> fileItems = new ArrayList<JMenuItem>();
    private TreePath path;
    private String directoryName;
    private boolean treeSelected = false;
    public static final PopupMenuTreeDirectories INSTANCE =
            new PopupMenuTreeDirectories();

    private void initLists() {
        fileItems.add(itemAddToFavoriteDirectories);
        fileItems.add(itemCreateDirectory);
        fileItems.add(itemRenameDirectory);
        fileItems.add(itemDeleteDirectory);
    }

    public JMenuItem getItemAddToFavoriteDirectories() {
        return itemAddToFavoriteDirectories;
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
     * Fügt einen Beobachter hinzu für die Aktion: Verzeichnis zu den Favoriten 
     * hinzufügen.
     * 
     * @param listener  Beobachter
     */
    public synchronized void addActionListenerAddToFavoriteDirectories(
            ActionListener listener) {
        itemAddToFavoriteDirectories.addActionListener(listener);
    }

    public void setFileItemsEnabled(boolean enabled) {
        for (JMenuItem item : fileItems) {
            item.setEnabled(enabled);
        }
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

    /**
     * Liefert, ob das ausgewählte Verzeichnis zu den Favoriten hinzugefügt
     * werden soll.
     * 
     * @param  source  Ereignisquelle
     * @return true, wenn das ausgewählte Verzeichnis zu den Favoriten
     *         hinzugefügt werden soll
     */
    public boolean isAddToFavoriteDirectoriesItem(Object source) {
        return source == itemAddToFavoriteDirectories;
    }

    private PopupMenuTreeDirectories() {
        init();
    }

    private void init() {
        add(itemAddToFavoriteDirectories);
        add(itemCreateDirectory);
        add(itemRenameDirectory);
        add(itemDeleteDirectory);
        add(itemRefresh);
        setIcons();
        initLists();
    }

    public boolean isTreeSelected() {
        return treeSelected;
    }

    public void setTreeSelected(boolean treeSelected) {
        this.treeSelected = treeSelected;
    }

    private void setIcons() {
        itemAddToFavoriteDirectories.setIcon(AppIcons.getIcon(
                "icon_favorite.png"));
        itemCreateDirectory.setIcon(AppIcons.getIcon("icon_add.png"));
        itemDeleteDirectory.setIcon(AppIcons.getIcon("icon_edit_delete.png"));
        itemRenameDirectory.setIcon(AppIcons.getIcon("icon_rename.png"));
        itemRefresh.setIcon(AppIcons.getIcon("icon_refresh.png"));
    }
}
