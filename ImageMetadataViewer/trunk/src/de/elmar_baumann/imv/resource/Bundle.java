package de.elmar_baumann.imv.resource;

import de.elmar_baumann.imv.app.AppLog;
import java.text.MessageFormat;
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
        ResourceBundle.getBundle("de/elmar_baumann/imv/resource/properties/Bundle");

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

    /**
     * Returns <code>java.util.ResourceBundle.getBundle().getString()</code>
     * and catches exceptions.
     *
     * @param  key     key
     * @param  params  parameters to format via {@link java.text.MessageFormat}
     * @return     value or key between two question marks if the value could
     *             not be retrieved
     * @throws NullPointerException if the key is null
     */
    public static String getString(String key, Object... params) {
        try {
            MessageFormat msg = new MessageFormat(bundle.getString(key));
            return msg.format(params);
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
