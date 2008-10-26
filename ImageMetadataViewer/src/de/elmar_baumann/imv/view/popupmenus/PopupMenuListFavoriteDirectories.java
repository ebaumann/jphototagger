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
public class PopupMenuListFavoriteDirectories extends JPopupMenu {

    private final String actionInsert = Bundle.getString("PopupMenuListFavoriteDirectories.Action.Insert");
    private final String actionUpdate = Bundle.getString("PopupMenuListFavoriteDirectories.Action.Update");
    private final String actionDelete = Bundle.getString("PopupMenuListFavoriteDirectories.Action.Delete");
    private final JMenuItem itemInsert = new JMenuItem(actionInsert);
    private final JMenuItem itemUpdate = new JMenuItem(actionUpdate);
    private final JMenuItem itemDelete = new JMenuItem(actionDelete);
    private FavoriteDirectory favoriteDirectory;
    private static PopupMenuListFavoriteDirectories instance = new PopupMenuListFavoriteDirectories();

    /**
     * Liefert die einzige Klasseninstanz.
     * 
     * @return Instanz
     */
    public static PopupMenuListFavoriteDirectories getInstance() {
        return instance;
    }

    /**
     * Liefert das ausgewählte Favoritenverzeichnis.
     * 
     * @return Verzeichnis oder null, wenn nicht ausgewählt
     */
    public FavoriteDirectory getFavoriteDirectory() {
        return favoriteDirectory;
    }

    /**
     * Setzt das betroffene Favoritenverzeichnis.
     * 
     * @param favoriteDirectory  Verzeichnis
     */
    public void setFavoriteDirectory(FavoriteDirectory favoriteDirectory) {
        this.favoriteDirectory = favoriteDirectory;
    }

    /**
     * Fügt einen Beobachter hinzu für die Aktion: Favoritenverzeichnis
     * hinzufügen.
     * 
     * @param listener  Beobachter
     */
    public void addActionListenerInsert(ActionListener listener) {
        itemInsert.addActionListener(listener);
    }

    /**
     * Fügt einen Beobachter hinzu für die Aktion: Favoritenverzeichnis
     * entfernen.
     * 
     * @param listener  Beobachter
     */
    public void addActionListenerDelete(ActionListener listener) {
        itemDelete.addActionListener(listener);
    }

    /**
     * Fügt einen Beobachter hinzu für die Aktion: Favoritenverzeichnis
     * aktualisieren.
     * 
     * @param listener  Beobachter
     */
    public void addActionListenerUpdate(ActionListener listener) {
        itemUpdate.addActionListener(listener);
    }

    /**
     * Aktiviert das Einfügen von Favoritenverzeichnissen.
     * 
     * @param enabled true, wenn aktiviert
     */
    public void setEnabledInsert(boolean enabled) {
        itemInsert.setEnabled(enabled);
    }

    /**
     * Aktiviert das Entfernen von Favoritenverzeichnissen.
     * 
     * @param enabled true, wenn aktiviert
     */
    public void setEnabledDelete(boolean enabled) {
        itemDelete.setEnabled(enabled);
    }

    /**
     * Aktiviert das Aktualisieren von Favoritenverzeichnissen.
     * 
     * @param enabled true, wenn aktiviert
     */
    public void setEnabledUpdate(boolean enabled) {
        itemUpdate.setEnabled(enabled);
    }

    private PopupMenuListFavoriteDirectories() {
        init();
    }

    private void init() {
        add(itemInsert);
        add(itemUpdate);
        add(itemDelete);
    }
}
