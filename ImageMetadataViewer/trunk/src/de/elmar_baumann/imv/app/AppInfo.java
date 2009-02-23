package de.elmar_baumann.imv.app;

import de.elmar_baumann.imv.resource.Bundle;

/**
 * Informationen über die Anwendung.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/06/21
 */
public final class AppInfo {

    private static final String projectName = "ImageMetaDataViewer"; // NOI18N NEVER CHANGE!
    /** Version der Anwendung */
    public static final String appVersion = "0.5.51 2009/02/20";  // NOI18N
    /** Name der Anwendung */
    public static final String appName = "Bilder";  // NOI18N
    /** Beschreibung der Anwendung */
    public static final String appDescription = Bundle.getString("AppTitle");

    /**
     * Returns the long name of this project.
     *
     * @return project name
     */
    public static String getProjectName() {
        return projectName;
    }

    private AppInfo() {}
}
