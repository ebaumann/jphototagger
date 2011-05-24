package org.jphototagger.program;

import org.jphototagger.lib.dialog.DirectoryChooser;
import org.jphototagger.lib.io.filefilter.DirectoryFilter;
import org.jphototagger.lib.util.PropertiesFile;
import org.jphototagger.lib.util.Settings;
import org.jphototagger.lib.util.SettingsHints;
import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.update.UpdateUserSettings;
import org.jphototagger.program.event.listener.impl.UserSettingsListenerSupport;
import org.jphototagger.program.event.listener.UserSettingsListener;
import org.jphototagger.program.event.UserSettingsEvent;
import org.jphototagger.program.event.UserSettingsEvent.Type;
import org.jphototagger.program.helper.CopyFiles;
import org.jphototagger.program.helper.CopyFiles.Options;
import org.jphototagger.program.image.thumbnail.ThumbnailCreator;
import org.jphototagger.program.types.Filename;
import java.io.File;
import java.util.logging.Level;
import java.util.Properties;

/**
 * Stores user settings in a single {@link java.util.Properties} instance.
 * <p>
 * To make changes permanent the application has to call {@link #writeToFile()}.
 * <p>
 * Getters and setters in this class are used by multiple classes, else a
 * class should use {@link #getSettings()}.
 *
 * @author Elmar Baumann
 */
public final class UserSettings {
    private static final int DEFAULT_MINUTES_TO_START_SCHEDULED_TASKS = 5;
    private static final String DOMAIN_NAME = "de.elmar_baumann"; // When changing see comment for AppInfo.PROJECT_NAME
    private static final String KEY_ACCEPT_HIDDEN_DIRECTORIES = "UserSettings.IsAcceptHiddenDirectories";
    private static final String KEY_AUTO_DOWNLOAD_NEWER_VERSIONS = "UserSettings.AutoDownloadNewerVersions";
    private static final String KEY_AUTO_SCAN_INCLUDE_SUBDIRECTORIES = "UserSettings.IsAutoscanIncludeSubdirectories";
    private static final String KEY_DATABASE_BACKUP_DIRECTORY = "UserSettings.DatabaseBackupDirectoryName";
    private static final String KEY_DATABASE_BACKUP_INTERVAL = "UserSettings.DbBackupInterval";
    private static final String KEY_DATABASE_DIRECTORY = "UserSettings.DatabaseDirectoryName";
    private static final String KEY_DATABASE_SCHEDULED_BACKUP = "UserSettings.DbScheduledBackup";
    private static final String KEY_DISPLAY_IPTC = "UserSettings.DisplayIptc";
    private static final String KEY_DISPLAY_SEARCH_BUTTON = "UserSettings.DisplaySearchButton";
    private static final String KEY_ENABLE_AUTOCOMPLETE = "UserSettings.EnableAutoComplete";
    private static final String KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS = "UserSettings.ExecuteActionsAfterImageChangeInDbAlways";
    private static final String KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP = "UserSettings.ExecuteActionsAfterImageChangeInDbIfImageHasXmp";
    private static final String KEY_EXTERNAL_THUMBNAIL_CREATION_COMMAND = "UserSettings.ExternalThumbnailCreationCommand";
    private static final String KEY_IPTC_CHARSET = "UserSettings.IptcCharset";
    private static final String KEY_LOG_LEVEL = "UserSettings.LogLevel";
    private static final String KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS = "UserSettings.MaximumSecondsToTerminateExternalPrograms";
    private static final String KEY_MAX_THUMBNAIL_WIDTH = "UserSettings.MaxThumbnailWidth";
    private static final String KEY_MINUTES_TO_START_SCHEDULED_TASKS = "UserSettings.MinutesToStartScheduledTasks";
    private static final String KEY_OPTIONS_COPY_MOVE_FILES = "UserSettings.CopyMoveFiles";
    private static final String KEY_SAVE_INPUT_EARLY = "UserSettings.SaveInputEarly";
    private static final String KEY_SCAN_FOR_EMBEDDED_XMP = "UserSettings.ScanForEmbeddedXmp";
    private static final String KEY_THUMBNAIL_CREATOR = "UserSettings.ThumbnailCreator";
    private static final String KEY_UPDATE_AUTOCOMPLETE = "UserSettings.UpdateAutocomplete";
    private static final String KEY_ADD_FILENAME_TO_GPS_LOCATION_EXPORT = "UserSettings.AddFilenameToGpsLocationExport";
    private static final String KEY_EXPERIMENTAL_FILE_FORMATS = "UserSettings.ExperimentalFileFormats";
    private static final String KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE = "UserSettings.Autocomplete.IgnoreCase";
    private static final String KEY_DISPLAY_THUMBNAIL_TOOLTIP = "UserSettings.DisplayThumbnailTooltip";
    public static final String KEY_HIDE_ROOT_FILES_FROM_DIRECTORIES_TAB = "UserSettings.HideRootFilesFromDirectoriesTab";
    public static final int MIN_THUMBNAIL_WIDTH = 50;
    public static final int MAX_THUMBNAIL_WIDTH = 400;
    public static final int DEFAULT_THUMBNAIL_WIDTH = 150;

