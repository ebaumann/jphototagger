package de.elmar_baumann.lib.resource;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages <em>one</em> {@link ResourceBundle}.
 * <p>
 * Can be instanciated directly or subclussed, e.g.:
 * <pre>
 * public final class JslBundle extends Bundle {
 *
 *     public static final JslBundle INSTANCE = new JslBundle();
 *
 *     private JslBundle() {
 *         super("de/elmar_baumann/lib/resource/properties/Bundle");
 *     }
 * }
 * </pre>
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-02-14
 */
public class Bundle {

    private final ResourceBundle bundle;

    /**
     * Constructor initializing the {@link ResourceBundle} with a specific
     * path.
     * 
     * @param path path, e.g. <code>"de/elmar_baumann/lib/resource/properties/Bundle"</code>
     *             if in that package at least one file <code>Bundle.properties</code>
     *             does exist
     */
    public Bundle(String path) {
        if (path == null) throw new NullPointerException("path == null");
        bundle = ResourceBundle.getBundle(path);
    }

    /**
     * Returns a string from the resource bundle and does log
     * {@link MissingResourceException}s rather than throwing it.
     *
     * @param key    key
     * @param params optional params as described in the {@link MessageFormat}
     *               class documentation
     * @return       string or the key whithin two question marks if that key
     *               does not address a string whithin the resource bundle
     * @throws       NullPointerException if <code>key</code> is null
     */
    public String getString(String key, Object... params) {
        if (key == null) throw new NullPointerException("key == null");
        try {
            String s = bundle.getString(key);
            return MessageFormat.format(s, params);
        } catch (Exception ex) {
            Logger.getLogger(Bundle.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "?" + key + "?";
    }

    /**
     * Returns whether a specific key is in the resource bundle.
     *
     * @param  key key
     * @return     true if the bundle contains that key
     */
    public boolean containsKey(String key) {
        return bundle.containsKey(key);
    }
}
