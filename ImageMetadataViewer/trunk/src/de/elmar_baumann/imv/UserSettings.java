package de.elmar_baumann.imv;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.app.AppInfo;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.ColumnUtil;
import de.elmar_baumann.imv.database.metadata.selections.EditColumns;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.listener.UserSettingsChangeListener;
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
 * To make changes permanent the application has to call {@link #writeToFile()}.
 * While creating an instance, this class loads the written properties if the
 * file exists.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class UserSettings implements UserSettingsChangeListener {

    private static final int DEFAULT_MAX_THUMBNAIL_LENGTH = 150;
    private static final int DEFAULT_MINUTES_TO_START_SCHEDULED_TASKS = 5;
    private static final String DELIMITER_COLUMNS = "\t"; // NOI18N
    private static final String KEY_DEFAULT_IMAGE_OPEN_APP =
            "UserSettings.DefaultImageOpenApp";
    private static final String KEY_EXTERNAL_THUMBNAIL_CREATION_COMMAND =
            "UserSettings.ExternalThumbnailCreationCommand";
    private static final String KEY_FAST_SEARCH_COLUMNS =
            "UserSettings.FastSearchColumns";
    private static final String KEY_EDIT_COLUMNS = "UserSettings.EditColumns";
    private static final String KEY_IPTC_CHARSET = "UserSettings.IptcCharset";
    private static final String KEY_ACCEPT_HIDDEN_DIRECTORIES =
            "UserSettings.IsAcceptHiddenDirectories";
    private static final String KEY_DATABASE_DIRECTORY_NAME =
            "UserSettings.DatabaseDirectoryName";
    private static final String KEY_AUTOSCAN_INCLUDE_SUBDIRECTORIES =
            "UserSettings.IsAutoscanIncludeSubdirectories";
    private static final String KEY_CREATE_THUMBNAILS_WITH_EXTERNAL_APP =
            "UserSettings.IsCreateThumbnailsWithExternalApp";
    private static final String KEY_TASK_REMOVE_RECORDS_WITH_NOT_EXISTING_FILES =
            "UserSettings.IsTaskRemoveRecordsWithNotExistingFiles";
    private static final String KEY_AUTOCOMPLETE =
            "UserSettings.IsUseAutocomplete";
    private static final String KEY_USE_EMBEDDED_THUMBNAILS =
            "UserSettings.IsUseEmbeddedThumbnails";
    private static final String KEY_LOGFILE_FORMATTER_CLASS =
            "UserSettings.LogfileFormatterClass";
    private static final String KEY_LOG_LEVEL = "UserSettings.LogLevel";
    private static final String KEY_MAX_THUMBNAIL_LENGTH =
            "UserSettings.MaxThumbnailWidth";
    private static final String KEY_MINUTES_TO_START_SCHEDULED_TASKS =
            "UserSettings.MinutesToStartScheduledTasks";
    private static final String KEY_THREAD_PRIORITY =
            "UserSettings.ThreadPriority";
    private static final String KEY_AUTOCOPY_DIRECTORY =
            "UserSettings.AutocopyDirectory";
    private static final String KEY_WEB_BROWSER = "UserSettings.WebBrowser";
    private static final String KEY_TREE_DIRECTORIES_SELECT_LAST_DIRECTORY =
            "UserSettings.TreeDirectoriesSelectLastDirectory";
    private static final String KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS =
            "UserSettings.MaximumSecondsToTerminateExternalPrograms";
    private static final String KEY_SCAN_FOR_EMBEDDED_XMP =
            "UserSettings.ScanForEmbeddedXmp";
    private static final String KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS =
            "UserSettings.ExecuteActionsAfterImageChangeInDbAlways";
    private static final String KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP =
            "UserSettings.ExecuteActionsAfterImageChangeInDbIfImageHasXmp";
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
     * Returns the properties.
     *
     * @return properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Returns a settings object instanciated with the properties file of
     * this class. The settings offering easy writing and reading different
     * types of objects.
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
     * located. When the user didn't change the directory, the database file is
     * in the same directory as the properties file.
     *
     * @return directory name
     */
    public String getDefaultDatabaseDirectoryName() {
        return getSettingsDirectoryName();
    }

    /**
     * Returns the filename of the database (complete path).
     *
     * @param  suffix true when the suffix shall be appended and false if not
     *         (e.g. to create a connection string)
     * @return filename
     */
    public String getDatabaseFileName(boolean suffix) {
        return getDatabaseDirectoryName() + File.separator + "database" +
                (suffix
                 ? ".data"
                 : "");
    }

    /**
     * Returns the directory name where the thumbnails are located. This is
     * a directory below the database directory.
     *
     * @return directory name
     */
    public String getThumbnailsDirectoryName() {
        return getDatabaseDirectoryName() + File.separator + "thumbnails";
    }

    /**
     * Writes the properties to a file.
     *
     * <em>If not called, settings are lost after exiting the program!</em>
     */
    public void writeToFile() {
        propertiesToFile.writeToFile();
    }

    /**
     * Returns the default options of a directory filter:
     *
     * <ul>
     * <li>{@link de.elmar_baumann.lib.io.DirectoryFilter.Option#ACCEPT_HIDDEN_FILES} if
     *     {@link #isAcceptHiddenDirectories()} is true
     * <li>{@link de.elmar_baumann.lib.io.DirectoryFilter.Option#REJECT_HIDDEN_FILES} if
     *     {@link #isAcceptHiddenDirectories()} is false
     * </ul>
     *
     * @return options. Default:
     *         {@link de.elmar_baumann.lib.io.DirectoryFilter.Option#REJECT_HIDDEN_FILES}
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
                AppLog.logWarning(UserSettings.class, ex);
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
     * Returns the thread priority for time consuming tasks, e.g. for rendering
     * thumbnails.
     * 
     * @return thread priority. Default: <code>5</code>
     */
    public int getThreadPriority() {
        int priority = settings.getInt(KEY_THREAD_PRIORITY);
        return priority >= 0 && priority <= 10
               ? priority
               : 5;
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
               ? "ISO-8859-1"
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
            AppLog.logWarning(UserSettings.class, ex);
            settings.setString(XMLFormatter.class.getName(),
                    KEY_LOGFILE_FORMATTER_CLASS);
        }
        return XMLFormatter.class;
    }

    /**
     * Returns whether automated tasks shall remove files from the database if
     * the doesn't exist into the filesystem.
     * 
     * @return true when removing those files from the database.
     *         Default: <code>false</code>
     */
    public boolean isTaskRemoveRecordsWithNotExistingFiles() {
        return properties.containsKey(
                KEY_TASK_REMOVE_RECORDS_WITH_NOT_EXISTING_FILES)
               ? settings.getBoolean(
                KEY_TASK_REMOVE_RECORDS_WITH_NOT_EXISTING_FILES)
               : false;
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
     * Returns wheter to activate autocomplete.
     * 
     * @return true when autocomplete shall be active. Default:
     *         <code>true</code>
     */
    public boolean isUseAutocomplete() {
        return properties.containsKey(KEY_AUTOCOMPLETE)
               ? settings.getBoolean(KEY_AUTOCOMPLETE)
               : true;
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
        } else if (type.equals(UserSettingsChangeEvent.Type.FAST_SEARCH_COLUMNS)) {
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
                UserSettingsChangeEvent.Type.IS_TASK_REMOVE_RECORDS_WITH_NOT_EXISTING_FILES)) {
            settings.setBoolean(evt.isTaskRemoveRecordsWithNotExistingFiles(),
                    KEY_TASK_REMOVE_RECORDS_WITH_NOT_EXISTING_FILES);
        } else if (type.equals(UserSettingsChangeEvent.Type.IS_USE_AUTOCOMPLETE)) {
            settings.setBoolean(evt.isAutocomplete(), KEY_AUTOCOMPLETE);
        } else if (type.equals(
                UserSettingsChangeEvent.Type.IS_USE_EMBEDDED_THUMBNAILS)) {
            writeToPropertiesUseEmbeddedThumbnails(evt.isUseEmbeddedThumbnails());
        } else if (type.equals(
                UserSettingsChangeEvent.Type.LOGFILE_FORMATTER_CLASS)) {
            writeToPropertiesLogfileFormatterClass(
                    evt.getLogfileFormatterClass());
        } else if (type.equals(UserSettingsChangeEvent.Type.LOG_LEVEL)) {
            settings.setString(evt.getLogLevel().toString(), KEY_LOG_LEVEL);
        } else if (type.equals(UserSettingsChangeEvent.Type.MAX_THUMBNAIL_WIDTH)) {
            settings.setString(evt.getMaxThumbnailWidth().toString(),
                    KEY_MAX_THUMBNAIL_LENGTH);
        } else if (type.equals(
                UserSettingsChangeEvent.Type.MINUTES_TO_START_SCHEDULED_TASKS)) {
            settings.setString(evt.getMinutesToStartScheduledTasks().toString(),
                    KEY_MINUTES_TO_START_SCHEDULED_TASKS);
        } else if (type.equals(
                UserSettingsChangeEvent.Type.NO_FAST_SEARCH_COLUMNS)) {
            properties.remove(KEY_FAST_SEARCH_COLUMNS);
        } else if (type.equals(UserSettingsChangeEvent.Type.THREAD_PRIORITY)) {
            settings.setInt(evt.getThreadPriority(), KEY_THREAD_PRIORITY);
        } else if (type.equals(UserSettingsChangeEvent.Type.AUTOCOPY_DIRECTORY)) {
            settings.setString(evt.getAutoCopyDirectory().getAbsolutePath(),
                    KEY_AUTOCOPY_DIRECTORY);
        } else if (type.equals(UserSettingsChangeEvent.Type.WEB_BROWSER)) {
            settings.setString(evt.getWebBrowser(), KEY_WEB_BROWSER);
        } else if (type.equals(
                UserSettingsChangeEvent.Type.TREE_DIRECTORIES_SELECT_LAST_DIRECTORY)) {
            settings.setBoolean(evt.isTreeDirectoriesSelectLastDirectory(),
                    KEY_TREE_DIRECTORIES_SELECT_LAST_DIRECTORY);
        } else if (type.equals(UserSettingsChangeEvent.Type.DATABASE_DIRECTORY)) {
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
        int index = classString.lastIndexOf(" ");
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
