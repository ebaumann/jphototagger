package de.elmar_baumann.imv.view.popupmenus;

import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.resource.Bundle;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Menü für Aktionen in der Liste mit den Favoritenverzeichnissen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/23
 */
public final class PopupMenuListFavoriteDirectories extends JPopupMenu {

    private final String actionInsert = Bundle.getString("PopupMenuListFavoriteDirectories.Action.Insert");
    private final String actionUpdate = Bundle.getString("PopupMenuListFavoriteDirectories.Action.Update");
    private final String actionDelete = Bundle.getString("PopupMenuListFavoriteDirectories.Action.Delete");
    private final String actionOpenInFolders = Bundle.getString("PopupMenuListFavoriteDirectories.Action.OpenInFolders");
    private final JMenuItem itemInsert = new JMenuItem(actionInsert);
    private final JMenuItem itemUpdate = new JMenuItem(actionUpdate);
    private final JMenuItem itemDelete = new JMenuItem(actionDelete);
    private final JMenuItem itemOpenInFolders = new JMenuItem(actionOpenInFolders);
    private FavoriteDirectory favoriteDirectory;
    public static final PopupMenuListFavoriteDirectories INSTANCE = new PopupMenuListFavoriteDirectories();

    public JMenuItem getItemDelete() {
        return itemDelete;
    }

    public JMenuItem getItemInsert() {
        return itemInsert;
    }

    public JMenuItem getItemUpdate() {
        return itemUpdate;
    }

    public JMenuItem getItemOpenInFolders() {
        return itemOpenInFolders;
    }

    public FavoriteDirectory getFavoriteDirectory() {
        return favoriteDirectory;
    }

    public void setFavoriteDirectory(FavoriteDirectory favoriteDirectory) {
        this.favoriteDirectory = favoriteDirectory;
    }

    public synchronized void addActionListenerInsert(ActionListener listener) {
        itemInsert.addActionListener(listener);
    }

    public synchronized void addActionListenerDelete(ActionListener listener) {
        itemDelete.addActionListener(listener);
    }

    public synchronized void addActionListenerUpdate(ActionListener listener) {
        itemUpdate.addActionListener(listener);
    }

    public synchronized void addActionListenerOpenInFolders(ActionListener listener) {
        itemOpenInFolders.addActionListener(listener);
    }

    public void setEnabledInsert(boolean enabled) {
        itemInsert.setEnabled(enabled);
    }

    public void setEnabledDelete(boolean enabled) {
        itemDelete.setEnabled(enabled);
    }

    public void setEnabledUpdate(boolean enabled) {
        itemUpdate.setEnabled(enabled);
    }

    public void setEnabledOpenInFolders(boolean enabled) {
        itemOpenInFolders.setEnabled(enabled);
    }

    private PopupMenuListFavoriteDirectories() {
        init();
    }

    private void init() {
        add(itemInsert);
        add(itemUpdate);
        add(itemDelete);
        add(itemOpenInFolders);
    }
}
