/*
 * @(#)PropertiesUtil.java    Created on 2010-08-21
 *
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

package org.jphototagger.lib.util;

import java.io.File;

import java.util.Properties;

/**
 * Utils for {@link java.util.Properties}.
 *
 * @author Elmar Baumann
 */
public final class PropertiesUtil {

    /**
     * Sets to the properties as value the absolute path of a directory.
     *
     * @param properties properties
     * @param key        key
     * @param file       file or null. If the file is not a directory, it's
     *                   parent file will be set. If the file is a directory, it
     *                   will be set. If the file is null, nothing will be set.
     * @return           true if the directory path name was set
     */
    public static boolean setDirectory(Properties properties, String key,
                                       File file) {
        if (properties == null) {
            throw new NullPointerException("properties == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        if (file == null) {
            return false;
        }

        if (file.isDirectory()) {
            properties.put(key, file.getAbsolutePath());

            return true;
        } else {
            properties.put(key, file.getParentFile().getAbsolutePath());

            return true;
        }
    }

    private PropertiesUtil() {}
}
