package org.jphototagger.lib.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Elmar Baumann
 */
public final class Bundle {

    /**
     * Returns a string from the resource bundle {@code Bundle.properties} within the same package and logs {@code MissingResourceException}s
     * rather than throwing it.
     *
     * @param classWithinSamePackageAsBundle
     * @param key
     * @param params optional params as described in the {@code MessageFormat} class documentation
     * @return Localized String in {@code Bundle.properties} in the same package as {@code clazz}
     */
    public static String getString(Class<?> classWithinSamePackageAsBundle, String key, Object... params) {
        if (classWithinSamePackageAsBundle == null) {
            throw new NullPointerException("classWithinSamePackageAsBundle == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        String bundlePath = classWithinSamePackageAsBundle.getPackage().getName() + ".Bundle";
        ResourceBundle bundle = ResourceBundle.getBundle(bundlePath);
        return getFormattedString(bundle, key, params);
    }

    private static String getFormattedString(ResourceBundle bundle, String key, Object... params) {
        try {
            String s = bundle.getString(key);
            return params == null || params.length == 0 ? s : MessageFormat.format(s, params);
        } catch (Throwable t) {
            Logger.getLogger(Bundle.class.getName()).log(Level.SEVERE, null, t);
            return "?" + key + "?";
        }
    }

    private Bundle() {
    }
}