    // NEVER CHANGE PROPERTIES_FILENAME!
    private static final String PROPERTIES_FILENAME = "Settings.properties";

    /** Field description */
    public static final SettingsHints SET_TABBED_PANE_SETTINGS =
        new SettingsHints(SettingsHints.Option.SET_TABBED_PANE_CONTENT);

    /** Field description */
    public static final UserSettings INSTANCE = new UserSettings();
    private final Properties properties = new Properties();
    private final PropertiesFile propertiesFile = new PropertiesFile(DOMAIN_NAME, AppInfo.PROJECT_NAME,
                                                      PROPERTIES_FILENAME, properties);
    private final Settings settings = new Settings(properties);
    private final UserSettingsListenerSupport listenerSupport = new UserSettingsListenerSupport();

    private UserSettings() {
        propertiesFile.readFromFile();
        UpdateUserSettings.update(properties);
        settings.removeKeysWithEmptyValues();
        writeToFile();
    }

    /**
     * Returns the properties with the user settings.
     * <p>
     * If You are modifying the properties not through a setter of this class,
     * You have to call {@link #writeToFile()} to make the changes persistent.
     *
     * @return properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Returns the settings object instanciated with the properties file of
     * this class.
     * <p>
     * If You are modifying the properties through the settings (calling a
     * setter of the class <code>Settings</code>) rather than through a setter
     * of this class, You have to call {@link #writeToFile()} to make the
     * changes persistent.
     *
     * @return settings
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Writes the properties of this settings to a file.
     * <p>
     * The setters of this class always calling this method after updating the
     * properties. If You change the properties outside
     * ({@link #getProperties()}, {@link #getSettings()}, You have to call
     * <code>writeToFile()</code> to make changes persistent.
     *
     */
    public void writeToFile() {
        try {
            propertiesFile.writeToFile();
        } catch (Exception ex) {
            AppLogger.logSevere(UserSettings.class, ex);
        }
    }

    /**
     * Returns directory name of the properties file of this settings.
     *
     * @return directory name
     */
    public String getSettingsDirectoryName() {
        return propertiesFile.getDirectoryName();
    }

    /**
     * Sets the name of the directory where the database files are stored.
     *
     *
     * @param directoryName directory name
     */
    public void setDatabaseDirectoryName(String directoryName) {
        if (directoryName == null) {
            throw new NullPointerException("directoryName == null");
        }

        settings.set(KEY_DATABASE_DIRECTORY, directoryName);
        writeToFile();
    }

    /**
     * Returns the name of the directory where the database files are stored.
     *
     * @return directory name.
     *         Default: {@link #getDefaultDatabaseDirectoryName()}.
     */
    public String getDatabaseDirectoryName() {
        return properties.containsKey(KEY_DATABASE_DIRECTORY)
               ? settings.getString(KEY_DATABASE_DIRECTORY)
               : getDefaultDatabaseDirectoryName();
    }

    /**
     * Sets the parent directory name of the directories, where the database
     * will be backed up.
     *
     * @param directoryName directory name
     */
    public void setDatabaseBackupDirectoryName(String directoryName) {
        if (directoryName == null) {
            throw new NullPointerException("directoryName == null");
        }

        settings.set(KEY_DATABASE_BACKUP_DIRECTORY, directoryName);
        writeToFile();
    }

