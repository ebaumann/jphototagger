package de.elmar_baumann.imv;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.app.AppInfo;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.ColumnUtil;
import de.elmar_baumann.imv.database.metadata.selections.EditColumns;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.UserSettingsChangeListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.DirectoryFilter;
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
 * When creating an instance, this class loads the written properties if the
 * file exists.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class UserSettings implements UserSettingsChangeListener {

    private static final int DEFAULT_MAX_THUMBNAIL_LENGTH = 150;
    private static final int DEFAULT_MINUTES_TO_START_SCHEDULED_TASKS = 5;
    private static final String DELIMITER_COLUMNS = "\t"; // NOI18N
    private static final String KEY_DEFAULT_IMAGE_OPEN_APP = "UserSettings.DefaultImageOpenApp";
    private static final String KEY_EXTERNAL_THUMBNAIL_CREATION_COMMAND = "UserSettings.ExternalThumbnailCreationCommand";
    private static final String KEY_FAST_SEARCH_COLUMNS = "UserSettings.FastSearchColumns";
    private static final String KEY_EDIT_COLUMNS = "UserSettings.EditColumns";
    private static final String KEY_IPTC_CHARSET = "UserSettings.IptcCharset";
    private static final String KEY_ACCEPT_HIDDEN_DIRECTORIES = "UserSettings.IsAcceptHiddenDirectories";
    private static final String KEY_DATABASE_DIRECTORY_NAME = "UserSettings.DatabaseDirectoryName";
    private static final String KEY_AUTOSCAN_INCLUDE_SUBDIRECTORIES = "UserSettings.IsAutoscanIncludeSubdirectories";
    private static final String KEY_CREATE_THUMBNAILS_WITH_EXTERNAL_APP = "UserSettings.IsCreateThumbnailsWithExternalApp";
    private static final String KEY_TASK_REMOVE_RECORDS_WITH_NOT_EXISTING_FILES = "UserSettings.IsTaskRemoveRecordsWithNotExistingFiles";
    private static final String KEY_AUTOCOMPLETE = "UserSettings.IsUseAutocomplete";
    private static final String KEY_USE_EMBEDDED_THUMBNAILS = "UserSettings.IsUseEmbeddedThumbnails";
    private static final String KEY_LOGFILE_FORMATTER_CLASS = "UserSettings.LogfileFormatterClass";
    private static final String KEY_LOG_LEVEL = "UserSettings.LogLevel";
    private static final String KEY_MAX_THUMBNAIL_LENGTH = "UserSettings.MaxThumbnailWidth";
    private static final String KEY_MINUTES_TO_START_SCHEDULED_TASKS = "UserSettings.MinutesToStartScheduledTasks";
    private static final String KEY_THREAD_PRIORITY = "UserSettings.ThreadPriority";
    private static final String KEY_AUTOCOPY_DIRECTORY = "UserSettings.AutocopyDirectory";
    private static final String KEY_WEB_BROWSER = "UserSettings.WebBrowser";
    private static final String KEY_TREE_DIRECTORIES_SELECT_LAST_DIRECTORY = "UserSettings.TreeDirectoriesSelectLastDirectory";
    private static final String KEY_OPEN_SYSTEM_OUTPUT_WINDOW_AFTER_START = "UserSettings.OpenSystemOutputWindowAfterStart";
    private static final String KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS = "UserSettings.MaximumSecondsToTerminateExternalPrograms";
    private static final String DOMAIN_NAME = "de.elmar_baumann"; // NOI18N NEVER CHANGE!
    private static final String PROPERTIES_FILENAME = "Settings.properties"; // NOI18N NEVER CHANGE!
    private final Properties properties = new Properties();
    private final Settings settings = new Settings(properties);
    private final PropertiesFile propertiesToFile = new PropertiesFile(DOMAIN_NAME, AppInfo.getProjectName(), PROPERTIES_FILENAME, properties);
    private boolean isWrittenToFile = false;
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

    public String getDatabaseDirectoryName() {
        return properties.containsKey(KEY_DATABASE_DIRECTORY_NAME)
                ? settings.getString(KEY_DATABASE_DIRECTORY_NAME)
                : getDefaultDatabaseDirectoryName();
    }

    public String getDefaultDatabaseDirectoryName() {
        return getSettingsDirectoryName();
    }

    public String getDatabaseFileName() {
        return getDatabaseDirectoryName() + File.separator + "database";
    }

    public String getThumbnailsDirectoryName() {
        return getDatabaseDirectoryName() + File.separator + "thumbnails";
    }

    /**
     * Writes the properties to a file. If not called, settings are lost after
     * exiting the program.
     */
    public void writeToFile() {
        propertiesToFile.writeToFile();
        isWrittenToFile = true;
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
     * @return default options
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
     * @return default options
     */
    public Set<DirectoryChooser.Option> getDefaultDirectoryChooserOptions() {
        return EnumSet.of(isAcceptHiddenDirectories()
                ? DirectoryChooser.Option.ACCEPT_HIDDEN_DIRECTORIES
                : DirectoryChooser.Option.REJECT_HIDDEN_DIRECTORIES);
    }

    /**
     * Liefert, ob die Thumbnails von einer externen Anwendung erzeugt werden.
     * 
     * @return true, wenn die Thumbnails von einer externen Anwendung erzeugt
     *         werden sollen
     * @see    #getExternalThumbnailCreationCommand() 
     */
    public boolean isCreateThumbnailsWithExternalApp() {
        return properties.containsKey(KEY_CREATE_THUMBNAILS_WITH_EXTERNAL_APP)
                ? settings.getBoolean(KEY_CREATE_THUMBNAILS_WITH_EXTERNAL_APP)
                : false;
    }

    /**
     * Liefert die Befehlszeile des externen Programms, das die Thumbnails
     * erzeugt.
     * 
     * @return Befehlszeile
     * @see    #isCreateThumbnailsWithExternalApp()
     */
    public String getExternalThumbnailCreationCommand() {
        return settings.getString(KEY_EXTERNAL_THUMBNAIL_CREATION_COMMAND);
    }

    public String getWebBrowser() {
        return settings.getString(KEY_WEB_BROWSER);
    }

    /**
     * Liefert den Loglevel.
     * 
     * @return Loglevel (Eine Ausgabe von getLocalizedName())
     * @see    java.util.logging.Level#getLocalizedName()
     */
    public Level getLogLevel() {
        String levelString = settings.getString(KEY_LOG_LEVEL);
        Level level = null;
        try {
            level = Level.parse(levelString);
        } catch (Exception ex) {
            AppLog.logWarning(UserSettings.class, ex);
            settings.setString(Level.WARNING.getLocalizedName(), KEY_LOG_LEVEL);
        }
        return level == null ? Level.WARNING : level;
    }

    /**
     * Liefert alle Spalten für die Schnellsuche.
     * 
     * @return Suchspalten
     */
    public List<Column> getFastSearchColumns() {
        List<Column> columns = new ArrayList<Column>();
        if (!settings.getString(KEY_FAST_SEARCH_COLUMNS).isEmpty()) {
            List<String> columnKeys = ArrayUtil.stringTokenToList(
                    settings.getString(KEY_FAST_SEARCH_COLUMNS), DELIMITER_COLUMNS);
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
     * Liefert die Anwendung, die ein Bild bei Doppelklick öffnen soll.
     * 
     * @return Anwendung oder Leerstring, wenn nicht definiert
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
     * Liefert die Priorität für Threads zum Erzeugen von Thumbnails und Metadaten.
     * 
     * @return Threadpriorität
     */
    public int getThreadPriority() {
        int priority = settings.getInt(KEY_THREAD_PRIORITY);
        return priority >= 0 && priority <= 10 ? priority : 5;
    }

    /**
     * Liefert die maximale Seitenlänge (längere Seite) von Thumbnails,
     * die skaliert werden.
     * 
     * @return Seitenlänge in Pixel
     */
    public int getMaxThumbnailLength() {
        int width = settings.getInt(KEY_MAX_THUMBNAIL_LENGTH);
        return width != Integer.MIN_VALUE ? width : DEFAULT_MAX_THUMBNAIL_LENGTH;
    }

    /**
     * Liefert, ob eingebettete Thumbnails benutzt werden sollen.
     * 
     * @return true, wenn eingebettete Thumbnails benutzt werden sollen
     */
    public boolean isUseEmbeddedThumbnails() {
        return properties.containsKey(KEY_USE_EMBEDDED_THUMBNAILS)
                ? settings.getBoolean(KEY_USE_EMBEDDED_THUMBNAILS)
                : false;
    }

    /**
     * Liefert den Zeichensatz, mit dem IPTC-Daten dekodiert werden sollen.
     * 
     * @return Zeichensatz
     */
    public String getIptcCharset() {
        String charset = settings.getString(KEY_IPTC_CHARSET);
        return charset.isEmpty() ? "ISO-8859-1" : charset;
    }

    /**
     * Liefert, ob beim automatischen Scan von Verzeichnissen auch die
     * Unterverzeichnisse einbezogen werden sollen.
     * 
     * @return true, falls die Unterverzeichnisse einbezogen werden sollen
     */
    public boolean isAutoscanIncludeSubdirectories() {
        return properties.containsKey(KEY_AUTOSCAN_INCLUDE_SUBDIRECTORIES)
                ? settings.getBoolean(KEY_AUTOSCAN_INCLUDE_SUBDIRECTORIES)
                : true;
    }

    public boolean isTreeDirectoriesSelectLastDirectory() {
        return properties.containsKey(KEY_TREE_DIRECTORIES_SELECT_LAST_DIRECTORY)
                ? settings.getBoolean(KEY_TREE_DIRECTORIES_SELECT_LAST_DIRECTORY)
                : false;
    }

    /**
     * Liefert die Klasse des Logdateiformatierers.
     * 
     * @return Logdateiformatierer
     */
    public Class getLogfileFormatterClass() {
        String className = settings.getString(KEY_LOGFILE_FORMATTER_CLASS);
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            AppLog.logWarning(UserSettings.class, ex);
            settings.setString(XMLFormatter.class.getName(), KEY_LOGFILE_FORMATTER_CLASS);
        }
        return XMLFormatter.class;
    }

    /**
     * Liefert, ob zu den geplanten Tasks die Aufgabe gehört: Datensätze löschen,
     * wenn eine Datei nicht mehr existiert.
     * 
     * @return true, wenn dieser Task ausgeführt werden soll
     */
    public boolean isTaskRemoveRecordsWithNotExistingFiles() {
        return properties.containsKey(KEY_TASK_REMOVE_RECORDS_WITH_NOT_EXISTING_FILES)
                ? settings.getBoolean(KEY_TASK_REMOVE_RECORDS_WITH_NOT_EXISTING_FILES)
                : false;
    }

    /**
     * Liefert die Minuten, bevor geplante Tasks starten.
     * 
     * @return Minuten
     */
    public int getMinutesToStartScheduledTasks() {
        int minutes = settings.getInt(KEY_MINUTES_TO_START_SCHEDULED_TASKS);
        return minutes > 0 ? minutes : DEFAULT_MINUTES_TO_START_SCHEDULED_TASKS;
    }

    /**
     * Liefert, ob Autocomplete eingeschaltet werden soll.
     * 
     * @return true, wenn Autocomplete eingeschaltet werden soll
     */
    public boolean isUseAutocomplete() {
        return properties.containsKey(KEY_AUTOCOMPLETE)
                ? settings.getBoolean(KEY_AUTOCOMPLETE)
                : true;
    }

    /**
     * Returns whether to open the output window for system outputs after
     * starting the program.
     *
     * @return true, if the window is to open. Default: false
     */
    public boolean isOpenSystemOutputAfterStart() {
        return properties.containsKey(KEY_OPEN_SYSTEM_OUTPUT_WINDOW_AFTER_START)
                ? settings.getBoolean(KEY_OPEN_SYSTEM_OUTPUT_WINDOW_AFTER_START)
                : false;
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
     * Returns whether directory choosers and -trees shall show hidden
     * directories and if directory scans shall include them.
     * 
     * @return true, if accepted
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
     * @return Existing directory or null if not defined or not existing
     */
    public File getAutocopyDirectory() {
        File dir = new File(settings.getString(KEY_AUTOCOPY_DIRECTORY));
        return dir.exists() && dir.isDirectory() ? dir : null;
    }

    @Override
    public void applySettings(UserSettingsChangeEvent evt) {
        writeProperties(evt);
    }

    private void writeProperties(UserSettingsChangeEvent evt) {
        UserSettingsChangeEvent.Type type = evt.getType();
        if (type.equals(UserSettingsChangeEvent.Type.DEFAULT_IMAGE_OPEN_APP)) {
            settings.setString(evt.getDefaultImageOpenApp().getAbsolutePath(), KEY_DEFAULT_IMAGE_OPEN_APP);
        } else if (type.equals(UserSettingsChangeEvent.Type.EXTERNAL_THUMBNAIL_CREATION_COMMAND)) {
            settings.setString(evt.getExternalThumbnailCreationCommand(), KEY_EXTERNAL_THUMBNAIL_CREATION_COMMAND);
        } else if (type.equals(UserSettingsChangeEvent.Type.FAST_SEARCH_COLUMNS)) {
            settings.setString(getColumnKeys(evt.getFastSearchColumns()), KEY_FAST_SEARCH_COLUMNS);
        } else if (type.equals(UserSettingsChangeEvent.Type.EDIT_COLUMNS)) {
            settings.setString(getColumnKeys(evt.getEditColumns()), KEY_EDIT_COLUMNS);
        } else if (type.equals(UserSettingsChangeEvent.Type.IPTC_CHARSET)) {
            settings.setString(evt.getIptcCharset(), KEY_IPTC_CHARSET);
        } else if (type.equals(UserSettingsChangeEvent.Type.IS_ACCEPT_HIDDEN_DIRECTORIES)) {
            settings.setBoolean(evt.isAcceptHiddenDirectories(), KEY_ACCEPT_HIDDEN_DIRECTORIES);
        } else if (type.equals(UserSettingsChangeEvent.Type.IS_AUTSCAN_INCLUDE_DIRECTORIES)) {
            settings.setBoolean(evt.isAutoscanIncludeSubdirectories(), KEY_AUTOSCAN_INCLUDE_SUBDIRECTORIES);
        } else if (type.equals(UserSettingsChangeEvent.Type.IS_CREATE_THUMBNAILS_WITH_EXTERNAL_APP)) {
            writeToPropertiesCreateThumbnailsWithExternalApp(evt.isCreateThumbnailsWithExternalApp());
        } else if (type.equals(UserSettingsChangeEvent.Type.IS_TASK_REMOVE_RECORDS_WITH_NOT_EXISTING_FILES)) {
            settings.setBoolean(evt.isTaskRemoveRecordsWithNotExistingFiles(), KEY_TASK_REMOVE_RECORDS_WITH_NOT_EXISTING_FILES);
        } else if (type.equals(UserSettingsChangeEvent.Type.IS_USE_AUTOCOMPLETE)) {
            settings.setBoolean(evt.isAutocomplete(), KEY_AUTOCOMPLETE);
        } else if (type.equals(UserSettingsChangeEvent.Type.IS_USE_EMBEDDED_THUMBNAILS)) {
            writeToPropertiesUseEmbeddedThumbnails(evt.isUseEmbeddedThumbnails());
        } else if (type.equals(UserSettingsChangeEvent.Type.LOGFILE_FORMATTER_CLASS)) {
            writeToPropertiesLogfileFormatterClass(evt.getLogfileFormatterClass());
        } else if (type.equals(UserSettingsChangeEvent.Type.LOG_LEVEL)) {
            settings.setString(evt.getLogLevel().toString(), KEY_LOG_LEVEL);
        } else if (type.equals(UserSettingsChangeEvent.Type.MAX_THUMBNAIL_WIDTH)) {
            settings.setString(evt.getMaxThumbnailWidth().toString(), KEY_MAX_THUMBNAIL_LENGTH);
        } else if (type.equals(UserSettingsChangeEvent.Type.MINUTES_TO_START_SCHEDULED_TASKS)) {
            settings.setString(evt.getMinutesToStartScheduledTasks().toString(), KEY_MINUTES_TO_START_SCHEDULED_TASKS);
        } else if (type.equals(UserSettingsChangeEvent.Type.NO_FAST_SEARCH_COLUMNS)) {
            properties.remove(KEY_FAST_SEARCH_COLUMNS);
        } else if (type.equals(UserSettingsChangeEvent.Type.THREAD_PRIORITY)) {
            settings.setInt(evt.getThreadPriority(), KEY_THREAD_PRIORITY);
        } else if (type.equals(UserSettingsChangeEvent.Type.AUTOCOPY_DIRECTORY)) {
            settings.setString(evt.getAutoCopyDirectory().getAbsolutePath(), KEY_AUTOCOPY_DIRECTORY);
        } else if (type.equals(UserSettingsChangeEvent.Type.WEB_BROWSER)) {
            settings.setString(evt.getWebBrowser(), KEY_WEB_BROWSER);
        } else if (type.equals(UserSettingsChangeEvent.Type.TREE_DIRECTORIES_SELECT_LAST_DIRECTORY)) {
            settings.setBoolean(evt.isTreeDirectoriesSelectLastDirectory(), KEY_TREE_DIRECTORIES_SELECT_LAST_DIRECTORY);
        } else if (type.equals(UserSettingsChangeEvent.Type.DATABASE_DIRECTORY)) {
            settings.setString(evt.getDatabaseDirectoryName(), KEY_DATABASE_DIRECTORY_NAME);
        } else if (type.equals(UserSettingsChangeEvent.Type.OPEN_SYSTEM_OUTPUT_WINDOW_AFTER_START)) {
            settings.setBoolean(evt.isOpenSystemOutputWindowAfterStart(), KEY_OPEN_SYSTEM_OUTPUT_WINDOW_AFTER_START);
        } else if (type.equals(UserSettingsChangeEvent.Type.MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS)) {
            settings.setInt(evt.getMaxSecondsToTerminateExternalPrograms(), KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS);
        }
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

    @Override
    public void finalize() {
        if (!isWrittenToFile) {
            writeToFile();
            AppLog.logWarning(UserSettings.class, Bundle.getString("UserSettings.ErrorMessage.NotWrittenPersistent"));
        }
    }
}
