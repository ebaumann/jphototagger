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

    private static final String delimiterColumns = "\t"; // NOI18N
    private static final int defaultMaxThumbnailLength = 150;
    private static final int defaultMinutesToStartScheduledTasks = 5;
    private static final String keyDefaultImageOpenApp = "UserSettings.DefaultImageOpenApp";
    private static final String keyExternalThumbnailCreationCommand = "UserSettings.ExternalThumbnailCreationCommand";
    private static final String keyFastSearchColumns = "UserSettings.FastSearchColumns";
    private static final String keyEditColumns = "UserSettings.EditColumns";
    private static final String keyIptcCharset = "UserSettings.IptcCharset";
    private static final String keyIsAcceptHiddenDirectories = "UserSettings.IsAcceptHiddenDirectories";
    private static final String keyIsAutoscanIncludeSubdirectories = "UserSettings.IsAutoscanIncludeSubdirectories";
    private static final String keyIsCreateThumbnailsWithExternalApp = "UserSettings.IsCreateThumbnailsWithExternalApp";
    private static final String keyIsTaskRemoveRecordsWithNotExistingFiles = "UserSettings.IsTaskRemoveRecordsWithNotExistingFiles";
    private static final String keyIsAutocomplete = "UserSettings.IsUseAutocomplete";
    private static final String keyIsUseEmbeddedThumbnails = "UserSettings.IsUseEmbeddedThumbnails";
    private static final String keyLogfileFormatterClass = "UserSettings.LogfileFormatterClass";
    private static final String keyLogLevel = "UserSettings.LogLevel";
    private static final String keyMaxThumbnailLength = "UserSettings.MaxThumbnailWidth";
    private static final String keyMinutesToStartScheduledTasks = "UserSettings.MinutesToStartScheduledTasks";
    private static final String keyThreadPriority = "UserSettings.ThreadPriority";
    private static final String keyAutocopyDirectory = "UserSettings.AutocopyDirectory";
    private static final String keyWebBrowser = "UserSettings.WebBrowser";
    private static final String keyTreeDirectoriesSelectLastDirectory = "UserSettings.TreeDirectoriesSelectLastDirectory";
    private final Properties properties = new Properties();
    private final Settings settings = new Settings(properties);
    private static final String domainName = "de.elmar_baumann"; // NOI18N NEVER CHANGE!
    private static final String propertiesFilename = "Settings.properties"; // NOI18N NEVER CHANGE!
    private final PropertiesFile propertiesToFile = new PropertiesFile(domainName, AppInfo.getProjectName(), propertiesFilename, properties);
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

    public String getThumbnailsDirectoryName() {
        return getSettingsDirectoryName() + File.separator + "thumbnails";
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
        return properties.containsKey(keyIsCreateThumbnailsWithExternalApp)
                ? settings.getBoolean(keyIsCreateThumbnailsWithExternalApp)
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
        return settings.getString(keyExternalThumbnailCreationCommand);
    }

    public String getWebBrowser() {
        return settings.getString(keyWebBrowser);
    }

    /**
     * Liefert den Loglevel.
     * 
     * @return Loglevel (Eine Ausgabe von getLocalizedName())
     * @see    java.util.logging.Level#getLocalizedName()
     */
    public Level getLogLevel() {
        String levelString = settings.getString(keyLogLevel);
        Level level = null;
        try {
            level = Level.parse(levelString);
        } catch (Exception ex) {
            AppLog.logWarning(UserSettings.class, ex);
            settings.setString(Level.WARNING.getLocalizedName(), keyLogLevel);
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
        if (!settings.getString(keyFastSearchColumns).isEmpty()) {
            List<String> columnKeys = ArrayUtil.stringTokenToList(
                    settings.getString(keyFastSearchColumns), delimiterColumns);
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
        if (!settings.getString(keyEditColumns).isEmpty()) {
            List<String> columnKeys = ArrayUtil.stringTokenToList(
                    settings.getString(keyEditColumns), delimiterColumns);
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
        return settings.getString(keyDefaultImageOpenApp);
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
        int priority = settings.getInt(keyThreadPriority);
        return priority >= 0 && priority <= 10 ? priority : 5;
    }

    /**
     * Liefert die maximale Seitenlänge (längere Seite) von Thumbnails,
     * die skaliert werden.
     * 
     * @return Seitenlänge in Pixel
     */
    public int getMaxThumbnailLength() {
        int width = settings.getInt(keyMaxThumbnailLength);
        return width != Integer.MIN_VALUE ? width : defaultMaxThumbnailLength;
    }

    /**
     * Liefert, ob eingebettete Thumbnails benutzt werden sollen.
     * 
     * @return true, wenn eingebettete Thumbnails benutzt werden sollen
     */
    public boolean isUseEmbeddedThumbnails() {
        return properties.containsKey(keyIsUseEmbeddedThumbnails)
                ? settings.getBoolean(keyIsUseEmbeddedThumbnails)
                : false;
    }

    /**
     * Liefert den Zeichensatz, mit dem IPTC-Daten dekodiert werden sollen.
     * 
     * @return Zeichensatz
     */
    public String getIptcCharset() {
        String charset = settings.getString(keyIptcCharset);
        return charset.isEmpty() ? "ISO-8859-1" : charset;
    }

    /**
     * Liefert, ob beim automatischen Scan von Verzeichnissen auch die
     * Unterverzeichnisse einbezogen werden sollen.
     * 
     * @return true, falls die Unterverzeichnisse einbezogen werden sollen
     */
    public boolean isAutoscanIncludeSubdirectories() {
        return properties.containsKey(keyIsAutoscanIncludeSubdirectories)
                ? settings.getBoolean(keyIsAutoscanIncludeSubdirectories)
                : true;
    }

    public boolean isTreeDirectoriesSelectLastDirectory() {
        return properties.containsKey(keyTreeDirectoriesSelectLastDirectory)
                ? settings.getBoolean(keyTreeDirectoriesSelectLastDirectory)
                : true;
    }

    /**
     * Liefert die Klasse des Logdateiformatierers.
     * 
     * @return Logdateiformatierer
     */
    public Class getLogfileFormatterClass() {
        String className = settings.getString(keyLogfileFormatterClass);
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            AppLog.logWarning(UserSettings.class, ex);
            settings.setString(XMLFormatter.class.getName(), keyLogfileFormatterClass);
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
        return properties.containsKey(keyIsTaskRemoveRecordsWithNotExistingFiles)
                ? settings.getBoolean(keyIsTaskRemoveRecordsWithNotExistingFiles)
                : false;
    }

    /**
     * Liefert die Minuten, bevor geplante Tasks starten.
     * 
     * @return Minuten
     */
    public int getMinutesToStartScheduledTasks() {
        int minutes = settings.getInt(keyMinutesToStartScheduledTasks);
        return minutes > 0 ? minutes : defaultMinutesToStartScheduledTasks;
    }

    /**
     * Liefert, ob Autocomplete eingeschaltet werden soll.
     * 
     * @return true, wenn Autocomplete eingeschaltet werden soll
     */
    public boolean isUseAutocomplete() {
        return properties.containsKey(keyIsAutocomplete)
                ? settings.getBoolean(keyIsAutocomplete)
                : true;
    }

    /**
     * Returns whether directory choosers and -trees shall show hidden
     * directories and if directory scans shall include them.
     * 
     * @return true, if accepted
     */
    public boolean isAcceptHiddenDirectories() {
        return properties.containsKey(keyIsAcceptHiddenDirectories)
                ? settings.getBoolean(keyIsAcceptHiddenDirectories)
                : false;
    }

    /**
     * Returns the autocopy directory, a source directory from which all
     * image files should be copied to another directory automatically.
     * 
     * @return Existing directory or null if not defined or not existing
     */
    public File getAutocopyDirectory() {
        File dir = new File(settings.getString(keyAutocopyDirectory));
        return dir.exists() && dir.isDirectory() ? dir : null;
    }

    @Override
    public void applySettings(UserSettingsChangeEvent evt) {
        writeProperties(evt);
    }

    private void writeProperties(UserSettingsChangeEvent evt) {
        UserSettingsChangeEvent.Type type = evt.getType();
        if (type.equals(UserSettingsChangeEvent.Type.DEFAULT_IMAGE_OPEN_APP)) {
            settings.setString(evt.getDefaultImageOpenApp().getAbsolutePath(), keyDefaultImageOpenApp);
        } else if (type.equals(UserSettingsChangeEvent.Type.EXTERNAL_THUMBNAIL_CREATION_COMMAND)) {
            settings.setString(evt.getExternalThumbnailCreationCommand(), keyExternalThumbnailCreationCommand);
        } else if (type.equals(UserSettingsChangeEvent.Type.FAST_SEARCH_COLUMNS)) {
            settings.setString(getColumnKeys(evt.getFastSearchColumns()), keyFastSearchColumns);
        } else if (type.equals(UserSettingsChangeEvent.Type.EDIT_COLUMNS)) {
            settings.setString(getColumnKeys(evt.getEditColumns()), keyEditColumns);
        } else if (type.equals(UserSettingsChangeEvent.Type.IPTC_CHARSET)) {
            settings.setString(evt.getIptcCharset(), keyIptcCharset);
        } else if (type.equals(UserSettingsChangeEvent.Type.IS_ACCEPT_HIDDEN_DIRECTORIES)) {
            settings.setBoolean(evt.isAcceptHiddenDirectories(), keyIsAcceptHiddenDirectories);
        } else if (type.equals(UserSettingsChangeEvent.Type.IS_AUTSCAN_INCLUDE_DIRECTORIES)) {
            settings.setBoolean(evt.isAutoscanIncludeSubdirectories(), keyIsAutoscanIncludeSubdirectories);
        } else if (type.equals(UserSettingsChangeEvent.Type.IS_CREATE_THUMBNAILS_WITH_EXTERNAL_APP)) {
            writeToPropertiesCreateThumbnailsWithExternalApp(evt.isCreateThumbnailsWithExternalApp());
        } else if (type.equals(UserSettingsChangeEvent.Type.IS_TASK_REMOVE_RECORDS_WITH_NOT_EXISTING_FILES)) {
            settings.setBoolean(evt.isTaskRemoveRecordsWithNotExistingFiles(), keyIsTaskRemoveRecordsWithNotExistingFiles);
        } else if (type.equals(UserSettingsChangeEvent.Type.IS_USE_AUTOCOMPLETE)) {
            settings.setBoolean(evt.isAutocomplete(), keyIsAutocomplete);
        } else if (type.equals(UserSettingsChangeEvent.Type.IS_USE_EMBEDDED_THUMBNAILS)) {
            writeToPropertiesUseEmbeddedThumbnails(evt.isUseEmbeddedThumbnails());
        } else if (type.equals(UserSettingsChangeEvent.Type.LOGFILE_FORMATTER_CLASS)) {
            writeToPropertiesLogfileFormatterClass(evt.getLogfileFormatterClass());
        } else if (type.equals(UserSettingsChangeEvent.Type.LOG_LEVEL)) {
            settings.setString(evt.getLogLevel().toString(), keyLogLevel);
        } else if (type.equals(UserSettingsChangeEvent.Type.MAX_THUMBNAIL_WIDTH)) {
            settings.setString(evt.getMaxThumbnailWidth().toString(), keyMaxThumbnailLength);
        } else if (type.equals(UserSettingsChangeEvent.Type.MINUTES_TO_START_SCHEDULED_TASKS)) {
            settings.setString(evt.getMinutesToStartScheduledTasks().toString(), keyMinutesToStartScheduledTasks);
        } else if (type.equals(UserSettingsChangeEvent.Type.NO_FAST_SEARCH_COLUMNS)) {
            properties.remove(keyFastSearchColumns);
        } else if (type.equals(UserSettingsChangeEvent.Type.THREAD_PRIORITY)) {
            settings.setInt(evt.getThreadPriority(), keyThreadPriority);
        } else if (type.equals(UserSettingsChangeEvent.Type.AUTOCOPY_DIRECTORY)) {
            settings.setString(evt.getAutoCopyDirectory().getAbsolutePath(), keyAutocopyDirectory);
        } else if (type.equals(UserSettingsChangeEvent.Type.WEB_BROWSER)) {
            settings.setString(evt.getWebBrowser(), keyWebBrowser);
        } else if (type.equals(UserSettingsChangeEvent.Type.TREE_DIRECTORIES_SELECT_LAST_DIRECTORY)) {
            settings.setBoolean(evt.isTreeDirectoriesSelectLastDirectory(), keyTreeDirectoriesSelectLastDirectory);
        }
    }

    private void writeToPropertiesCreateThumbnailsWithExternalApp(boolean create) {
        settings.setBoolean(create, keyIsCreateThumbnailsWithExternalApp);
        if (create) {
            settings.setBoolean(false, keyIsUseEmbeddedThumbnails);
        }
    }

    private void writeToPropertiesLogfileFormatterClass(Class formatterClass) {
        String classString = formatterClass.toString();
        int index = classString.lastIndexOf(" ");
        settings.setString(index >= 0 && index + 1 < classString.length()
                ? classString.substring(index + 1)
                : XMLFormatter.class.getName(),
                keyLogfileFormatterClass);
    }

    private void writeToPropertiesUseEmbeddedThumbnails(boolean use) {
        settings.setBoolean(use, keyIsUseEmbeddedThumbnails);
        if (use) {
            settings.setBoolean(false, keyIsCreateThumbnailsWithExternalApp);
        }
    }

    private String getColumnKeys(List<Column> columns) {
        StringBuffer buffer = new StringBuffer();
        for (Column column : columns) {
            buffer.append(column.getKey() + delimiterColumns);
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
