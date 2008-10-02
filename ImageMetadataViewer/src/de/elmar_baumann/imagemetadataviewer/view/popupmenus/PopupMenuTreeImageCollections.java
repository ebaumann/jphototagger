package de.elmar_baumann.imagemetadataviewer.view.popupmenus;

import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Popupmenü für den Tree mit Bildsammlungen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/08
 */
public class PopupMenuTreeImageCollections extends JPopupMenu {

    private final String actionDelete = Bundle.getString("PopupMenuTreeImageCollections.Action.Delete");
    private final String actionRename = Bundle.getString("PopupMenuTreeImageCollections.Action.Rename");
    private final JMenuItem itemDelete = new JMenuItem(actionDelete);
    private final JMenuItem itemRename = new JMenuItem(actionRename);
    private String imageCollectionName;
    private static PopupMenuTreeImageCollections instance = new PopupMenuTreeImageCollections();

    private PopupMenuTreeImageCollections() {
        addItems();
    }

    /**
     * Erzeugt die einzige Klasseninstanz.
     * 
     * @return Klasseninstanz
     */
    public static PopupMenuTreeImageCollections getInstance() {
        return instance;
    }

    /**
     * Setzt den Namen der Bildsamlung.
     * 
     * @param name Name. Default: null.
     */
    public void setImageCollectionName(String name) {
        imageCollectionName = name;
    }

    /**
     * Liefert den Namen der gespeicherten Bildsamlung.
     * 
     * @return Name
     */
    public String getImageCollectionName() {
        return imageCollectionName;
    }

    /**
     * Aktiviert das Item zum Löschen von Bildsamlungen.
     * 
     * @param enabled true, wenn aktiviert. Default: true.
     */
    public void setEnabledDelete(boolean enabled) {
        itemDelete.setEnabled(enabled);
    }

    /**
     * Aktiviert das Item zum Umbenennenvon Bildsamlungen.
     * 
     * @param enabled true, wenn aktiviert. Default: true.
     */
    public void setEnabledRename(boolean enabled) {
        itemRename.setEnabled(enabled);
    }

    /**
     * Fügt einen Beobachter hinzu für das Ereignis:
     * Eine Bildsammlung soll gelöscht werden.
     * 
     * @param listener Beobachter
     */
    public void addActionListenerDelete(ActionListener listener) {
        itemDelete.addActionListener(listener);
    }

    /**
     * Fügt einen Beobachter hinzu für das Ereignis:
     * Eine Bildsammlung soll umbenannt werden.
     * 
     * @param listener Beobachter
     */
    public void addActionListenerRename(ActionListener listener) {
        itemRename.addActionListener(listener);
    }

    /**
     * Entfernt einen Beobachter für das Ereignis:
     * Eine Bildsammlung soll gelöscht werden.
     * 
     * @param listener Beobachter
     */
    public void removeActionListenerDelete(ActionListener listener) {
        itemDelete.removeActionListener(listener);
    }

    /**
     * Entfernt einen Beobachter für das Ereignis:
     * Eine Bildsammlung soll umbenannt werden.
     * 
     * @param listener Beobachter
     */
    public void removeActionListenerRename(ActionListener listener) {
        itemRename.removeActionListener(listener);
    }

    private void addItems() {
        add(itemRename);
        add(itemDelete);
    }
}
