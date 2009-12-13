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
package de.elmar_baumann.jpt.app;

import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.lib.util.Version;

/**
 * Informations about this application.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-06-21
 */
public final class AppInfo {

    /**
     * ("Historical") Name of this project.
     *
     * This is also the name of the user settings subdirectory (properties file)
     * and the default thumbnails' parent directory. When changing, the first or
     * second action in the main() mehtod has to be renaming that directory from
     * the old to the new name. Also check the code accessing this field.
     */
    public static final String PROJECT_NAME = "ImageMetaDataViewer"; // NOI18N WHEN CHANGING SEE COMMENT ABOVE!
    /**
     * The application's version
     */
    public static final String APP_VERSION = "0.7.5 2009-12-13";  // NOI18N
    /**
     * The application's name
     */
    public static final String APP_NAME = "JPhotoTagger";  // NOI18N
    /**
     * Title of the application
     */
    public static final String APP_DESCRIPTION = Bundle.getString("AppDescription"); // NOI18N
    /**
     * Minimum required Java version
     */
    public static final Version MIN_JAVA_VERSION = new Version(1, 6);

    private AppInfo() {
    }
}
