package de.elmar_baumann.lib.resource;

import java.util.Vector;

/**
 * Einstellungen. Wird von Klassen dieser Lib genutzt.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/12
 */
public class Settings {

    private static Settings instance = new Settings();
    private Vector<String> iconImagesPaths;

    /**
     * Liefert die Pfade zu den Icons der Anwendung.
     * 
     * @return Pfade oder null, wenn nicht gesetzt
     */
    public Vector<String> getIconImagesPaths() {
        return iconImagesPaths;
    }

    /**
     * Setzt die Pfade zu den Icons der der Anwendung.
     * 
     * @param iconImagesPaths Pfade
     */
    public void setIconImagesPath(Vector<String> iconImagesPaths) {
        this.iconImagesPaths = iconImagesPaths;
    }

    /**
     * Liefert, ob der Pfad zu den Icons der Anwendung definiert ist.
     * 
     * @return true, wenn definiert
     */
    public boolean hasIconImages() {
        return iconImagesPaths != null;
    }

    /**
     * Liefert die einzige Instanz.
     * 
     * @return Instanz
     */
    public static Settings getInstance() {
        return instance;
    }

    private Settings() {
    }
}
