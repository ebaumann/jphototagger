/*
 * @(#)Settings.java    2010-02-15
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

package de.elmar_baumann.jpt.plugin.flickrupload;

import java.util.Properties;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class Settings {
    private static final String KEY_DC_DESCRIPTION =
        "de.elmar_baumann.jpt.plugin.flickrupload.AddDcDescription";
    private static final String KEY_PHOTOSHOP_HEADLINE =
        "de.elmar_baumann.jpt.plugin.flickrupload.AddPhotoshopHeadline";
    private static final String KEY_DC_SUBJECTS =
        "de.elmar_baumann.jpt.plugin.flickrupload.AddDcSubjects";
    private static final String VALUE_BOOLEAN_TRUE  = "1";
    private static final String VALUE_BOOLEAN_FALSE = "0";
    private final Properties    properties;

    public Settings(Properties properties) {
        this.properties = properties;
    }

    public void setAddDcDescription(boolean add) {
        setBoolean(add, KEY_DC_DESCRIPTION);
    }

    public void setAddPhotoshopHeadline(boolean add) {
        setBoolean(add, KEY_PHOTOSHOP_HEADLINE);
    }

    public void setAddDcSubjects(boolean add) {
        setBoolean(add, KEY_DC_SUBJECTS);
    }

    public boolean isAddDcDescription() {
        return isTrue(KEY_DC_DESCRIPTION);
    }

    public boolean isAddPhotoshopHeadline() {
        return isTrue(KEY_PHOTOSHOP_HEADLINE);
    }

    public boolean isAddDcSubjects() {
        return isTrue(KEY_DC_SUBJECTS);
    }

    private boolean isTrue(String key) {
        String value = properties.getProperty(key);

        return (value != null) && value.equals(VALUE_BOOLEAN_TRUE);
    }

    private void setBoolean(boolean b, String key) {
        properties.setProperty(key, b
                                    ? VALUE_BOOLEAN_TRUE
                                    : VALUE_BOOLEAN_FALSE);
    }
}
