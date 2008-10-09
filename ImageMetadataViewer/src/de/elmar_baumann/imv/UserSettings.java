package de.elmar_baumann.imv;

import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.model.ComboBoxModelThreadPriority;
import de.elmar_baumann.imv.view.dialogs.UserSettingsDialog;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.XMLFormatter;
import javax.swing.ListModel;

/**
 * Benutzereinstellungen. Benutzt den Dialog
 * {@link de.elmar_baumann.imv.view.dialogs.UserSettingsDialog}.
 * Liest dessen Einstellungen. Motivation: Die Eingaben in etliche Controls des
 * Dialogs werden automatisch persistent gespeichert.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class UserSettings {

    private UserSettingsDialog settingsDialog = UserSettingsDialog.getInstance();
    private Database db = Database.getInstance();
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
        return settingsDialog.checkBoxExternalThumbnailApp.isSelected();
    }

    /**
     * Liefert die Befehlszeile des externen Programms, das die Thumbnails
     * erzeugt.
     * 
     * @return Befehlszeile
     * @see    #isCreateThumbnailsWithExternalApp()
     */
    public String getExternalThumbnailCreationCommand() {
        return settingsDialog.textFieldExternalThumbnailApp.getText();
    }

    /**
     * Liefert den Loglevel.
     * 
     * @return Loglevel (Eine Ausgabe von getLocalizedName())
     * @see    java.util.logging.Level#getLocalizedName()
     */
    public String getLogLevel() {
        Object item = settingsDialog.comboBoxLogLevel.getSelectedItem();
        if (item == null) {
            return Level.WARNING.getLocalizedName();
        } else {
            return item.toString();
        }
    }

    /**
     * Liefert alle Spalten für die Schnellsuche.
     * 
     * @return Suchspalten
     */
    public List<Column> getFastSearchColumns() {
        return settingsDialog.searchColumnsListModel.getTableColumns(
            settingsDialog.checkListSearchColumns.getSelectedItemIndices());
    }

    /**
     * Liefert die Anwendung, die ein Bild bei Doppelklick öffnen soll.
     * 
     * @return Anwendung oder Leerstring, wenn nicht definiert
     */
    public String getDefaultImageOpenApp() {
        return settingsDialog.labelImageOpenApp.getText();
    }

    /**
     * Liefert die Priorität für Threads zum Erzeugen von Thumbnails und Metadaten.
     * 
     * @return Threadpriorität
     */
    public int getThreadPriority() {
        ComboBoxModelThreadPriority model =
            (ComboBoxModelThreadPriority) settingsDialog.comboBoxThreadPriority.getModel();
        Object item = model.getSelectedItem();
        if (item == null) {
            return Thread.NORM_PRIORITY;
        } else {
            return model.getPriorityOf(item.toString());
        }
    }

    /**
     * Liefert die maximale Seitenlänge (längere Seite) von Thumbnails,
     * die skaliert werden.
     * 
     * @return Seitenlänge in Pixel
     */
    public int getMaxThumbnailLength() {
        return new Integer(settingsDialog.spinnerMaxThumbnailSize.getValue().
            toString()).intValue();
    }

    /**
     * Liefert die Anzahl der anzuzeigenden Thumbnails pro Zeile.
     * 
     * @return Thumbnailanzahl
     */
    public int getThumbnailCountPerRow() {
        return new Integer(settingsDialog.spinnerThumbnailCountPerRow.getValue().
            toString()).intValue();
    }

    /**
     * Liefert, ob eingebettete Thumbnails benutzt werden sollen.
     * 
     * @return true, wenn eingebettete Thumbnails benutzt werden sollen
     */
    public boolean isUseEmbeddedThumbnails() {
        return settingsDialog.checkBoxUseEmbeddedThumbnails.isSelected();
    }

    /**
     * Liefert den Zeichensatz, mit dem IPTC-Daten dekodiert werden sollen.
     * 
     * @return Zeichensatz
     */
    public String getIptcCharset() {
        Object item = settingsDialog.comboBoxIptcCharset.getItemAt(0);
        if (item == null) {
            return "ISO-8859-1"; // NOI18N
        } else {
            return item.toString();
        }
    }

    /**
     * Liefert alle Anwendungen, die ein Bild öffnen können.
     * 
     * @return Anwendungsdateien, falls existent
     */
    public List<File> getOtherImageOpenApps() {
        List<File> apps = new ArrayList<File>();
        ListModel model = settingsDialog.listOpenImageApps.getModel();
        int count = model.getSize();
        for (int i = 0; i < count; i++) {
            File file = new File(model.getElementAt(i).toString());
            if (file.exists()) {
                apps.add(file);
            }
        }
        return apps;
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
        return settingsDialog.checkBoxTasksAutoscanIncludeSubdirectories.isSelected();
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
     * Liefert, ob beim Start der Verzeichnisbaum ausgeklappt werden soll.
     * 
     * @return true, wenn ausklappen
     */
    public boolean isExpandDirectoriesTree() {
        return !settingsDialog.checkBoxDisableExpandDirectoriesTree.isSelected();
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

    private UserSettings() {
    }
}
