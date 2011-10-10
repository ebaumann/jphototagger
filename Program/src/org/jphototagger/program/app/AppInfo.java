package org.jphototagger.program.app;

import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.Version;

/**
 * Informations about this application.
 *
 * @author Elmar Baumann
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
    public static final String APP_VERSION = "0.11.0";
    /**
     * ("Historical") Name of this project.
     * <p>
     * This is also the name of the user settings subdirectory (properties file)
     * and the default thumbnails parent directory. When changing, the first or
     * second action in the main() mehtod has to be renaming that directory from
     * the old to the new name. Also check the code accessing this field.
     */
    // IF CHANGING, SEE COMMENT ABOVE!
    public static final String PROJECT_NAME = "ImageMetaDataViewer";
    /**
     * Minimum required Java version
     */
    public static final Version MIN_JAVA_VERSION = new Version(1, 6);
    /**
     * Title of the application
     */
    public static final String APP_DESCRIPTION = Bundle.getString(AppInfo.class, "AppInfo.AppDescription");
    /**
     * To mail address for bug reports
     */
    public static final String MAIL_TO_ADDRESS_BUGS = "support@jphototagger.org";
    /**
     * Localized subject for  mails
     */
    public static final String MAIL_SUBJECT_BUGS = Bundle.getString(AppInfo.class, "AppInfo.Subject.Bugs");
    /**
     * To mail address for feature requests
     */
    public static final String MAIL_TO_ADDRESS_FEATURES = "support@jphototagger.org";
    /**
     * Localized subject for  mails
     */
    public static final String MAIL_SUBJECT_FEATURES = Bundle.getString(AppInfo.class, "AppInfo.Subject.Features");
    /**
     * Localized URI to JPhotoTagger's user forum
     */
    public static final String URI_USER_FORUM = Bundle.getString(AppInfo.class, "AppInfo.URI.UserForum");
    /**
     * Localized URI to JPhotoTagger's website
     */
    public static final String URI_WEBSITE = Bundle.getString(AppInfo.class, "AppInfo.URI.Website");
    /**
     * Localized URI to JPhotoTagger's change log
     */
    public static final String URI_CHANGELOG = Bundle.getString(AppInfo.class, "AppInfo.URI.Changelog");

    private AppInfo() {
    }
}
