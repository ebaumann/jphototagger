package de.elmar_baumann.imv;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.app.AppInfo;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.ColumnUtil;
import de.elmar_baumann.imv.database.metadata.selections.EditColumns;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.listener.UserSettingsChangeListener;
import de.elmar_baumann.imv.types.Filename;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.filefilter.DirectoryFilter;
import de.elmar_baumann.lib.resource.Resources;
import de.elmar_baumann.lib.util.ArrayUtil;
import de.elmar_baumann.lib.util.PropertiesFile;
import de.elmar_baumann.lib.util.Settings;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.XMLFormatter;

/**
 * Stores user settings in a single {@link java.util.Properties} instance.
 * <p>
 * To make changes permanent the application has to call {@link #writeToFile()}.
 * <p>
 * While creating an instance, this class loads the written properties if is
 * does exist.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008/10/05
 */
public final class UserSettings implements UserSettingsChangeListener {

    private static final int DEFAULT_MAX_THUMBNAIL_LENGTH = 150;
    private static final int DEFAULT_MINUTES_TO_START_SCHEDULED_TASKS = 5;
    private static final String DELIMITER_COLUMNS = "\t"; // NOI18N
    private static final String KEY_DEFAULT_IMAGE_OPEN_APP =
            "UserSettings.DefaultImageOpenApp"; // NOI18N
    private static final String KEY_EXTERNAL_THUMBNAIL_CREATION_COMMAND =
            "UserSettings.ExternalThumbnailCreationCommand"; // NOI18N
    private static final String KEY_FAST_SEARCH_COLUMNS =
            "UserSettings.FastSearchColumns"; // NOI18N
    private static final String KEY_EDIT_COLUMNS = "UserSettings.EditColumns"; // NOI18N
    private static final String KEY_IPTC_CHARSET = "UserSettings.IptcCharset"; // NOI18N
    private static final String KEY_ACCEPT_HIDDEN_DIRECTORIES =
            "UserSettings.IsAcceptHiddenDirectories"; // NOI18N
    private static final String KEY_DATABASE_DIRECTORY_NAME =
            "UserSettings.DatabaseDirectoryName"; // NOI18N
    private static final String KEY_AUTOSCAN_INCLUDE_SUBDIRECTORIES =
            "UserSettings.IsAutoscanIncludeSubdirectories"; // NOI18N
    private static final String KEY_CREATE_THUMBNAILS_WITH_EXTERNAL_APP =
            "UserSettings.IsCreateThumbnailsWithExternalApp"; // NOI18N
    private static final String KEY_USE_EMBEDDED_THUMBNAILS =
            "UserSettings.IsUseEmbeddedThumbnails"; // NOI18N
    private static final String KEY_LOGFILE_FORMATTER_CLASS =
            "UserSettings.LogfileFormatterClass"; // NOI18N
    private static final String KEY_LOG_LEVEL = "UserSettings.LogLevel"; // NOI18N
    private static final String KEY_MAX_THUMBNAIL_LENGTH =
            "UserSettings.MaxThumbnailWidth"; // NOI18N
    private static final String KEY_MINUTES_TO_START_SCHEDULED_TASKS =
            "UserSettings.MinutesToStartScheduledTasks"; // NOI18N
    private static final String KEY_AUTOCOPY_DIRECTORY =
            "UserSettings.AutocopyDirectory"; // NOI18N
    private static final String KEY_WEB_BROWSER = "UserSettings.WebBrowser"; // NOI18N
    private static final String KEY_TREE_DIRECTORIES_SELECT_LAST_DIRECTORY =
            "UserSettings.TreeDirectoriesSelectLastDirectory"; // NOI18N
    private static final String KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS =
            "UserSettings.MaximumSecondsToTerminateExternalPrograms"; // NOI18N
    private static final String KEY_SCAN_FOR_EMBEDDED_XMP =
            "UserSettings.ScanForEmbeddedXmp"; // NOI18N
    private static final String KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS =
            "UserSettings.ExecuteActionsAfterImageChangeInDbAlways"; // NOI18N
    private static final String KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP =
            "UserSettings.ExecuteActionsAfterImageChangeInDbIfImageHasXmp"; // NOI18N
    private static final String DOMAIN_NAME = "de.elmar_baumann"; // NOI18N NEVER CHANGE!
    private static final String PROPERTIES_FILENAME = "Settings.properties"; // NOI18N NEVER CHANGE!
    private final Properties properties = new Properties();
    private final Settings settings = new Settings(properties);
    private final PropertiesFile propertiesToFile = new PropertiesFile(
            DOMAIN_NAME, AppInfo.getProjectName(), PROPERTIES_FILENAME,
            properties);
    public static final UserSettings INSTANCE = new UserSettings();

