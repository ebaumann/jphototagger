package de.elmar_baumann.imagemetadataviewer.view.popupmenus;

import de.elmar_baumann.imagemetadataviewer.data.SavedSearch;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Popupmenü für gespeicherte Suchen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/31
 */
public class PopupMenuTreeSavedSearches extends JPopupMenu {

    private final String actionDelete = Bundle.getString("PopupMenuTreeSavedSearches.Action.Delete");
    private final String actionEdit = Bundle.getString("PopupMenuTreeSavedSearches.Action.Edit");
    private final String actionNew = Bundle.getString("PopupMenuTreeSavedSearches.Action.New");
    private final String actionRename = Bundle.getString("PopupMenuTreeSavedSearches.Action.Rename");
    private final JMenuItem itemDelete = new JMenuItem(actionDelete);
    private final JMenuItem itemEdit = new JMenuItem(actionEdit);
    private final JMenuItem itemCreate = new JMenuItem(actionNew);
    private final JMenuItem itemRename = new JMenuItem(actionRename);
    private SavedSearch savedSearch;
    private static PopupMenuTreeSavedSearches instance = new PopupMenuTreeSavedSearches();

    private PopupMenuTreeSavedSearches() {
        addItems();
    }

    /**
     * Liefert die einzige Klasseninstanz.
     * 
     * @return Instanz
     */
    public static PopupMenuTreeSavedSearches getInstance() {
        return instance;
    }

    /**
     * Setzt die gespeicherte Suche.
     * 
     * @param savedSearch Gespeicherte Suche. Default: null.
     */
    public void setSavedSearch(SavedSearch savedSearch) {
        this.savedSearch = savedSearch;
    }

    /**
     * Liefert die gespeicherte Suche.
     * 
     * @return Gespeicherte Suche
     */
    public SavedSearch getSavedSearch() {
        return savedSearch;
    }

    /**
     * Aktiviert das Item zum Löschen gespeicherter Suchen.
     * 
     * @param enabled true, wenn aktiviert. Default: true.
     */
    public void setEnabledDelete(boolean enabled) {
        itemDelete.setEnabled(enabled);
    }

    /**
     * Aktiviert das Item zum Bearbeiten gespeicherter Suchen.
     * 
     * @param enabled true, wenn aktiviert. Default: true.
     */
    public void setEnabledEdit(boolean enabled) {
        itemEdit.setEnabled(enabled);
    }

    /**
     * Aktiviert das Item zum Umbenennen gespeicherter Suchen.
     * 
     * @param enabled true, wenn aktiviert. Default: true.
     */
    public void setEnabledRename(boolean enabled) {
        itemRename.setEnabled(enabled);
    }

    /**
     * Fügt einen Beobachter hinzu für das Ereignis:
     * Eine gespeicherte Suche soll gelöscht werden.
     * 
     * @param listener Beobachter
     */
    public void addActionListenerDelete(ActionListener listener) {
        itemDelete.addActionListener(listener);
    }

    /**
     * Fügt einen Beobachter hinzu für das Ereignis:
     * Eine gespeicherte Suche soll bearbeitet werden.
     * 
     * @param listener Beobachter
     */
    public void addActionListenerEdit(ActionListener listener) {
        itemEdit.addActionListener(listener);
    }

    /**
     * Fügt einen Beobachter hinzu für das Ereignis:
     * Eine gespeicherte Suche soll erzeugt werden.
     * 
     * @param listener Beobachter
     */
    public void addActionListenerCreate(ActionListener listener) {
        itemCreate.addActionListener(listener);
    }

    /**
     * Fügt einen Beobachter hinzu für das Ereignis:
     * Eine gespeicherte Suche soll umbenannt werden.
     * 
     * @param listener Beobachter
     */
    public void addActionListenerRename(ActionListener listener) {
        itemRename.addActionListener(listener);
    }

    /**
     * Entfernt einen Beobachter für das Ereignis:
     * Eine gespeicherte Suche soll gelöscht werden.
     * 
     * @param listener Beobachter
     */
    public void removeActionListenerDelete(ActionListener listener) {
        itemDelete.removeActionListener(listener);
    }

    /**
     * Entfernt einen Beobachter für das Ereignis:
     * Eine gespeicherte Suche soll bearbeitet werden.
     * 
     * @param listener Beobachter
     */
    public void removeActionListenerEdit(ActionListener listener) {
        itemEdit.removeActionListener(listener);
    }

    /**
     * Entfernt einen Beobachter für das Ereignis:
     * Eine gespeicherte Suche soll erzeugt werden.
     * 
     * @param listener Beobachter
     */
    public void removeActionListenerCreate(ActionListener listener) {
        itemCreate.removeActionListener(listener);
    }

    /**
     * Entfernt einen Beobachter für das Ereignis:
     * Eine gespeicherte Suche soll umbenannt werden.
     * 
     * @param listener Beobachter
     */
    public void removeActionListenerRename(ActionListener listener) {
        itemRename.removeActionListener(listener);
    }

    private void addItems() {
        add(itemCreate);
        add(itemEdit);
        add(itemRename);
        add(itemDelete);
    }
}
