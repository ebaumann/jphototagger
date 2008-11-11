package de.elmar_baumann.imv.resource;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Verkürzter Zugriff auf String-Ressourcen.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/02
 */
public class Bundle {

    private static ResourceBundle bundle =
        ResourceBundle.getBundle("de/elmar_baumann/imv/resource/Bundle");

    /**
     * Liefert java.util.ResourceBundle.getBundle().getString() und fängt
     * dessen Ausnahmen ab.
     * 
     * @param  key  Schlüssel
     * @return Wert, bei einer Ausnahme der Schlüssel mit Fragezeichen
     *         umschlossen
     */
    public static String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException ex) {
            de.elmar_baumann.imv.Log.logWarning(Bundle.class, ex);
        } catch (Exception ex) {
            de.elmar_baumann.imv.Log.logWarning(Bundle.class, ex);
        }
        return "?" + key + "?"; // NOI18N
    }
}
