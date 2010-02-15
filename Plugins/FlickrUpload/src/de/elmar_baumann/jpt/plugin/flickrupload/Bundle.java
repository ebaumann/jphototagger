/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elmar_baumann.jpt.plugin.flickrupload;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-02-14
 */
final class Bundle {

    private static final de.elmar_baumann.jpt.plugin.Bundle BUNDLE = new de.elmar_baumann.jpt.plugin.Bundle("de/elmar_baumann/jpt/plugin/flickrupload/Bundle");

    static String getString(String key, Object... params) {
        return BUNDLE.getString(key, params);
    }

    private Bundle() {
    }
}
