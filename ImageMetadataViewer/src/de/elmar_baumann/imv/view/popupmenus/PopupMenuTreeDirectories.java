package de.elmar_baumann.imv.view.popupmenus;

import de.elmar_baumann.imv.resource.Bundle;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/24
 */
public class PopupMenuTreeDirectories extends JPopupMenu {

    private final String actionAddToFavoriteDirectories = Bundle.getString("PopupMenuTreeDirectories.Action.AddToFavoriteDirectories");
    private final JMenuItem itemAddToFavoriteDirectories = new JMenuItem(actionAddToFavoriteDirectories);
    private String directoryName;
    private static PopupMenuTreeDirectories instance = new PopupMenuTreeDirectories();

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

    /**
     * Aktiviert die Aktion: Zu den Favoritenverzeichnissen hinzufügen.
     * 
     * @param enabled  true, wenn aktiviert. Default: true.
     */
    public void setEnabledAddToFavoriteDirectories(boolean enabled) {
        itemAddToFavoriteDirectories.setEnabled(enabled);
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
    }
}
