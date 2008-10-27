package de.elmar_baumann.imv.view.popupmenus;

import de.elmar_baumann.imv.resource.Bundle;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.tree.TreePath;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/24
 */
public class PopupMenuTreeDirectories extends JPopupMenu {

    private final String actionAddToFavoriteDirectories = Bundle.getString("PopupMenuTreeDirectories.Action.AddToFavoriteDirectories");
    private final String actionFilesystemAddDirectory = Bundle.getString("PopupMenuTreeDirectories.Action.FilesystemAddDirectory");
    private final String actionFilesystemDeleteDirectory = Bundle.getString("PopupMenuTreeDirectories.Action.FilesystemDeleteDirectory");
    private final String actionFilesystemRenameDirectory = Bundle.getString("PopupMenuTreeDirectories.Action.FilesystemRenameDirectory");
    private final JMenuItem itemAddToFavoriteDirectories = new JMenuItem(actionAddToFavoriteDirectories);
    private final JMenuItem itemFilesystemAddDirectory = new JMenuItem(actionFilesystemAddDirectory);
    private final JMenuItem itemFilesystemDeleteDirectory = new JMenuItem(actionFilesystemDeleteDirectory);
    private final JMenuItem itemFilesystemRenameDirectory = new JMenuItem(actionFilesystemRenameDirectory);
    private List<JMenuItem> fileItems = new ArrayList<JMenuItem>();
    private TreePath path;
    private String directoryName;
    private static PopupMenuTreeDirectories instance = new PopupMenuTreeDirectories();

    private void initLists() {
        fileItems.add(itemAddToFavoriteDirectories);
        fileItems.add(itemFilesystemAddDirectory);
        fileItems.add(itemFilesystemDeleteDirectory);
        fileItems.add(itemFilesystemRenameDirectory);
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

    public void addActionListenerFilesystemDeleteDirectoy(ActionListener listener) {
        itemFilesystemDeleteDirectory.addActionListener(listener);
    }

    public void addActionListenerFilesystemRenameDirectoy(ActionListener listener) {
        itemFilesystemRenameDirectory.addActionListener(listener);
    }

    public void addActionListenerFilesystemAddDirectoy(ActionListener listener) {
        itemFilesystemAddDirectory.addActionListener(listener);
    }

    public boolean isActionFilesystemDeleteDirectory(Object source) {
        return source == itemFilesystemDeleteDirectory;
    }

    public boolean isActionFilesystemRenameDirectory(Object source) {
        return source == itemFilesystemRenameDirectory;
    }

    public boolean isActionFilesystemAddDirectory(Object source) {
        return source == itemFilesystemAddDirectory;
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
        add(new JSeparator());
        add(itemFilesystemAddDirectory);
        add(itemFilesystemRenameDirectory);
        add(itemFilesystemDeleteDirectory);
        initLists();
    }
}
