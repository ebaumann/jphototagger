package de.elmar_baumann.imv.app;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.util.Version;

/**
 * Informations about this application.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-06-21
 */
public final class AppInfo {

    /**
     * Name of this project
     */
    private static final String PROJECT_NAME = "ImageMetaDataViewer"; // NOI18N NEVER CHANGE!
    /**
     * The application's version
     */
    public static final String APP_VERSION = "0.6.31 2009-08-09";  // NOI18N
    /**
     * The application's name
     */
    public static final String APP_NAME = "Bilder";  // NOI18N
    /**
     * Title of the application
     */
    public static final String APP_DESCRIPTION = Bundle.getString("AppTitle"); // NOI18N
    /**
     * Minimum required Java version
     */
    public static final Version MIN_JAVA_VERSION = new Version(1, 6);

    /**
     * Returns the long name of this project.
     *
     * @return project name
     */
    public static String getProjectName() {
        return PROJECT_NAME;
    }

    private AppInfo() {
    }
}
