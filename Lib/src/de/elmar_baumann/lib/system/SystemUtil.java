/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.lib.system;

import de.elmar_baumann.lib.util.Version;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2009-04-30
 */
public final class SystemUtil {

    /**
     * Returns the Version of the JVM.
     *
     * @return Version or null if not found
     */
    public static Version getJavaVersion() {
        Version version = null;
        String versionProperty = System.getProperty("java.version");
        StringTokenizer tok = new StringTokenizer(versionProperty, ".");
        if (tok.countTokens() >= 2) {
            try {
                int major = Integer.parseInt(tok.nextToken());
                int minor = Integer.parseInt(tok.nextToken());
                return new Version(major, minor);
            } catch (Exception ex) {
                Logger.getLogger(SystemUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return version;
    }

    private SystemUtil() {
    }
}