    /**
     * Returns the parent directory name of the directories, where the database
     * will be backed up.
     *
     * @return directory name. Default: {@link #getDatabaseDirectoryName()}.
     */
    public String getDatabaseBackupDirectoryName() {
        return properties.containsKey(KEY_DATABASE_BACKUP_DIRECTORY)
               ? settings.getString(KEY_DATABASE_BACKUP_DIRECTORY)
               : getDatabaseDirectoryName();
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
        if (name == null) {
            throw new NullPointerException("name == null");
        }

        assert name.equals(Filename.FULL_PATH) || name.equals(Filename.FULL_PATH_NO_SUFFIX) : name;

        return getDatabaseDirectoryName() + File.separator + getDatabaseBasename() + (name.equals(Filename.FULL_PATH)
                ? ".data"
                : "");
    }

    /**
     * Returns the basename of the database directory: The directory where the
     * database files are stored. The basename is the directory name without its
     * parent names.
     *
     * @return basename of the database directory
     */
    public static String getDatabaseBasename() {
        return "database";
    }

    /**
     * Returns the directory name where the thumbnails are located.
     * <p>
     * This is a directory below the database directory.
     *
     * @return directory name
     */
    public String getThumbnailsDirectoryName() {
        return getDatabaseDirectoryName() + File.separator + getThumbnailDirBasename();
    }

    /**
     * Returns the basename of the thumbnails directory: A subdirectory of the
     * database location where the thumbnails are stored. The basename is the
     * directory name without its parent names.
     *
     * @return basename of the thumbnails directory
     */
    public static String getThumbnailDirBasename() {
        return "thumbnails";
    }

    /**
     * Returns the default directory chooser filter option whether to show
     * hidden directories depending on {@link #isAcceptHiddenDirectories()} .
     *
     * @return DirectoryChooser.Option.DISPLAY_HIDDEN_DIRECTORIES, if hidden
     *         directories shall be displayed, else
     *         DirectoryChooser.Option.NO_OPTION
     */
    public DirectoryChooser.Option getDirChooserOptionShowHiddenDirs() {
        return isAcceptHiddenDirectories()
               ? DirectoryChooser.Option.DISPLAY_HIDDEN_DIRECTORIES
               : DirectoryChooser.Option.NO_OPTION;
    }

    /**
     * Returns the default directory filter whether to show hidden files.
     *
     * @return DirectoryFilter.Option.ACCEPT_HIDDEN_FILES, if hidden files
     *         shall be displayed, else DirectoryFilter.Option.NO_OPTION
     */
    public DirectoryFilter.Option getDirFilterOptionShowHiddenFiles() {
        return isAcceptHiddenDirectories()
               ? DirectoryFilter.Option.ACCEPT_HIDDEN_FILES
               : DirectoryFilter.Option.NO_OPTION;
    }

    /**
     * Sets the thumbnail creator.
     *
     * @param creator thumbnail creator
     */
    public void setThumbnailCreator(ThumbnailCreator creator) {
        if (creator == null) {
            throw new NullPointerException("creator == null");
        }

        properties.put(KEY_THUMBNAIL_CREATOR, creator.name());
        writeToFile();
    }

    /**
     * Returns the thumbnail creator.
     *
     * @return thumbnail creator.
     *                   Default: {@link ThumbnailCreator#JAVA_IMAGE_IO}.
     */
    public ThumbnailCreator getThumbnailCreator() {
        return properties.containsKey(KEY_THUMBNAIL_CREATOR)
               ? ThumbnailCreator.valueOf(properties.getProperty(KEY_THUMBNAIL_CREATOR))
               : ThumbnailCreator.JAVA_IMAGE_IO;
    }

    /**
     * Sets the command line of the external program which creates the
     * thumbnails.
     *
     * @param command command line
     */
    public void setExternalThumbnailCreationCommand(String command) {
        if (command == null) {
            throw new NullPointerException("command == null");
        }

        settings.set(KEY_EXTERNAL_THUMBNAIL_CREATION_COMMAND, command);
        writeToFile();
    }

