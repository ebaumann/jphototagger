/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.imv.resource;

import de.elmar_baumann.imv.app.AppLog;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Verkürzter Zugriff auf String-Ressourcen.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-02
 */
public final class Bundle {

    private static final ResourceBundle BUNDLE =
        ResourceBundle.getBundle("de/elmar_baumann/imv/resource/properties/Bundle"); // NOI18N

    /**
     * Returns <code>java.util.ResourceBundle.getBundle().getString()</code>
     * and catches exceptions.
     *
     * @param  key     key
     * @param  params  parameters to format via {@link java.text.MessageFormat}
     * @return         value or key between two question marks if the value
     *                 could not be retrieved
     * @throws NullPointerException if the key is null
     */
    public static String getString(String key, Object... params) {
        try {
            MessageFormat msg = new MessageFormat(BUNDLE.getString(key));
            return msg.format(params);
        } catch (NullPointerException ex) {
            throw ex;
        } catch (Exception ex) {
            AppLog.logSevere(Bundle.class, ex);
        }
        return "?" + key + "?"; // NOI18N
    }

    /**
     * Returns wheter a specific key is in the bundle.
     *
     * @param  key key
     * @return     true if the bundle contains that key
     */
    public static boolean containsKey(String key) {
        return BUNDLE.containsKey(key);
    }

    private Bundle() {
    }
}
