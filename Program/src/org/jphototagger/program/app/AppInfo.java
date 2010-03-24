/*
 * @(#)AppInfo.java    Created on 2008-06-21
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

package org.jphototagger.program.app;

import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.lib.util.Version;

/**
 * Informations about this application.
 *
 * @author  Elmar Baumann
 */
public final class AppInfo {

    /**
     * The application's name
     */
    public static final String APP_NAME = "JPhotoTagger";

    /**
     * The application's version
     */

    // Never change that scheme: "[0-9]+\.[0-9]+\.[0-9]+"!
    public static final String APP_VERSION = "0.8.9";

    /**
     * ("Historical") Name of this project.
     *
     * This is also the name of the user settings subdirectory (properties file)
     * and the default thumbnails' parent directory. When changing, the first or
     * second action in the main() mehtod has to be renaming that directory from
     * the old to the new name. Also check the code accessing this field.
     */

    // IF CHANGING IS REQUIRED, SEE COMMENT ABOVE!
    public static final String PROJECT_NAME = "ImageMetaDataViewer";

    /**
     * Minimum required Java version
     */
    public static final Version MIN_JAVA_VERSION = new Version(1, 6);

    /**
     * Title of the application
     */
    public static final String APP_DESCRIPTION =
        JptBundle.INSTANCE.getString("AppInfo.AppDescription");

    private AppInfo() {}
}