    private UserSettings() {
        propertiesToFile.readFromFile();
        settings.removeEmptyKeys();
        Resources.INSTANCE.setProperties(properties);
        Resources.INSTANCE.setIconImagesPath(AppIcons.getAppIconPaths());
    }

    /**
     * Returns the properties with the user settings.
     *
     * @return properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Returns a settings object instanciated with the properties file of
     * this class.
     * <p>
     * The settings are offering easy reading and writing different types of
     * objects.
     *
     * @return settings
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Returns directory name of the propertie's file .
     *
     * @return directory name
     */
    public String getSettingsDirectoryName() {
        return propertiesToFile.getDirectoryName();
    }

    /**
     * Returns the name of the directory where the database file is located.
     *
     * @return directory name
     */
    public String getDatabaseDirectoryName() {
        return properties.containsKey(KEY_DATABASE_DIRECTORY_NAME)
               ? settings.getString(KEY_DATABASE_DIRECTORY_NAME)
               : getDefaultDatabaseDirectoryName();
    }

    /**
     * Returns the default name of the directory where the database file is
     * located.
     * <p>
     * When the user doesn't change the directory, the database file is
     * in the same directory as the properties file.
     *
     * @return directory name
     */
    public String getDefaultDatabaseDirectoryName() {
        return getSettingsDirectoryName();
    }

    /**
     * Returns the filename of the database.
     *
     * @param  name name token. Has to be {@link Filename#FULL_PATH} or
     *              {@link Filename#FULL_PATH_NO_SUFFIX}.
     * @return      filename
     */
    public String getDatabaseFileName(Filename name) {

        assert name.equals(Filename.FULL_PATH) ||
                name.equals(Filename.FULL_PATH_NO_SUFFIX) : name;

        return getDatabaseDirectoryName() + File.separator + "database" + // NOI18N
                (name.equals(Filename.FULL_PATH)
                 ? ".data" // NOI18N
                 : ""); // NOI18N
    }

    /**
     * Returns the directory name where the thumbnails are located.
     * <p>
     * This is a directory below the database directory.
     *
     * @return directory name
     */
    public String getThumbnailsDirectoryName() {
        return getDatabaseDirectoryName() + File.separator + "thumbnails"; // NOI18N
    }

    /**
     * Writes the properties to a file.
     *<p>
     * <em>If not called, settings are lost after exiting the program!</em>
     */
    public void writeToFile() {
        propertiesToFile.writeToFile();
    }

    /**
     * Returns the default options of a directory filter:
     *
     * <ul>
     * <li>{@link de.elmar_baumann.lib.io.filefilter.DirectoryFilter.Option#ACCEPT_HIDDEN_FILES} if
     *     {@link #isAcceptHiddenDirectories()} is true
     * <li>{@link de.elmar_baumann.lib.io.filefilter.DirectoryFilter.Option#REJECT_HIDDEN_FILES} if
     *     {@link #isAcceptHiddenDirectories()} is false
     * </ul>
     *
     * @return options. Default:
     *         {@link de.elmar_baumann.lib.io.filefilter.DirectoryFilter.Option#REJECT_HIDDEN_FILES}
     */
    public Set<DirectoryFilter.Option> getDefaultDirectoryFilterOptions() {
        return EnumSet.of(isAcceptHiddenDirectories()
                          ? DirectoryFilter.Option.ACCEPT_HIDDEN_FILES
                          : DirectoryFilter.Option.REJECT_HIDDEN_FILES);
    }

