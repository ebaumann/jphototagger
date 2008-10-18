package de.elmar_baumann.imv;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.UserSettingsChangeListener;
import de.elmar_baumann.imv.model.ComboBoxModelThreadPriority;
import de.elmar_baumann.imv.model.ListModelFastSearchColumns;
import de.elmar_baumann.imv.view.dialogs.UserSettingsDialog;
import de.elmar_baumann.lib.component.CheckList;
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
import javax.swing.JCheckBox;
import javax.swing.ListModel;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class UserSettings implements UserSettingsChangeListener {

    private static final String delimiterSearchColumns = "\t"; // NOI18N
    private static final int defaultMaxThumbnailWidth = 150;
    private static final int defaultMinutesToStartScheduledTasks = 5;
    private static final String keyDefaultImageOpenApp = "UserSettings.DefaultImageOpenApp";
    private static final String keyExternalThumbnailCreationCommand = "UserSettings.ExternalThumbnailCreationCommand";
    private static final String keyFastSearchColumns = "UserSettings.FastSearchColumns";
    private static final String keyIptcCharset = "UserSettings.IptcCharset";
    private static final String keyIsAcceptHiddenDirectories = "UserSettings.IsAcceptHiddenDirectories";
    private static final String keyIsAutoscanIncludeSubdirectories = "UserSettings.IsAutoscanIncludeSubdirectories";
    private static final String keyIsCreateThumbnailsWithExternalApp = "UserSettings.IsCreateThumbnailsWithExternalApp";
    private static final String keyIsTaskRemoveRecordsWithNotExistingFiles = "UserSettings.IsTaskRemoveRecordsWithNotExistingFiles";
    private static final String keyIsUseAutocomplete = "UserSettings.IsUseAutocomplete";
    private static final String keyIsUseEmbeddedThumbnails = "UserSettings.IsUseEmbeddedThumbnails";
    private static final String keyLogfileFormatterClass = "UserSettings.LogfileFormatterClass";
    private static final String keyLogLevel = "UserSettings.LogLevel";
    private static final String keyMaxThumbnailWidth = "UserSettings.MaxThumbnailWidth";
    private static final String keyMinutesToStartScheduledTasks = "UserSettings.MinutesToStartScheduledTasks";
    private static final String keyOtherImageOpenApps = "UserSettings.OtherImageOpenApps";
    private static final String keyThreadPriority = "UserSettings.ThreadPriority";
    private PersistentSettings settings = PersistentSettings.getInstance();
    private static UserSettings instance = new UserSettings();

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
        return settings.getProperties().containsKey(keyIsCreateThumbnailsWithExternalApp)
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
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(UserSettings.class.getName()).log(Level.WARNING, null, ex);
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
        }
        return columns;
    }

    private String getSearchColumnKeys(CheckList checkListSearchColumns,
        ListModelFastSearchColumns searchColumnsListModel) {
        StringBuffer tableColumns = new StringBuffer();
        List<Integer> indices =
            checkListSearchColumns.getSelectedItemIndices();
        for (Integer index : indices) {
            tableColumns.append(searchColumnsListModel.getTableColumnAtIndex(
                index).getKey() + delimiterSearchColumns);
        }
        return tableColumns.toString();
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
     * Liefert die Priorität für Threads zum Erzeugen von Thumbnails und Metadaten.
     * 
     * @return Threadpriorität
     */
    public int getThreadPriority() {
        int priority = settings.getInt(keyThreadPriority);
        return priority >= 0 && priority <= 10 ? priority : 5;
    }

    private void setCreateThumbnailsWithExternalApp(JCheckBox checkBoxIsCreateThumbnailsWithExternalApp) {
        boolean selected = checkBoxIsCreateThumbnailsWithExternalApp.isSelected();
        settings.setBoolean(selected, keyIsCreateThumbnailsWithExternalApp);
        if (selected) {
            settings.setBoolean(false, keyIsUseEmbeddedThumbnails);
        }
    }

    private void setLogfileFormatterClass(Object selectedItem) {
        String classString = selectedItem.toString();
        int index = classString.lastIndexOf(" ");
        settings.setString(index >= 0 && index + 1 < classString.length() 
            ? classString.substring(index + 1)
            : XMLFormatter.class.getName(),
            keyLogfileFormatterClass);
    }

    private void setThreadPriority(ComboBoxModelThreadPriority model) {
        Object item = model.getSelectedItem();
        settings.setInt(model.getPriorityOf(item.toString()), keyThreadPriority);
    }

    /**
     * Liefert die maximale Seitenlänge (längere Seite) von Thumbnails,
     * die skaliert werden.
     * 
     * @return Seitenlänge in Pixel
     */
    public int getMaxThumbnailWidth() {
        int width = settings.getInt(keyMaxThumbnailWidth);
        return width != Integer.MIN_VALUE ? width : defaultMaxThumbnailWidth;
    }

    /**
     * Liefert, ob eingebettete Thumbnails benutzt werden sollen.
     * 
     * @return true, wenn eingebettete Thumbnails benutzt werden sollen
     */
    public boolean isUseEmbeddedThumbnails() {
        return settings.getProperties().containsKey(keyIsUseEmbeddedThumbnails)
            ? settings.getBoolean(keyIsUseEmbeddedThumbnails)
            : false;
    }

    private void setUseEmbeddedThumbnails(JCheckBox checkBoxIsUseEmbeddedThumbnails) {
        boolean selected = checkBoxIsUseEmbeddedThumbnails.isSelected();
        settings.setBoolean(selected, keyIsUseEmbeddedThumbnails);
        if (selected) {
            settings.setBoolean(false, keyIsCreateThumbnailsWithExternalApp);
        }
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
     * Liefert alle Anwendungen, die ein Bild öffnen können.
     * 
     * @return Anwendungsdateien, falls existent
     */
    public List<File> getOtherImageOpenApps() {
        return FileUtil.getAsFiles(settings.getStringArray(keyOtherImageOpenApps));
    }

    private void setOtherImageOpenApps(ListModel model) {
        List<String> apps = new ArrayList<String>();
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            apps.add(model.getElementAt(i).toString());
        }
        if (apps.isEmpty()) {
            settings.getProperties().remove(keyOtherImageOpenApps);
        } else {
            settings.setStringArray(apps, keyOtherImageOpenApps);
        }
    }

    /**
     * Liefert, ob beim automatischen Scan von Verzeichnissen auch die
     * Unterverzeichnisse einbezogen werden sollen.
     * 
     * @return true, falls die Unterverzeichnisse einbezogen werden sollen
     */
    public boolean isAutoscanIncludeSubdirectories() {
        return settings.getProperties().containsKey(keyIsAutoscanIncludeSubdirectories)
            ? settings.getBoolean(keyIsAutoscanIncludeSubdirectories)
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
            Logger.getLogger(UserSettingsDialog.class.getName()).log(Level.WARNING, ex.getMessage());
            PersistentSettings.getInstance().setString(XMLFormatter.class.getName(), keyLogfileFormatterClass);
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
        return settings.getProperties().containsKey(keyIsTaskRemoveRecordsWithNotExistingFiles)
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
        return settings.getProperties().containsKey(keyIsUseAutocomplete)
            ? settings.getBoolean(keyIsUseAutocomplete)
            : true;
    }

    /**
     * Returns whether directory choosers and -trees shall show hidden
     * directories and if directory scans shall include them.
     * 
     * @return true, if accepted
     */
    public boolean isAcceptHiddenDirectories() {
        return settings.getProperties().containsKey(keyIsAcceptHiddenDirectories)
            ? settings.getBoolean(keyIsAcceptHiddenDirectories)
            : false;
    }

    private UserSettings() {
    }

    @Override
    public void applySettings(UserSettingsChangeEvent evt) {
        UserSettingsDialog dialog = (UserSettingsDialog) evt.getSource();
        UserSettingsChangeEvent.Type type = evt.getType();

        if (type.equals(UserSettingsChangeEvent.Type.DefaultImageOpenApp)) {
            settings.setString(dialog.labelDefaultImageOpenApp.getText(),
                keyDefaultImageOpenApp);
        } else if (type.equals(UserSettingsChangeEvent.Type.ExternalThumbnailCreationCommand)) {
            settings.setString(dialog.textFieldExternalThumbnailCreationCommand.getText(),
                keyExternalThumbnailCreationCommand);
        } else if (type.equals(UserSettingsChangeEvent.Type.FastSearchColumnDefined)) {
            settings.setString(
                getSearchColumnKeys(dialog.checkListSearchColumns, dialog.searchColumnsListModel),
                keyFastSearchColumns);
        } else if (type.equals(UserSettingsChangeEvent.Type.IptcCharset)) {
            settings.setString(dialog.comboBoxIptcCharset.getSelectedItem().toString(),
                keyIptcCharset);
        } else if (type.equals(UserSettingsChangeEvent.Type.IsAcceptHiddenDirectories)) {
            settings.setBoolean(dialog.checkBoxIsAcceptHiddenDirectories.isSelected(),
                keyIsAcceptHiddenDirectories);
        } else if (type.equals(UserSettingsChangeEvent.Type.IsAutoscanIncludeSubdirectories)) {
            settings.setBoolean(dialog.checkBoxIsAutoscanIncludeSubdirectories.isSelected(),
                keyIsAutoscanIncludeSubdirectories);
        } else if (type.equals(UserSettingsChangeEvent.Type.IsCreateThumbnailsWithExternalApp)) {
            setCreateThumbnailsWithExternalApp(dialog.checkBoxIsCreateThumbnailsWithExternalApp);
        } else if (type.equals(UserSettingsChangeEvent.Type.IsTaskRemoveRecordsWithNotExistingFiles)) {
            settings.setBoolean(dialog.checkBoxIsTaskRemoveRecordsWithNotExistingFiles.isSelected(),
                keyIsTaskRemoveRecordsWithNotExistingFiles);
        } else if (type.equals(UserSettingsChangeEvent.Type.IsUseAutocomplete)) {
            settings.setBoolean(!dialog.checkBoxIsAutocompleteDisabled.isSelected(),
                keyIsUseAutocomplete);
        } else if (type.equals(UserSettingsChangeEvent.Type.IsUseEmbeddedThumbnails)) {
            setUseEmbeddedThumbnails(dialog.checkBoxIsUseEmbeddedThumbnails);
        } else if (type.equals(UserSettingsChangeEvent.Type.LogfileFormatterClass)) {
            setLogfileFormatterClass(dialog.comboBoxLogfileFormatterClass.getSelectedItem());
        } else if (type.equals(UserSettingsChangeEvent.Type.LogLevel)) {
            settings.setString(dialog.comboBoxLogLevel.getSelectedItem().toString(),
                keyLogLevel);
        } else if (type.equals(UserSettingsChangeEvent.Type.MaxThumbnailWidth)) {
            settings.setString(dialog.spinnerMaxThumbnailWidth.getValue().toString(),
                keyMaxThumbnailWidth);
        } else if (type.equals(UserSettingsChangeEvent.Type.MinutesToStartScheduledTasks)) {
            settings.setString(dialog.spinnerMinutesToStartScheduledTasks.getValue().toString(),
                keyMinutesToStartScheduledTasks);
        } else if (type.equals(UserSettingsChangeEvent.Type.NoFastSearchColumns)) {
            settings.getProperties().remove(keyFastSearchColumns);
        } else if (type.equals(UserSettingsChangeEvent.Type.OtherImageOpenApps)) {
            setOtherImageOpenApps(dialog.listOtherImageOpenApps.getModel());
        } else if (type.equals(UserSettingsChangeEvent.Type.ThreadPriority)) {
            setThreadPriority((ComboBoxModelThreadPriority) dialog.comboBoxThreadPriority.getModel());
        }
    }
}
