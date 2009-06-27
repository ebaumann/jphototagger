package de.elmar_baumann.imv.app;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.clipboard.lang.Version;

/**
 * Informations about this application.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/06/21
 */
public final class AppInfo {

    /**
     * Name of this project
     */
    private static final String projectName = "ImageMetaDataViewer"; // NOI18N NEVER CHANGE!
    /**
     * The application's version
     */
    public static final String appVersion = "0.5.94 2009/06/27";  // NOI18N
    /**
     * The application's name
     */
    public static final String appName = "Bilder";  // NOI18N
    /**
     * Title of the application
     */
    public static final String appDescription = Bundle.getString("AppTitle");
    /**
     * Minimum required Java version
     */
    public static final Version minJavaVersion = new Version(1, 6);

    /**
     * Returns the long name of this project.
     *
     * @return project name
     */
    public static String getProjectName() {
        return projectName;
    }

    private AppInfo() {
    }
}
