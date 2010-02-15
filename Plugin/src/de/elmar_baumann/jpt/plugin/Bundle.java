package de.elmar_baumann.jpt.plugin;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-02-14
 */
public final class Bundle {

    private final ResourceBundle bundle;

    public Bundle(String path) {
        if (path == null) throw new NullPointerException("path == null");
        bundle = ResourceBundle.getBundle(path);
    }

    public String getString(String key, Object... params) {
        try {
            String s = bundle.getString(key);
            return MessageFormat.format(s, params);
        } catch (Exception ex) {
            Logger.getLogger(Bundle.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "?" + key + "?";
    }
}
