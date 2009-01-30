package de.elmar_baumann.imv.view.popupmenus;

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

    private final String actionAddToFavoriteDirectories = Bundle.getString("PopupMenuTreeDirectories.Action.AddToFavoriteDirectories");
    private final JMenuItem itemAddToFavoriteDirectories = new JMenuItem(actionAddToFavoriteDirectories);
    private final List<JMenuItem> fileItems = new ArrayList<JMenuItem>();
    private TreePath path;
    private String directoryName;
    private boolean treeSelected = false;
    private static final PopupMenuTreeDirectories instance = new PopupMenuTreeDirectories();

    private void initLists() {
        fileItems.add(itemAddToFavoriteDirectories);
    }

    /**
     * Liefert die einzige Klasseninstanz.
     * 
     * @return Instanz
     */
    public static PopupMenuTreeDirectories getInstance() {
        return instance;
    }

    public JMenuItem getItemAddToFavoriteDirectories() {
        return itemAddToFavoriteDirectories;
    }

    /**
     * Fügt einen Beobachter hinzu für die Aktion: Verzeichnis zu den Favoriten 
     * hinzufügen.
     * 
     * @param listener  Beobachter
     */
    public void addActionListenerAddToFavoriteDirectories(ActionListener listener) {
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
        initLists();
    }

    public boolean isTreeSelected() {
        return treeSelected;
    }

    public void setTreeSelected(boolean treeSelected) {
        this.treeSelected = treeSelected;
    }
}
