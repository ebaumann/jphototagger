package de.elmar_baumann.imv;

import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.UserSettingsChangeListener;
import de.elmar_baumann.imv.view.dialogs.UserSettingsDialog;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import de.elmar_baumann.lib.util.ArrayUtil;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.XMLFormatter;

/**
 * Benutzereinstellungen. Benutzt den Dialog
 * {@link de.elmar_baumann.imv.view.dialogs.UserSettingsDialog}.
 * Liest dessen Einstellungen. Motivation: Die Eingaben in etliche Controls des
 * Dialogs werden automatisch persistent gespeichert.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class UserSettings implements UserSettingsChangeListener {

    private UserSettingsDialog settingsDialog = UserSettingsDialog.getInstance();
    private Database db = Database.getInstance();
    private static UserSettings instance = new UserSettings();
    private static final String delimiterSearchColumns = "\t"; // NOI18N
    private static final int defaultMaxThumbnailWidth = 150;
    public static final String keyIsCreateThumbnailsWithExternalApp = "UserSettings.IsCreateThumbnailsWithExternalApp";
    public static final String keyExternalThumbnailCreationCommand = "UserSettings.ExternalThumbnailCreationCommand";
    public static final String keyLogLevel = "UserSettings.LogLevel";
    public static final String keyFastSearchColumns = "UserSettings.FastSearchColumns";
    public static final String keyDefaultImageOpenApp = "UserSettings.DefaultImageOpenApp";
    public static final String keyThreadPriority = "UserSettings.ThreadPriority";
    public static final String keyMaxThumbnailWidth = "UserSettings.MaxThumbnailWidth";
    public static final String keyIsUseEmbeddedThumbnails = "UserSettings.IsUseEmbeddedThumbnails";
    public static final String keyIptcCharset = "UserSettings.IptcCharset";
    public static final String keyOtherImageOpenApps = "UserSettings.OtherImageOpenApps";
    public static final String keyAutoscanDirectories = "UserSettings.AutoscanDirectories";
    public static final String keyIsAutoscanIncludeSubdirectories = "UserSettings.IsAutoscanIncludeSubdirectories";
    public static final String keyLogfileFormatterClass = "UserSettings.LogfileFormatterClass";
    public static final String keyIsTaskRemoveRecordsWithNotExistingFiles = "UserSettings.IsTaskRemoveRecordsWithNotExistingFiles";
    public static final String keyMinutesToStartScheduledTasks = "UserSettings.MinutesToStartScheduledTasks";
    public static final String keyIsUseAutocomplete = "UserSettings.IsUseAutocomplete";
    public static final String keyIsAcceptHiddenDirectories = "UserSettings.IsAcceptHiddenDirectories";
    private PersistentSettings settings = PersistentSettings.getInstance();

    /**
     * Liefert die einzige Klasseninstanz.
     * 
     * @return Instanz
     */
    public static UserSettings getInstance() {
        return instance;
    }

    /**
     * Liefert, ob die Thumbnails von einer externen Anwendung erzeugt werden.
     * 
     * @return true, wenn die Thumbnails von einer externen Anwendung erzeugt
     *         werden sollen
     * @see    #getExternalThumbnailCreationCommand() 
     */
    public boolean isCreateThumbnailsWithExternalApp() {
        //return settingsDialog.checkBoxExternalThumbnailApp.isSelected();
        return settings.getBoolean(keyIsCreateThumbnailsWithExternalApp);
    }

    /**
     * Liefert die Befehlszeile des externen Programms, das die Thumbnails
     * erzeugt.
     * 
     * @return Befehlszeile
     * @see    #isCreateThumbnailsWithExternalApp()
     */
    public String getExternalThumbnailCreationCommand() {
        //return settingsDialog.textFieldExternalThumbnailApp.getText();
        return settings.getString(keyExternalThumbnailCreationCommand);
    }

    /**
     * Liefert den Loglevel.
     * 
     * @return Loglevel (Eine Ausgabe von getLocalizedName())
     * @see    java.util.logging.Level#getLocalizedName()
     */
    public Level getLogLevel() {
//        Object item = settingsDialog.comboBoxLogLevel.getSelectedItem();
//        if (item == null) {
//            return Level.WARNING.getLocalizedName();
//        } else {
//            return item.toString();
//        }
        String levelString = settings.getString(keyLogLevel);
        Level level = null;
        try {
            level = Level.parse(levelString);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(UserSettings.class.getName()).log(Level.WARNING, null, ex);
        }
        return level == null ? Level.WARNING : level;
    }

    /**
     * Liefert alle Spalten für die Schnellsuche.
     * 
     * @return Suchspalten
     */
    public List<Column> getFastSearchColumns() {
//        return settingsDialog.searchColumnsListModel.getTableColumns(
//            settingsDialog.checkListSearchColumns.getSelectedItemIndices());
        List<Column> columns = new ArrayList<Column>();
        List<String> columnKeys = ArrayUtil.stringTokenToList(
            settings.getString(keyFastSearchColumns), delimiterSearchColumns);
        for (String key : columnKeys) {
            try {
                Class cl = Class.forName(key);
                @SuppressWarnings("unchecked")
                Method method = cl.getMethod("getInstance", new Class[0]); // NOI18N
                Object o = method.invoke(null, new Object[0]);
                if (o instanceof Column) {
                    columns.add((Column) o);
                }
            } catch (Exception ex) {
                Logger.getLogger(UserSettingsDialog.class.getName()).log(Level.WARNING, ex.getMessage());
            }
        }
        return columns;
    }

    /**
     * Liefert die Anwendung, die ein Bild bei Doppelklick öffnen soll.
     * 
     * @return Anwendung oder Leerstring, wenn nicht definiert
     */
    public String getDefaultImageOpenApp() {
        //return settingsDialog.labelImageOpenApp.getText();
        return settings.getString(keyDefaultImageOpenApp);
    }

    /**
     * Liefert die Priorität für Threads zum Erzeugen von Thumbnails und Metadaten.
     * 
     * @return Threadpriorität
     */
    public int getThreadPriority() {
//        ComboBoxModelThreadPriority model =
//            (ComboBoxModelThreadPriority) settingsDialog.comboBoxThreadPriority.getModel();
//        Object item = model.getSelectedItem();
//        if (item == null) {
//            return Thread.NORM_PRIORITY;
//        } else {
//            return model.getPriorityOf(item.toString());
//        }
        int priority = settings.getInt(keyThreadPriority);
        return priority >= 0 && priority <= 10 ? priority : 5;
    }

    /**
     * Liefert die maximale Seitenlänge (längere Seite) von Thumbnails,
     * die skaliert werden.
     * 
     * @return Seitenlänge in Pixel
     */
    public int getMaxThumbnailWidth() {
//        return new Integer(settingsDialog.spinnerMaxThumbnailWidth.getValue().
//            toString()).intValue();
        int width = settings.getInt(keyMaxThumbnailWidth);
        return width != Integer.MIN_VALUE ? width : defaultMaxThumbnailWidth;
    }

    /**
     * Liefert, ob eingebettete Thumbnails benutzt werden sollen.
     * 
     * @return true, wenn eingebettete Thumbnails benutzt werden sollen
     */
    public boolean isUseEmbeddedThumbnails() {
        //return settingsDialog.checkBoxUseEmbeddedThumbnails.isSelected();
        return settings.getBoolean(keyIsUseEmbeddedThumbnails);
    }

    /**
     * Liefert den Zeichensatz, mit dem IPTC-Daten dekodiert werden sollen.
     * 
     * @return Zeichensatz
     */
    public String getIptcCharset() {
//        Object item = settingsDialog.comboBoxIptcCharset.getItemAt(0);
//        if (item == null) {
//            return "ISO-8859-1"; // NOI18N
//        } else {
//            return item.toString();
//        }
        String charset = settings.getString(keyIptcCharset);
        return charset.isEmpty() ? "ISO-8859-1" : charset;
    }

    /**
     * Liefert alle Anwendungen, die ein Bild öffnen können.
     * 
     * @return Anwendungsdateien, falls existent
     */
    public List<File> getOtherImageOpenApps() {
//        List<File> apps = new ArrayList<File>();
//        ListModel model = settingsDialog.listOpenImageApps.getModel();
//        int count = model.getSize();
//        for (int i = 0; i < count; i++) {
//            File file = new File(model.getElementAt(i).toString());
//            if (file.exists()) {
//                apps.add(file);
//            }
//        }
//        return apps;
        return FileUtil.getAsFiles(settings.getStringArray(keyOtherImageOpenApps));
    }

    /**
     * Liefert regelmäßig zu scannende Verzeichnisse.
     * 
     * @return Verzeichnisnamen
     */
    public List<String> getAutoscanDirectories() {
        return db.getAutoscanDirectories();
    }

    /**
     * Liefert, ob beim automatischen Scan von Verzeichnissen auch die
     * Unterverzeichnisse einbezogen werden sollen.
     * 
     * @return true, falls die Unterverzeichnisse einbezogen werden sollen
     */
    public boolean isAutoscanIncludeSubdirectories() {
        //return settingsDialog.checkBoxTasksAutoscanIncludeSubdirectories.isSelected();
        return settings.getBoolean(keyIsAutoscanIncludeSubdirectories);
    }

    /**
     * Liefert die Klasse des Logdateiformatierers.
     * 
     * @return Logdateiformatierer
     */
    public Class getLogfileFormatterClass() {
        Object o = settingsDialog.comboBoxLogfileFormatter.getModel().getSelectedItem();
        if (o != null && o instanceof Class) {
            return (Class) o;
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
        return settingsDialog.checkBoxTasksRemoveRecordsWithNotExistingFiles.isSelected();
    }

    /**
     * Liefert die Minuten, bevor geplante Tasks starten.
     * 
     * @return Minuten
     */
    public int getMinutesToStartScheduledTasks() {
        return new Integer(settingsDialog.spinnerTasksMinutesToStartScheduledTasks.getValue().toString()).intValue();
    }

    /**
     * Liefert, ob Autocomplete eingeschaltet werden soll.
     * 
     * @return true, wenn Autocomplete eingeschaltet werden soll
     */
    public boolean isUseAutocomplete() {
        return !settingsDialog.checkBoxDisableAutocomplete.isSelected();
    }

    /**
     * Returns whether directory choosers and -trees shall show hidden
     * directories and if directory scans shall include them.
     * 
     * @return true, if accepted
     */
    public boolean isAcceptHiddenDirectories() {
        return settingsDialog.checkBoxAcceptHiddenDirectories.isSelected();
    }

    public List<String> getFileExcludePatterns() {
        return settingsDialog.getFileExcludePatterns();
    }

    private UserSettings() {
    }

    @Override
    public void applySettings(UserSettingsChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
