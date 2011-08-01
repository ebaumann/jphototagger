package org.jphototagger.lib.resource;

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
 *         super("org/jphototagger/lib/resource/properties/Bundle");
 *     }
 * }
 * </pre>
 *
 * @author Elmar Baumann
 */
public class Bundle {

    private final ResourceBundle bundle;

    /**
     * Constructor initializing the {@link ResourceBundle} with a specific path (usually for extending this
     * class or using a bundle with a name different from <code>Bundle.properties</code>,
     * {@link #getBundle(java.lang.Class)} is more reliable on refactorings).
     *
     * @param path path, e.g. <code>"org/jphototagger/lib/resource/properties/Bundle"</code>
     *             <code>Bundle.properties</code> if in that package at least one file does exist
     */
    public Bundle(String path) {
        if (path == null) {
            throw new NullPointerException("path == null");
        }

        bundle = ResourceBundle.getBundle(path);
    }

    /**
     *
     * @param clazz
     * @return Bundle of {@code Bundle.properties} whithin the same package as {@code clazz}
     */
    public static Bundle getBundle(Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz == null");
        }

        String bundlePath = createDefaultBundlePath(clazz);

        return new Bundle(bundlePath);
    }

    private static String createDefaultBundlePath(Class<?> clazz) {
        String packagePath = getPackagePath(clazz);
        String bundlePath = packagePath + '/' + "Bundle";

        return bundlePath;
    }

    /**
     * Returns a string from the resource bundle and logs {@link MissingResourceException}s rather than throwing it.
     * <p>
     * This method is preferrable to create a new instance of this class or using {@link #getBundle(java.lang.Class)}.
     *
     * @param clazz
     * @param key
     * @param params optional params as described in the {@link MessageFormat} class documentation
     * @return       String in {@code Bundle.properties} whithin the same package as {@code clazz}
     */
    public static String getString(Class<?> clazz, String key, Object... params) {
        if (clazz == null) {
            throw new NullPointerException("clazz == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        // Javadoc: Resource bundle instances created by the getBundle factory methods are cached by default
        String bundlePath = createDefaultBundlePath(clazz);
        ResourceBundle bundle = ResourceBundle.getBundle(bundlePath);

        return getFormattedString(bundle, key, params);
    }

    private static String getPackagePath(Class<?> clazz) {
        String className = clazz.getName();
        int indexLastDot = className.lastIndexOf('.');

        if (indexLastDot < 1) {
            return "";
        }

        String packagePath = className.substring(0, indexLastDot);

        return packagePath.replace(".", "/");
    }

    private static String getFormattedString(ResourceBundle bundle, String key, Object... params) {

        try {
            String s = bundle.getString(key);

            return params == null || params.length == 0 ? s : MessageFormat.format(s, params);
        } catch (Exception ex) {
            Logger.getLogger(Bundle.class.getName()).log(Level.SEVERE, null, ex);

            return "?" + key + "?";
        }
    }

    /**
     * Returns a string from the resource bundle and logs {@link MissingResourceException}s rather than throwing it.
     *
     * @param key
     * @param params optional params as described in the {@link MessageFormat} class documentation
     * @return       string or the key whithin two question marks if that key does not address a string whithin the resource bundle
     */
    public String getString(String key, Object... params) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        return getFormattedString(bundle, key, params);
    }

    public boolean containsKey(String key) {
        if (key == null) {
            return false;
        }

        return bundle.containsKey(key);
    }
}
