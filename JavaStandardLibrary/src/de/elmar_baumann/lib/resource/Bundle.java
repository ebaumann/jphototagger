package de.elmar_baumann.lib.resource;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Vereinfachter Zugriff auf String-Ressourcen.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/02
 */
public class Bundle {

    private static ResourceBundle bundle =
        ResourceBundle.getBundle("de/elmar_baumann/lib/resource/Bundle"); // NOI18N

    /**
     * Liefert <code>java.util.ResourceBundle.getBundle().getString()</code> und fängt
     * dessen Ausnahmen ab.
     * 
     * @param  key Schlüssel
     * @return     Wert, bei einer Ausnahme der Schlüssel mit Fragezeichen
     *             umschlossen
     */
    public static String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException ex) {
            Logger.getLogger(Bundle.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Bundle.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "?" + key + "?"; // NOI18N
    }
}