    /**
     * Returns the default options of a file chooser's file filter:
     *
     * <ul>
     * <li>{@link de.elmar_baumann.lib.dialog.DirectoryChooser.Option#ACCEPT_HIDDEN_DIRECTORIES} if
     *     {@link #isAcceptHiddenDirectories()} is true
     * <li>{@link de.elmar_baumann.lib.dialog.DirectoryChooser.Option#REJECT_HIDDEN_DIRECTORIES} if
     *     {@link #isAcceptHiddenDirectories()} is false
     * </ul>
     * @return options. Default:
     *         {@link de.elmar_baumann.lib.dialog.DirectoryChooser.Option#REJECT_HIDDEN_DIRECTORIES}
     */
    public Set<DirectoryChooser.Option> getDefaultDirectoryChooserOptions() {
        return EnumSet.of(isAcceptHiddenDirectories()
                          ? DirectoryChooser.Option.ACCEPT_HIDDEN_DIRECTORIES
                          : DirectoryChooser.Option.REJECT_HIDDEN_DIRECTORIES);
    }

    /**
     * Returns wheter to create thumbnails with an external application.
     * 
     * @return true if an external application shall create the thumbnails.
     *         Default: <code>false</code>
     * @see    #getExternalThumbnailCreationCommand() 
     */
    public boolean isCreateThumbnailsWithExternalApp() {
        return properties.containsKey(KEY_CREATE_THUMBNAILS_WITH_EXTERNAL_APP)
               ? settings.getBoolean(KEY_CREATE_THUMBNAILS_WITH_EXTERNAL_APP)
               : false;
    }

    /**
     * Returns the command line of the external program which creates the
     * thumbnails.
     * 
     * @return command line or empty string when not defined
     * @see    #isCreateThumbnailsWithExternalApp()
     */
    public String getExternalThumbnailCreationCommand() {
        return settings.getString(KEY_EXTERNAL_THUMBNAIL_CREATION_COMMAND);
    }

    /**
     * Returns the path to the web browser.
     *
     * @return path (filename) or empty string if not defined
     */
    public String getWebBrowser() {
        return settings.getString(KEY_WEB_BROWSER);
    }

    /**
     * Returns the log level.
     * 
     * @return log level as returned from {@link Level#getLocalizedName()}.
     *         Default: {@link Level#INFO}
     */
    public Level getLogLevel() {
        Level level = null;
        if (properties.containsKey(KEY_LOG_LEVEL)) {
            String levelString = settings.getString(KEY_LOG_LEVEL);
            try {
                level = Level.parse(levelString);
            } catch (Exception ex) {
                AppLog.logSevere(UserSettings.class, ex);
            }
        }
        if (level == null) {
            settings.setString(Level.INFO.getLocalizedName(), KEY_LOG_LEVEL);
        }
        return level == null
               ? Level.INFO
               : level;
    }

    /**
     * Returns the columns to include into the fast search.
     * 
     * @return columns. Default: Empty list
     */
    public List<Column> getFastSearchColumns() {
        List<Column> columns = new ArrayList<Column>();
        if (!settings.getString(KEY_FAST_SEARCH_COLUMNS).isEmpty()) {
            List<String> columnKeys = ArrayUtil.stringTokenToList(
                    settings.getString(KEY_FAST_SEARCH_COLUMNS),
                    DELIMITER_COLUMNS);
            return ColumnUtil.columnKeysToColumns(columnKeys);
        }
        return columns;
    }

    /**
     * Returns the edit columns the user want to see in the edit columns panel
     * array.
     * 
     * @return edit columns
     */
    public List<Column> getEditColumns() {
        if (!settings.getString(KEY_EDIT_COLUMNS).isEmpty()) {
            List<String> columnKeys = ArrayUtil.stringTokenToList(
                    settings.getString(KEY_EDIT_COLUMNS), DELIMITER_COLUMNS);
            return ColumnUtil.columnKeysToColumns(columnKeys);
        }
        return new ArrayList<Column>(EditColumns.getColumns());
    }

    /**
     * Returns the path to the application which opens images when double
     * clicking onto a thumbnail.
     * 
     * @return path or empty string if not defined
     */
    public String getDefaultImageOpenApp() {
        return settings.getString(KEY_DEFAULT_IMAGE_OPEN_APP);
    }

    /**
     * Returns whether a default image open application is defined.
     * 
     * @return true if defined
     */
    public boolean hasDefaultImageOpenApp() {
        return !getDefaultImageOpenApp().isEmpty();
    }

    /**
     * Returns the maximum length of the thumbnail width.
     * 
     * @return maximum length in pixel. Default: Internal constant
     *         <code>DEFAULT_MAX_THUMBNAIL_LENGTH</code>.
     */
    public int getMaxThumbnailLength() {
        int width = settings.getInt(KEY_MAX_THUMBNAIL_LENGTH);
        return width != Integer.MIN_VALUE
               ? width
               : DEFAULT_MAX_THUMBNAIL_LENGTH;
    }

