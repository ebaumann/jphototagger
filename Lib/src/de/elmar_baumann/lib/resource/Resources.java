/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
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
package de.elmar_baumann.lib.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Settings that will be used by the classes of this Java library.
 * <p>
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class Resources {

    public static final Resources INSTANCE = new Resources();
    private Properties properties;
    private List<String> iconImagesPaths = new ArrayList<String>();

    /**
     * Returns the paths to icon images set to frames if defined, e.g. to
     * dialogs.
     * <p>
     * Each path is a single icon image, e.g.
     * <code>"/de/elmar_baumann/jpt/resource/help.png"</code>.
     * 
     * @return paths
     */
    public List<String> getFramesIconImagesPaths() {
        return iconImagesPaths;
    }

    /**
     * Sets the paths to icon images set to frames if defined, e.g. to
     * dialogs.
     * <p>
     *  Each path is a single icon image, e.g.
     * <code>"/de/elmar_baumann/jpt/resource/help.png"</code>.
     * 
     * @param paths paths
     */
    public void setFramesIconImagesPath(List<String> paths) {
        if (paths == null)
            throw new NullPointerException("iconImagesPaths == null"); // NOI18N

        this.iconImagesPaths = new ArrayList<String>(paths);
    }

    /**
     * Sets the properties for reading and writing sizes and locations of
     * GUI elements.
     *
     * @param properties  properties
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * Returns the properties for reading and writing sizes and locations of
     * GUI elements.
     *
     * @return properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Returns, whether at least one path to an icon image set to frames is
     * defined.
     * 
     * @return true, if one ore more paths to icon images are defined
     */
    public boolean hasFrameIconImages() {
        return !iconImagesPaths.isEmpty();
    }

    private Resources() {
    }
}
