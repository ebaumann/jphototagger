package de.elmar_baumann.lib.resource;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Returns the strings defined in the <code>Bundle.properties</code> file
 * (<code>"de/elmar_baumann/lib/resource/properties/Bundle"</code>).
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/02
 */
public final class Bundle {

    private static final ResourceBundle bundle =
        ResourceBundle.getBundle("de/elmar_baumann/lib/resource/properties/Bundle"); // NOI18N

    /**
     * Returns <code>java.util.ResourceBundle.getBundle().getString()</code>
     * and catches exceptions.
     * 
     * @param  key key
     * @return     value or key between two question marks if the value could
     *             not be retrieved
     * @throws NullPointerException if the key is null
     */
    public static String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (NullPointerException ex) {
            throw ex;
        } catch (Exception ex) {
            Logger.getLogger(Bundle.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Bundle.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "?" + key + "?"; // NOI18N
    }

    private Bundle() {
    }
}