    /**
     * Returns wheter to use in image files embedded thumbnails rather than
     * rendering them.
     * 
     * @return true, if when embedded thumbnails shall be used.
     *         Default: <code>false</code>
     */
    public boolean isUseEmbeddedThumbnails() {
        return properties.containsKey(KEY_USE_EMBEDDED_THUMBNAILS)
               ? settings.getBoolean(KEY_USE_EMBEDDED_THUMBNAILS)
               : false;
    }

    /**
     * Returns whether to scan for embedded XMP metadata if no sidecar file
     * exists.
     *
     * @return true, when to scan image files for embedded XMP metadata.
     *         Default: <code>false</code>.
     */
    public boolean isScanForEmbeddedXmp() {
        return properties.containsKey(KEY_SCAN_FOR_EMBEDDED_XMP)
               ? settings.getBoolean(KEY_SCAN_FOR_EMBEDDED_XMP)
               : false;
    }

    /**
     * Returns wheter actions shall be executed always after changing images in
     * the database, e.g. a user defined action which embeds XMP into the image
     * files.
     *
     * @return true when the actions shall be executed always.
     *         Default: <code>false</code>
     * @see    #isExecuteActionsAfterImageChangeInDbIfImageHasXmp()
     */
    public boolean isExecuteActionsAfterImageChangeInDbAlways() {
        return properties.containsKey(
                KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS)
               ? settings.getBoolean(
                KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS)
               : false;
    }

    /**
     * Returns wheter actions shall be executed after changing images in the
     * database when the image has embbeded XMP metadata.
     *
     * @return true when the actions shall be executed if the modified image
     *         has embedded XMP metadata. Default: <code>false</code>
     * @see    #isExecuteActionsAfterImageChangeInDbAlways()
     */
    public boolean isExecuteActionsAfterImageChangeInDbIfImageHasXmp() {
        return properties.containsKey(
                KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP)
               ? settings.getBoolean(
                KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP)
               : false;
    }

    /**
     * Returns the charset for decoding IPTC metadata strings.
     * 
     * @return charset. Default: <code>"ISO-8859-1"</code>
     */
    public String getIptcCharset() {
        String charset = settings.getString(KEY_IPTC_CHARSET);
        return charset.isEmpty()
               ? "ISO-8859-1" // NOI18N
               : charset;
    }

    /**
     * Returns whether automatated scans of directories for updating the
     * database shall include subdirectories.
     * 
     * @return true if include subdirectories. Default: <code>true</code>
     */
    public boolean isAutoscanIncludeSubdirectories() {
        return properties.containsKey(KEY_AUTOSCAN_INCLUDE_SUBDIRECTORIES)
               ? settings.getBoolean(KEY_AUTOSCAN_INCLUDE_SUBDIRECTORIES)
               : true;
    }

    /**
     * Returns whether in the directories tree the last selected item shall be
     * selected while starting the application (only when the last selected
     * tab is the directories tab).
     *
     * @return true if select the last selected item. Default: <code>false</code>
     */
    public boolean isTreeDirectoriesSelectLastDirectory() {
        return properties.containsKey(KEY_TREE_DIRECTORIES_SELECT_LAST_DIRECTORY)
               ? settings.getBoolean(KEY_TREE_DIRECTORIES_SELECT_LAST_DIRECTORY)
               : false;
    }