    /**
     * Returns the command line of the external program which creates the
     * thumbnails.
     *
     * @return command line or empty string when not defined
     */
    public String getExternalThumbnailCreationCommand() {
        return settings.getString(KEY_EXTERNAL_THUMBNAIL_CREATION_COMMAND);
    }

    /**
     * Sets the log level.
     *
     * @param logLevel log level
     */
    public void setLogLevel(Level logLevel) {
        if (logLevel == null) {
            throw new NullPointerException("logLevel == null");
        }

        settings.set(KEY_LOG_LEVEL, logLevel.toString());
        writeToFile();
        notifyListeners(Type.LOG_LEVEL);
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
                AppLogger.logSevere(UserSettings.class, ex);
            }
        }

        if (level == null) {
            settings.set(KEY_LOG_LEVEL, Level.INFO.getLocalizedName());
        }

        return (level == null)
               ? Level.INFO
               : level;
    }

    /**
     * Sets whether the search button shall be displayed.
     *
     * @param display true, if the search button shall be displayed
     */
    public void setDisplaySearchButton(boolean display) {
        settings.set(KEY_DISPLAY_SEARCH_BUTTON, display);
        writeToFile();
    }

    /**
     * Returns whether the search button shall be displayed.
     *
     * @return true, if the search button shall be displayed. Default: true.
     */
    public boolean isDisplaySearchButton() {
        return properties.containsKey(KEY_DISPLAY_SEARCH_BUTTON)
               ? settings.getBoolean(KEY_DISPLAY_SEARCH_BUTTON)
               : true;
    }

    /**
     * Sets whether to scan for embedded XMP metadata if no sidecar file
     * exists.
     *
     * @param scan true, when to scan image files for embedded XMP metadata
     */
    public void setScanForEmbeddedXmp(boolean scan) {
        settings.set(KEY_SCAN_FOR_EMBEDDED_XMP, scan);
        writeToFile();
    }

    /**
     * Returns whether to scan for embedded XMP metadata if no sidecar file
     * exists.
     *
     * @return true, when to scan image files for embedded XMP metadata.
     *         Default: false.
     */
    public boolean isScanForEmbeddedXmp() {
        return properties.containsKey(KEY_SCAN_FOR_EMBEDDED_XMP)
               ? settings.getBoolean(KEY_SCAN_FOR_EMBEDDED_XMP)
               : false;
    }

    /**
     * Sets the options when copying or moving files.
     *
     * @param options options
     */
    public void setCopyMoveFilesOptions(Options options) {
        if (options == null) {
            throw new NullPointerException("options == null");
        }

        settings.set(KEY_OPTIONS_COPY_MOVE_FILES, options.getInt());
        writeToFile();
    }

    /**
     * Returns the options when copying or moving files.
     *
     * @return options. Default: CONFIRM_OVERWRITE.
     */
    public CopyFiles.Options getCopyMoveFilesOptions() {
        return properties.containsKey(KEY_OPTIONS_COPY_MOVE_FILES)
               ? CopyFiles.Options.fromInt(settings.getInt(KEY_OPTIONS_COPY_MOVE_FILES))
               : CopyFiles.Options.CONFIRM_OVERWRITE;
    }

    /**
     * Sets whether actions shall be executed always after changing images in
     * the database, e.g. a user defined action which embeds XMP into the image
     * files.
     *
     * @param set true when the actions shall be executed always
     */
    public void setExecuteActionsAfterImageChangeInDbAlways(boolean set) {
        settings.set(KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS, set);
        settings.set(KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP, !set);
        writeToFile();
    }

    /**
     * Returns whether actions shall be executed always after changing images in
     * the database, e.g. a user defined action which embeds XMP into the image
     * files.
     *
     * @return true when the actions shall be executed always. Default: false.
     * @see    #isExecuteActionsAfterImageChangeInDbIfImageHasXmp()
     */
    public boolean isExecuteActionsAfterImageChangeInDbAlways() {
        return properties.containsKey(KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS)
               ? settings.getBoolean(KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS)
               : false;
    }

    /**
     * Sets whether actions shall be executed after changing images in the
     * database when the image has embbeded XMP metadata.
     *
     * @param set true when the actions shall be executed if the modified image
     *            has embedded XMP metadata
     */
    public void setExecuteActionsAfterImageChangeInDbIfImageHasXmp(boolean set) {
        settings.set(KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS, !set);
        settings.set(KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP, set);
        writeToFile();
    }

    /**
     * Returns whether actions shall be executed after changing images in the
     * database when the image has embbeded XMP metadata.
     *
     * @return true when the actions shall be executed if the modified image
     *         has embedded XMP metadata. Default: false.
     * @see    #isExecuteActionsAfterImageChangeInDbAlways()
     */
    public boolean isExecuteActionsAfterImageChangeInDbIfImageHasXmp() {
        return properties.containsKey(KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP)
               ? settings.getBoolean(KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP)
               : false;
    }

    /**
     * Sets the charset for decoding IPTC metadata strings.
     *
     * @param charset charset
     */
    public void setIptcCharset(String charset) {
        if (charset == null) {
            throw new NullPointerException("charset == null");
        }

        settings.set(KEY_IPTC_CHARSET, charset);
        writeToFile();
        notifyListeners(Type.IPTC_CHARSET);
    }

    /**
     * Returns the charset for decoding IPTC metadata strings.
     *
     * @return charset. Default: "ISO-8859-1".
     */
    public String getIptcCharset() {
        String charset = settings.getString(KEY_IPTC_CHARSET);

        return charset.isEmpty()
               ? "ISO-8859-1"
               : charset;
    }

    /**
     * Sets whether automatated scans of directories for updating the
     * database shall include subdirectories.
     *
     * @param include true if include subdirectories
     */
    public void setAutoscanIncludeSubdirectories(boolean include) {
        settings.set(KEY_AUTO_SCAN_INCLUDE_SUBDIRECTORIES, include);
        writeToFile();
    }

    /**
     * Returns whether automatated scans of directories for updating the
     * database shall include subdirectories.
     *
     * @return true if include subdirectories. Default: true.
     */
    public boolean isAutoscanIncludeSubdirectories() {
        return properties.containsKey(KEY_AUTO_SCAN_INCLUDE_SUBDIRECTORIES)
               ? settings.getBoolean(KEY_AUTO_SCAN_INCLUDE_SUBDIRECTORIES)
               : true;
    }

    /**
     * Returns whether to save input early, e.g. when an input control has been
     * leaved.
     *
     * @return true if input shall be saved early
     */
    public boolean isSaveInputEarly() {
        return properties.containsKey(KEY_SAVE_INPUT_EARLY)
               ? settings.getBoolean(KEY_SAVE_INPUT_EARLY)
               : true;
    }

    /**
     * Sets whether to save input early, e.g. when an input control has been
     * leaved.
     *
     * @param early true if input shall be saved early. Default: true.
     */
    public void setSaveInputEarly(boolean early) {
        settings.set(KEY_SAVE_INPUT_EARLY, early);
        writeToFile();
    }

    /**
     * Sets the miniutes to wait after starting before the application starts
     * the automated tasks.
     *
     * @param minutes minutes
     */
    public void setMinutesToStartScheduledTasks(int minutes) {
        settings.set(KEY_MINUTES_TO_START_SCHEDULED_TASKS, Integer.toString(minutes));
        writeToFile();
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

        return (minutes > 0)
               ? minutes
               : DEFAULT_MINUTES_TO_START_SCHEDULED_TASKS;
    }

    /**
     * Sets the maximum length of the thumbnail width.
     *
     * @param width length in pixel
     */
    public void setMaxThumbnailWidth(int width) {
        settings.set(KEY_MAX_THUMBNAIL_WIDTH, Integer.toString(width));
        writeToFile();
        notifyListeners(Type.MAX_THUMBNAIL_WIDTH);
    }

    /**
     * Returns the maximum length of the thumbnail width.
     *
     * @return maximum length in pixel. Default: Internal constant
     *         <code>DEFAULT_MAX_THUMBNAIL_LENGTH</code>.
     */
    public int getMaxThumbnailWidth() {
        int width = settings.getInt(KEY_MAX_THUMBNAIL_WIDTH);

        return (width != Integer.MIN_VALUE)
               ? width
               : DEFAULT_THUMBNAIL_WIDTH;
    }

    /**
     * Sets the maximum time to wait before terminating external programs.
     *
     * @param seconds time in seconds
     */
    public void setMaxSecondsToTerminateExternalPrograms(Integer seconds) {
        if (seconds == null) {
            throw new NullPointerException("seconds == null");
        }

        if (seconds.intValue() < 0) {
            throw new IllegalArgumentException("Invalid time: " + seconds.intValue());
        }

        settings.set(KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS, seconds);
        writeToFile();
    }

    /**
     * Returns the maximum time to wait before terminating external programs.
     *
     * @return time in seconds. Default: 60.
     */
    public int getMaxSecondsToTerminateExternalPrograms() {
        return properties.containsKey(KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS)
               ? settings.getInt(KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS)
               : 60;
    }

    /**
     * Sets, whether directory choosers and -trees shall show hidden
     * directories and if directory scans shall include them.
     *
     * @param accept true, if accepted
     */
    public void setAcceptHiddenDirectories(boolean accept) {
        settings.set(KEY_ACCEPT_HIDDEN_DIRECTORIES, accept);
        writeToFile();
    }

    /**
     * Returns whether directory choosers and -trees shall show hidden
     * directories and if directory scans shall include them.
     *
     * @return true, if accepted. Default: false.
     */
    public boolean isAcceptHiddenDirectories() {
        return properties.containsKey(KEY_ACCEPT_HIDDEN_DIRECTORIES)
               ? settings.getBoolean(KEY_ACCEPT_HIDDEN_DIRECTORIES)
               : false;
    }

    /**
     * Sets whether to check and auto download newer program versions.
     *
     * @param auto true if to check and auto download.
     *             Default: true.
     */
    public void setAutoDownloadNewerVersions(boolean auto) {
        settings.set(KEY_AUTO_DOWNLOAD_NEWER_VERSIONS, auto);
        writeToFile();
        notifyListeners(Type.CHECK_FOR_UPDATES);
    }

    /**
     * Returns whether to check and auto download newer program versions.
     *
     * @return true, if to check and auto download
     */
    public boolean isAutoDownloadNewerVersions() {
        return properties.containsKey(KEY_AUTO_DOWNLOAD_NEWER_VERSIONS)
               ? settings.getBoolean(KEY_AUTO_DOWNLOAD_NEWER_VERSIONS)
               : true;
    }

    /**
     * Sets, whether IPTC shall be displayed, if an image was selected.
     *
     * @param display true, if IPTC shall be displayed, if an image was selected
     */
    public void setDisplayIptc(boolean display) {
        settings.set(KEY_DISPLAY_IPTC, display);
        writeToFile();
        notifyListeners(Type.DISPLAY_IPTC);
    }

    /**
     * Sets whether IPTC shall be displayed, if an image was selected.
     *
     * @return true if IPTC shall be displayed, if an image was selected.
     *         Default: false.
     */
    public boolean isDisplayIptc() {
        return properties.containsKey(KEY_DISPLAY_IPTC)
               ? settings.getBoolean(KEY_DISPLAY_IPTC)
               : false;
    }

    /**
     * Sets the database backup interval in days.
     *
     * @param interval days
     */
    public void setScheduledBackupDbInterval(int interval) {
        settings.set(KEY_DATABASE_BACKUP_INTERVAL, interval);
        writeToFile();
    }

    /**
     * Returns the database backup interval in days.
     *
     * @return days or -1 if not set. Default: -1
     */
    public int getScheduledBackupDbInterval() {
        return properties.containsKey(KEY_DATABASE_BACKUP_INTERVAL)
               ? settings.getInt(KEY_DATABASE_BACKUP_INTERVAL)
               : -1;
    }

    /**
     * Sets, whether automized backups shall be scheduled.
     *
     * @param scheduled true, if automized backups shall be scheduled
     */
    public void setScheduledBackupDb(boolean scheduled) {
        settings.set(KEY_DATABASE_SCHEDULED_BACKUP, scheduled);
        writeToFile();
    }

    /**
     * Returns, whether automized backups shall be scheduled.
     *
     * @return true, if automized backups shall be scheduled. Default: false.
     */
    public boolean isScheduledBackupDb() {
        return properties.containsKey(KEY_DATABASE_SCHEDULED_BACKUP)
               ? settings.getBoolean(KEY_DATABASE_SCHEDULED_BACKUP)
               : false;
    }

    public void setAddFilenameToGpsLocationExport(boolean add) {
        settings.set(KEY_ADD_FILENAME_TO_GPS_LOCATION_EXPORT, add);
        writeToFile();
    }

    public boolean isAddFilenameToGpsLocationExport() {
        return properties.containsKey(KEY_ADD_FILENAME_TO_GPS_LOCATION_EXPORT)
               ? settings.getBoolean(KEY_ADD_FILENAME_TO_GPS_LOCATION_EXPORT)
               : false;
    }

    /**
     * Sets, whether autocomplete shall be enabled.
     *
     * @param enable true, if autocomplete shall be enabled.
     */
    public void setEnableAutocomplete(boolean enable) {
        settings.set(KEY_ENABLE_AUTOCOMPLETE, enable);
        writeToFile();
    }

    /**
     * Returns, whether autocomplete shall be enabled.
     *
     * @return true, if autocomplete shall be enabled. Default: true.
     */
    public boolean isAutocomplete() {
        return settings.containsKey(KEY_ENABLE_AUTOCOMPLETE)
               ? settings.getBoolean(KEY_ENABLE_AUTOCOMPLETE)
               : true;
    }

    /**
     * Sets whether to update autocomplete permanently based on user inputs.
     *
     * @param update true if update permanently
     */
    public void setUpdateAutocomplete(boolean update) {
        settings.set(KEY_UPDATE_AUTOCOMPLETE, update);
        writeToFile();
    }

    /**
     * Returns whether to update autocomplete permanently based on user inputs.
     *
     * @return true if update permanently
     */
    public boolean isUpdateAutocomplete() {
        return settings.containsKey(KEY_UPDATE_AUTOCOMPLETE)
               ? settings.getBoolean(KEY_UPDATE_AUTOCOMPLETE)
               : true;
    }

    /**
     * Sets whether to use experimental image file formats.
     *
     * @param b true, if to use experimental image file formats
     */
    public void setUseExperimentalFileFormats(boolean b) {
        settings.set(KEY_EXPERIMENTAL_FILE_FORMATS, b);
        writeToFile();
    }

    /**
     * Returns whether to use experimental image file formats.
     *
     * @return true if to use experimental image file formats. Default: false.
     */
    public boolean isUseExperimentalFileFormats() {
        return settings.containsKey(KEY_EXPERIMENTAL_FILE_FORMATS)
               ? settings.getBoolean(KEY_EXPERIMENTAL_FILE_FORMATS)
               : false;
    }

    public void setAutocompleteFastSearchIgnoreCase(boolean ignore) {
        settings.set(KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE, ignore);
        writeToFile();
    }

    public boolean isAutocompleteFastSearchIgnoreCase() {
        return settings.containsKey(KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE)
               ? settings.getBoolean(KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE)
               : false;
    }

    /**
     *
     * @return Default: true
     */
    public boolean isDisplayThumbnailTooltip() {
        return settings.containsKey(KEY_DISPLAY_THUMBNAIL_TOOLTIP)
                ? settings.getBoolean(KEY_DISPLAY_THUMBNAIL_TOOLTIP)
                : true;
    }

    public void setDisplayThumbnailTooltip(boolean display) {
        settings.set(KEY_DISPLAY_THUMBNAIL_TOOLTIP, display);
        writeToFile();
        notifyListeners(Type.DISPLAY_THUMBNAIL_TOOLTIP);
    }

    /**
     * Adss a listener.
     *
     * @param listener listener
     */
    public void addUserSettingsListener(UserSettingsListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        listenerSupport.add(listener);
    }

    /**
     * Removes a listener.
     *
     * @param listener listener
     */
    public void removeUserSettingsListener(UserSettingsListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        listenerSupport.remove(listener);
    }

    private void notifyListeners(UserSettingsEvent.Type type) {
        listenerSupport.notifyUserListeners(new UserSettingsEvent(type, this));
    }
}
