/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elmar_baumann.jpt.plugin.flickrupload;

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
final class Bundle {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("de/elmar_baumann/jpt/plugin/flickrupload/Bundle");

    static String getString(String key, Object... params) {
        try {
            String s = BUNDLE.getString(key);
            return MessageFormat.format(s, params);
        } catch (Exception ex) {
            Logger.getLogger(Bundle.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "?" + key + "?";
    }

    private Bundle() {
    }
}
