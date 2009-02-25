package de.elmar_baumann.imv.resource;

import de.elmar_baumann.imv.app.AppLog;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Verkürzter Zugriff auf String-Ressourcen.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/02
 */
public final class Bundle {

    private static final ResourceBundle bundle =
        ResourceBundle.getBundle("de/elmar_baumann/imv/resource/Bundle");

    /**
     * Liefert java.util.ResourceBundle.getBundle().getString() und fängt
     * dessen Ausnahmen ab.
     * 
     * @param  key  Schlüssel
     * @return Wert, bei einer Ausnahme der Schlüssel mit Fragezeichen
     *         umschlossen
     * @throws NullPointerException wenn der Schlüssel null ist
     */
    public static String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException ex) {
            AppLog.logWarning(Bundle.class, ex);
        } catch (NullPointerException ex) {
            throw ex;
        } catch (Exception ex) {
            AppLog.logWarning(Bundle.class, ex);
        }
        return "?" + key + "?"; // NOI18N
    }

    private Bundle() {
    }
}
