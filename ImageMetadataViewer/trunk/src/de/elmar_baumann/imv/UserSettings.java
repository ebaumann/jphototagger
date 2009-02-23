package de.elmar_baumann.imv;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.ColumnUtil;
import de.elmar_baumann.imv.database.metadata.selections.EditColumns;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.UserSettingsChangeListener;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import de.elmar_baumann.lib.util.ArrayUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.XMLFormatter;

/**
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
    private static final PersistentSettings persistentSettings = PersistentSettings.INSTANCE;
    public static final UserSettings INSTANCE = new UserSettings();

    private UserSettings() {
    }

    /**
     * Liefert, ob die Thumbnails von einer externen Anwendung erzeugt werden.
     * 
     * @return true, wenn die Thumbnails von einer externen Anwendung erzeugt
     *         werden sollen
     * @see    #getExternalThumbnailCreationCommand() 
     */
    public boolean isCreateThumbnailsWithExternalApp() {
        return persistentSettings.getProperties().containsKey(keyIsCreateThumbnailsWithExternalApp)
            ? persistentSettings.getBoolean(keyIsCreateThumbnailsWithExternalApp)
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
        return persistentSettings.getString(keyExternalThumbnailCreationCommand);
    }

    /**
     * Liefert den Loglevel.
     * 
     * @return Loglevel (Eine Ausgabe von getLocalizedName())
     * @see    java.util.logging.Level#getLocalizedName()
     */
    public Level getLogLevel() {
        String levelString = persistentSettings.getString(keyLogLevel);
        Level level = null;
        try {
            level = Level.parse(levelString);
        } catch (Exception ex) {
            de.elmar_baumann.imv.app.AppLog.logWarning(getClass(), ex);
            persistentSettings.setString(Level.WARNING.getLocalizedName(), keyLogLevel);
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
        if (!persistentSettings.getString(keyFastSearchColumns).isEmpty()) {
            List<String> columnKeys = ArrayUtil.stringTokenToList(
                persistentSettings.getString(keyFastSearchColumns), delimiterColumns);
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
        if (!persistentSettings.getString(keyEditColumns).isEmpty()) {
            List<String> columnKeys = ArrayUtil.stringTokenToList(
                persistentSettings.getString(keyEditColumns), delimiterColumns);
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
        return persistentSettings.getString(keyDefaultImageOpenApp);
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
        int priority = persistentSettings.getInt(keyThreadPriority);
        return priority >= 0 && priority <= 10 ? priority : 5;
    }

    /**
     * Liefert die maximale Seitenlänge (längere Seite) von Thumbnails,
     * die skaliert werden.
     * 
     * @return Seitenlänge in Pixel
     */
    public int getMaxThumbnailLength() {
        int width = persistentSettings.getInt(keyMaxThumbnailLength);
        return width != Integer.MIN_VALUE ? width : defaultMaxThumbnailLength;
    }

    /**
     * Liefert, ob eingebettete Thumbnails benutzt werden sollen.
     * 
     * @return true, wenn eingebettete Thumbnails benutzt werden sollen
     */
    public boolean isUseEmbeddedThumbnails() {
        return persistentSettings.getProperties().containsKey(keyIsUseEmbeddedThumbnails)
            ? persistentSettings.getBoolean(keyIsUseEmbeddedThumbnails)
            : false;
    }

    /**
     * Liefert den Zeichensatz, mit dem IPTC-Daten dekodiert werden sollen.
     * 
     * @return Zeichensatz
     */
    public String getIptcCharset() {
        String charset = persistentSettings.getString(keyIptcCharset);
        return charset.isEmpty() ? "ISO-8859-1" : charset;
    }

    /**
     * Liefert, ob beim automatischen Scan von Verzeichnissen auch die
     * Unterverzeichnisse einbezogen werden sollen.
     * 
     * @return true, falls die Unterverzeichnisse einbezogen werden sollen
     */
    public boolean isAutoscanIncludeSubdirectories() {
        return persistentSettings.getProperties().containsKey(keyIsAutoscanIncludeSubdirectories)
            ? persistentSettings.getBoolean(keyIsAutoscanIncludeSubdirectories)
            : true;
    }

    /**
     * Liefert die Klasse des Logdateiformatierers.
     * 
     * @return Logdateiformatierer
     */
    public Class getLogfileFormatterClass() {
        String className = persistentSettings.getString(keyLogfileFormatterClass);
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            de.elmar_baumann.imv.app.AppLog.logWarning(getClass(), ex);
            PersistentSettings.INSTANCE.setString(XMLFormatter.class.getName(), keyLogfileFormatterClass);
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
        return persistentSettings.getProperties().containsKey(keyIsTaskRemoveRecordsWithNotExistingFiles)
            ? persistentSettings.getBoolean(keyIsTaskRemoveRecordsWithNotExistingFiles)
            : false;
    }

    /**
     * Liefert die Minuten, bevor geplante Tasks starten.
     * 
     * @return Minuten
     */
    public int getMinutesToStartScheduledTasks() {
        int minutes = persistentSettings.getInt(keyMinutesToStartScheduledTasks);
        return minutes > 0 ? minutes : defaultMinutesToStartScheduledTasks;
    }

    /**
     * Liefert, ob Autocomplete eingeschaltet werden soll.
     * 
     * @return true, wenn Autocomplete eingeschaltet werden soll
     */
    public boolean isUseAutocomplete() {
        return persistentSettings.getProperties().containsKey(keyIsAutocomplete)
            ? persistentSettings.getBoolean(keyIsAutocomplete)
            : true;
    }

    /**
     * Returns whether directory choosers and -trees shall show hidden
     * directories and if directory scans shall include them.
     * 
     * @return true, if accepted
     */
    public boolean isAcceptHiddenDirectories() {
        return persistentSettings.getProperties().containsKey(keyIsAcceptHiddenDirectories)
            ? persistentSettings.getBoolean(keyIsAcceptHiddenDirectories)
            : false;
    }

    /**
     * Returns the autocopy directory, a source directory from which all
     * image files should be copied to another directory automatically.
     * 
     * @return Existing directory or null if not defined or not existing
     */
    public File getAutocopyDirectory() {
        File dir = new File(persistentSettings.getString(keyAutocopyDirectory));
        return dir.exists() && dir.isDirectory() ? dir : null;
    }

    @Override
    public void applySettings(UserSettingsChangeEvent evt) {
        writePersistent(evt);
    }

    private void writePersistent(UserSettingsChangeEvent evt) {
        UserSettingsChangeEvent.Type type = evt.getType();
        if (type.equals(UserSettingsChangeEvent.Type.DEFAULT_IMAGE_OPEN_APP)) {
            persistentSettings.setString(evt.getDefaultImageOpenApp().getAbsolutePath(), keyDefaultImageOpenApp);
        } else if (type.equals(UserSettingsChangeEvent.Type.EXTERNAL_THUMBNAIL_CREATION_COMMAND)) {
            persistentSettings.setString(evt.getExternalThumbnailCreationCommand(), keyExternalThumbnailCreationCommand);
        } else if (type.equals(UserSettingsChangeEvent.Type.FAST_SEARCH_COLUMNS)) {
            persistentSettings.setString(getColumnKeys(evt.getFastSearchColumns()), keyFastSearchColumns);
        } else if (type.equals(UserSettingsChangeEvent.Type.EDIT_COLUMNS)) {
            persistentSettings.setString(getColumnKeys(evt.getEditColumns()), keyEditColumns);
        } else if (type.equals(UserSettingsChangeEvent.Type.IPTC_CHARSET)) {
            persistentSettings.setString(evt.getIptcCharset(), keyIptcCharset);
        } else if (type.equals(UserSettingsChangeEvent.Type.IS_ACCEPT_HIDDEN_DIRECTORIES)) {
            persistentSettings.setBoolean(evt.isAcceptHiddenDirectories(), keyIsAcceptHiddenDirectories);
        } else if (type.equals(UserSettingsChangeEvent.Type.IS_AUTSCAN_INCLUDE_DIRECTORIES)) {
            persistentSettings.setBoolean(evt.isAutoscanIncludeSubdirectories(), keyIsAutoscanIncludeSubdirectories);
        } else if (type.equals(UserSettingsChangeEvent.Type.IS_CREATE_THUMBNAILS_WITH_EXTERNAL_APP)) {
            writePersistentCreateThumbnailsWithExternalApp(evt.isCreateThumbnailsWithExternalApp());
        } else if (type.equals(UserSettingsChangeEvent.Type.IS_TASK_REMOVE_RECORDS_WITH_NOT_EXISTING_FILES)) {
            persistentSettings.setBoolean(evt.isTaskRemoveRecordsWithNotExistingFiles(), keyIsTaskRemoveRecordsWithNotExistingFiles);
        } else if (type.equals(UserSettingsChangeEvent.Type.IS_USE_AUTOCOMPLETE)) {
            persistentSettings.setBoolean(evt.isAutocomplete(), keyIsAutocomplete);
        } else if (type.equals(UserSettingsChangeEvent.Type.IS_USE_EMBEDDED_THUMBNAILS)) {
            writePersistentUseEmbeddedThumbnails(evt.isUseEmbeddedThumbnails());
        } else if (type.equals(UserSettingsChangeEvent.Type.LOGFILE_FORMATTER_CLASS)) {
            writePersistentLogfileFormatterClass(evt.getLogfileFormatterClass());
        } else if (type.equals(UserSettingsChangeEvent.Type.LOG_LEVEL)) {
            persistentSettings.setString(evt.getLogLevel().toString(), keyLogLevel);
        } else if (type.equals(UserSettingsChangeEvent.Type.MAX_THUMBNAIL_WIDTH)) {
            persistentSettings.setString(evt.getMaxThumbnailWidth().toString(), keyMaxThumbnailLength);
        } else if (type.equals(UserSettingsChangeEvent.Type.MINUTES_TO_START_SCHEDULED_TASKS)) {
            persistentSettings.setString(evt.getMinutesToStartScheduledTasks().toString(), keyMinutesToStartScheduledTasks);
        } else if (type.equals(UserSettingsChangeEvent.Type.NO_FAST_SEARCH_COLUMNS)) {
            persistentSettings.getProperties().remove(keyFastSearchColumns);
        } else if (type.equals(UserSettingsChangeEvent.Type.THREAD_PRIORITY)) {
            persistentSettings.setInt(evt.getThreadPriority(), keyThreadPriority);
        } else if (type.equals(UserSettingsChangeEvent.Type.AUTOCOPY_DIRECTORY)) {
            persistentSettings.setString(evt.getAutoCopyDirectory().getAbsolutePath(), keyAutocopyDirectory);
        }
    }

    private void writePersistentCreateThumbnailsWithExternalApp(boolean create) {
        persistentSettings.setBoolean(create, keyIsCreateThumbnailsWithExternalApp);
        if (create) {
            persistentSettings.setBoolean(false, keyIsUseEmbeddedThumbnails);
        }
    }

    private void writePersistentLogfileFormatterClass(Class formatterClass) {
        String classString = formatterClass.toString();
        int index = classString.lastIndexOf(" ");
        persistentSettings.setString(index >= 0 && index + 1 < classString.length()
            ? classString.substring(index + 1)
            : XMLFormatter.class.getName(),
            keyLogfileFormatterClass);
    }

    private void writePersistentUseEmbeddedThumbnails(boolean use) {
        persistentSettings.setBoolean(use, keyIsUseEmbeddedThumbnails);
        if (use) {
            persistentSettings.setBoolean(false, keyIsCreateThumbnailsWithExternalApp);
        }
    }

    private String getColumnKeys(List<Column> columns) {
        StringBuffer buffer = new StringBuffer();
        for (Column column : columns) {
            buffer.append(column.getKey() + delimiterColumns);
        }
        return buffer.toString();
    }
}