    /**
     * Returns the class object of the logfile formatter.
     * 
     * @return Class object of the logfile formatter.
     *         Default: {@link XMLFormatter}
     */
    public Class getLogfileFormatterClass() {
        String className = settings.getString(KEY_LOGFILE_FORMATTER_CLASS);
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            AppLog.logSevere(UserSettings.class, ex);
            settings.setString(XMLFormatter.class.getName(),
                    KEY_LOGFILE_FORMATTER_CLASS);
        }
        return XMLFormatter.class;
    }

    /**
     * Returns the miniutes to wait after starting before the application starts
     * the automated tasks.
     * 
     * @return minutes. Default: Internal constant
     *         <code>DEFAULT_MINUTES_TO_START_SCHEDULED_TASKS</code>
     */
    public int getMinutesToStartScheduledTasks() {
        int minutes = settings.getInt(KEY_MINUTES_TO_START_SCHEDULED_TASKS);
        return minutes > 0
               ? minutes
               : DEFAULT_MINUTES_TO_START_SCHEDULED_TASKS;
    }

    /**
     * Returns the maximum time to wait before terminating external programs.
     *
     * @return time in seconds. Default: <code>60</code>.
     */
    public int getMaxSecondsToTerminateExternalPrograms() {
        return properties.containsKey(
                KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS)
               ? settings.getInt(KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS)
               : 60;
    }

    /**
     * Returns whether directory choosers and -trees shall show hidden
     * directories and if directory scans shall include them.
     * 
     * @return true, if accepted. Default: <code>false</code>
     */
    public boolean isAcceptHiddenDirectories() {
        return properties.containsKey(KEY_ACCEPT_HIDDEN_DIRECTORIES)
               ? settings.getBoolean(KEY_ACCEPT_HIDDEN_DIRECTORIES)
               : false;
    }

    /**
     * Returns the autocopy directory, a source directory from which all
     * image files should be copied to another directory automatically.
     * 
     * @return Existing directory or null if not defined or not existing.
     *         Default: <code>null</code>
     */
    public File getAutocopyDirectory() {
        File dir = new File(settings.getString(KEY_AUTOCOPY_DIRECTORY));
        return dir.exists() && dir.isDirectory()
               ? dir
               : null;
    }

    @Override
    public void applySettings(UserSettingsChangeEvent evt) {
        writeProperties(evt);
    }

    /**
     * Changes the properties to apply changed user settings. Does <em>not</em>
     * write them persistent into the file system.
     *
     * @param evt user settings change event
     */
    private void writeProperties(UserSettingsChangeEvent evt) {
        UserSettingsChangeEvent.Type type = evt.getType();
        if (type.equals(UserSettingsChangeEvent.Type.DEFAULT_IMAGE_OPEN_APP)) {
            settings.setString(evt.getDefaultImageOpenApp().getAbsolutePath(),
                    KEY_DEFAULT_IMAGE_OPEN_APP);
        } else if (type.equals(
                UserSettingsChangeEvent.Type.EXTERNAL_THUMBNAIL_CREATION_COMMAND)) {
            settings.setString(evt.getExternalThumbnailCreationCommand(),
                    KEY_EXTERNAL_THUMBNAIL_CREATION_COMMAND);
        } else if (type.equals(
                UserSettingsChangeEvent.Type.FAST_SEARCH_COLUMNS)) {
            settings.setString(getColumnKeys(evt.getFastSearchColumns()),
                    KEY_FAST_SEARCH_COLUMNS);
        } else if (type.equals(UserSettingsChangeEvent.Type.EDIT_COLUMNS)) {
            settings.setString(getColumnKeys(evt.getEditColumns()),
                    KEY_EDIT_COLUMNS);
        } else if (type.equals(UserSettingsChangeEvent.Type.IPTC_CHARSET)) {
            settings.setString(evt.getIptcCharset(), KEY_IPTC_CHARSET);
        } else if (type.equals(
                UserSettingsChangeEvent.Type.IS_ACCEPT_HIDDEN_DIRECTORIES)) {
            settings.setBoolean(evt.isAcceptHiddenDirectories(),
                    KEY_ACCEPT_HIDDEN_DIRECTORIES);
        } else if (type.equals(
                UserSettingsChangeEvent.Type.IS_AUTSCAN_INCLUDE_DIRECTORIES)) {
            settings.setBoolean(evt.isAutoscanIncludeSubdirectories(),
                    KEY_AUTOSCAN_INCLUDE_SUBDIRECTORIES);
        } else if (type.equals(
                UserSettingsChangeEvent.Type.IS_CREATE_THUMBNAILS_WITH_EXTERNAL_APP)) {
            writeToPropertiesCreateThumbnailsWithExternalApp(evt.
                    isCreateThumbnailsWithExternalApp());
        } else if (type.equals(
                UserSettingsChangeEvent.Type.IS_USE_EMBEDDED_THUMBNAILS)) {
            writeToPropertiesUseEmbeddedThumbnails(evt.isUseEmbeddedThumbnails());
        } else if (type.equals(
                UserSettingsChangeEvent.Type.LOGFILE_FORMATTER_CLASS)) {
            writeToPropertiesLogfileFormatterClass(
                    evt.getLogfileFormatterClass());
        } else if (type.equals(UserSettingsChangeEvent.Type.LOG_LEVEL)) {
            settings.setString(evt.getLogLevel().toString(), KEY_LOG_LEVEL);
        } else if (type.equals(
                UserSettingsChangeEvent.Type.MAX_THUMBNAIL_WIDTH)) {
            settings.setString(evt.getMaxThumbnailWidth().toString(),
                    KEY_MAX_THUMBNAIL_LENGTH);
        } else if (type.equals(
                UserSettingsChangeEvent.Type.MINUTES_TO_START_SCHEDULED_TASKS)) {
            settings.setString(evt.getMinutesToStartScheduledTasks().toString(),
                    KEY_MINUTES_TO_START_SCHEDULED_TASKS);
        } else if (type.equals(
                UserSettingsChangeEvent.Type.NO_FAST_SEARCH_COLUMNS)) {
            properties.remove(KEY_FAST_SEARCH_COLUMNS);
        } else if (type.equals(
                UserSettingsChangeEvent.Type.AUTOCOPY_DIRECTORY)) {
            settings.setString(evt.getAutoCopyDirectory().getAbsolutePath(),
                    KEY_AUTOCOPY_DIRECTORY);
        } else if (type.equals(UserSettingsChangeEvent.Type.WEB_BROWSER)) {
            settings.setString(evt.getWebBrowser(), KEY_WEB_BROWSER);
        } else if (type.equals(
                UserSettingsChangeEvent.Type.TREE_DIRECTORIES_SELECT_LAST_DIRECTORY)) {
            settings.setBoolean(evt.isTreeDirectoriesSelectLastDirectory(),
                    KEY_TREE_DIRECTORIES_SELECT_LAST_DIRECTORY);
        } else if (type.equals(
                UserSettingsChangeEvent.Type.DATABASE_DIRECTORY)) {
            settings.setString(evt.getDatabaseDirectoryName(),
                    KEY_DATABASE_DIRECTORY_NAME);
        } else if (type.equals(
                UserSettingsChangeEvent.Type.MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS)) {
            settings.setInt(evt.getMaxSecondsToTerminateExternalPrograms(),
                    KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS);
        } else if (type.equals(
                UserSettingsChangeEvent.Type.SCAN_FOR_EMBEDDED_XMP)) {
            settings.setBoolean(evt.isScanForEmbeddedXmp(),
                    KEY_SCAN_FOR_EMBEDDED_XMP);
        } else if (type.equals(
                UserSettingsChangeEvent.Type.EXECUTE_ACTION_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS)) {
            settings.setBoolean(true,
                    KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS);
            settings.setBoolean(false,
                    KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP);
        } else if (type.equals(
                UserSettingsChangeEvent.Type.EXECUTE_ACTION_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP)) {
            settings.setBoolean(false,
                    KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS);
            settings.setBoolean(true,
                    KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP);
        }
        writeToFile();
    }

    private void writeToPropertiesCreateThumbnailsWithExternalApp(boolean create) {
        settings.setBoolean(create, KEY_CREATE_THUMBNAILS_WITH_EXTERNAL_APP);
        if (create) {
            settings.setBoolean(false, KEY_USE_EMBEDDED_THUMBNAILS);
        }
    }

    private void writeToPropertiesLogfileFormatterClass(Class formatterClass) {
        String classString = formatterClass.toString();
        int index = classString.lastIndexOf(" "); // NOI18N
        settings.setString(index >= 0 && index + 1 < classString.length()
                           ? classString.substring(index + 1)
                           : XMLFormatter.class.getName(),
                KEY_LOGFILE_FORMATTER_CLASS);
    }

    private void writeToPropertiesUseEmbeddedThumbnails(boolean use) {
        settings.setBoolean(use, KEY_USE_EMBEDDED_THUMBNAILS);
        if (use) {
            settings.setBoolean(false, KEY_CREATE_THUMBNAILS_WITH_EXTERNAL_APP);
        }
    }

    private String getColumnKeys(List<Column> columns) {
        StringBuffer buffer = new StringBuffer();
        for (Column column : columns) {
            buffer.append(column.getKey() + DELIMITER_COLUMNS);
        }
        return buffer.toString();
    }
}
