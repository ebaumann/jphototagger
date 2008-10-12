package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.controller.thumbnail.ControllerDoubleklickThumbnail;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.data.ThumbnailFlag;
import de.elmar_baumann.imv.event.DatabaseAction;
import de.elmar_baumann.imv.event.DatabaseListener;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.UserSettingsChangeListener;
import de.elmar_baumann.imv.view.dialogs.UserSettingsDialog;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JViewport;

/**
 * Zeigt Thumbnails von Bilddateien.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ImageFileThumbnailsPanel extends ThumbnailsPanel
    implements UserSettingsChangeListener, DatabaseListener {

    private List<String> filenames = new ArrayList<String>();
    private Database db = Database.getInstance();
    private PopupMenuPanelThumbnails popupMenu = PopupMenuPanelThumbnails.getInstance();
    private ControllerDoubleklickThumbnail controllerDoubleklickThumbnail;
    private boolean persitentSelectionsApplied = false;
    private JViewport viewport;
    private static final String keyFilenames = "de.elmar_baumann.imv.view.panels.Filenames"; // NOI18N

    public ImageFileThumbnailsPanel() {
        setThumbnailCount(0);
        controllerDoubleklickThumbnail = new ControllerDoubleklickThumbnail(this);
        UserSettingsDialog.getInstance().addChangeListener(this);
        db.addDatabaseListener(this);
    }

    @Override
    public void applySettings(UserSettingsChangeEvent evt) {
        if (evt.getChanged().equals(UserSettingsChangeEvent.Changed.ThumbnailWidth)) {
            setThumbnailWidth(UserSettings.getInstance().getMaxThumbnailWidth());
            repaint();
        } else if (evt.getChanged().equals(UserSettingsChangeEvent.Changed.OtherOpenImageApps)) {
            popupMenu.addOtherOpenImageApps();
        }
    }

    public List<String> getFilenames() {
        return filenames;
    }

    /**
     * Liefert die Anzahl der Thumbnails.
     * 
     * @return Thumbnailanzahl
     */
    public int getThumbnailCount() {
        return filenames.size();
    }

    /**
     * Setzt den Viewport. <em>Ist ganz am Anfang aufzurufen!</em>
     * 
     * @param viewport  Viewport
     */
    public void setViewport(JViewport viewport) {
        this.viewport = viewport;
    }

    /**
     * Liefert, ob das Thumbnail einer Datei selektiert ist.
     * 
     * @param   filename Dateiname
     * @return  true, wenn selektiert
     */
    public boolean isThumbnailSelected(String filename) {
        List<String> files = getSelectedFilenames();
        for (String file : files) {
            if (file.equals(filename)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Setzt die Dateinamen neu anzuzeigender Bilder, die bisherigen werden
     * nicht mehr angezeigt.
     * 
     * @param filenames Dateinamen
     */
    public void setFilenames(List<String> filenames) {
        this.filenames = filenames;
        empty();
        setThumbnailWidth(UserSettings.getInstance().getMaxThumbnailWidth());
        setMissingFilesFlags();
        setThumbnailCount(filenames.size());
        readPersistentSelectedFiles();
        scrollToFirstThumbnailRow();
        repaint();
    }

    private void scrollToFirstThumbnailRow() {
        if (viewport != null) {
            viewport.setViewPosition(new Point(0, 0));
        }
    }

    private void setMissingFilesFlags() {
        int count = filenames.size();
        for (int i = 0; i < count; i++) {
            if (!FileUtil.existsFile(filenames.get(i))) {
                addThumbnailFlag(i, ThumbnailFlag.ErrorFileNotFound);
            }
        }
    }

    /**
     * Mitteilen, dass die Anwendung beendet wird.
     */
    public void beforeQuit() {
        controllerDoubleklickThumbnail.stop();
        writePersistent();
    }

    private void writePersistent() {
        List<Integer> indices = getIndicesSelectedThumbnails();
        List<String> selectedFilenames = new ArrayList<String>();
        int countFilenames = filenames.size();
        for (Integer index : indices) {
            if (index >= 0 && index < countFilenames) {
                selectedFilenames.add(filenames.get(index));
            }
        }
        PersistentSettings.getInstance().setStringArray(selectedFilenames, keyFilenames);
    }

    private void repaintThumbnail(String filename) {
        int index = getThumbnailIndexOf(filename);
        if (index >= 0) {
            removeFromCache(index);
        }
    }

    @Override
    public Image getThumbnailAtIndex(int index) {
        return db.getThumbnail(filenames.get(index));
    }

    /**
     * Liefert den Thumbnailindex für einen Dateinamen.
     * 
     * @param filename Dateiname
     * @return         Index oder -1 wenn das Thumbnail der Datei nicht angezeigt wird
     */
    public int getThumbnailIndexOf(String filename) {
        return filenames.indexOf(filename);
    }

    /**
     * Liefert, ob es für einen Index ein Thumbnail gibt.
     * 
     * @param index Index
     * @return      true, wenn es für den Index ein Thumbnail gibt
     */
    public boolean isThumbnailIndex(int index) {
        return index >= 0 && index < filenames.size();
    }

    /**
     * Liefert den Dateinamen für ein bestimmtes Thumbnail.
     * 
     * @param index Index des Thumbnails
     * @return      Dateiname, Leerstring bei ungültigem Index
     * @see         #isThumbnailIndex(int)
     */
    public String getThumbnailFilenameAtIndex(int index) {
        if (isThumbnailIndex(index)) {
            return filenames.get(index);
        }
        return ""; // NOI18N
    }

    /**
     * Liefert die Dateiennamen aller selektierten Thumbnails.
     * 
     * @return Dateinamen
     */
    public List<String> getSelectedFilenames() {
        return getFilenamesOfIndices(getIndicesSelectedThumbnails());
    }

    /**
     * Liefert die Dateinamen bestimmter Indexe.
     * 
     * @param indices Indexe
     * @return        Dateinamen
     */
    public List<String> getFilenamesOfIndices(List<Integer> indices) {
        List<String> fNames = new ArrayList<String>();
        for (Integer index : indices) {
            fNames.add(getThumbnailFilenameAtIndex(index.intValue()));
        }
        return fNames;
    }

    private void readPersistentSelectedFiles() {
        if (!persitentSelectionsApplied && getSelectionCount() == 0) {
            List<String> storedFilenames = PersistentSettings.getInstance().getStringArray(keyFilenames);
            List<Integer> indices = new ArrayList<Integer>();
            for (String filename : storedFilenames) {
                int index = filenames.indexOf(filename);
                if (index >= 0) {
                    indices.add(index);
                }
            }
            setIndicesSelectedThumbnails(indices);
            persitentSelectionsApplied = true;
        }
    }

    @Override
    protected String getTextForThumbnailAtIndex(int index) {
        if (isThumbnailIndex(index)) {
            String heading = filenames.get(index);
            int indexPathSeparator = heading.lastIndexOf(File.separator);
            if (indexPathSeparator >= 0 && indexPathSeparator + 1 < heading.length()) {
                heading = heading.substring(indexPathSeparator + 1);
            }
            return heading;
        }
        return ""; // NOI18N
    }

    @Override
    protected void doubleClickAtIndex(int index) {
        controllerDoubleklickThumbnail.doubleClickAtIndex(index);
    }

    @Override
    protected void showPopupMenu(MouseEvent e) {
        if (getSelectionCount() > 0) {
            popupMenu.setThumbnailsPanel(this);
            popupMenu.show(this, e.getX(), e.getY());
        }
    }

    @Override
    protected void showToolTip(MouseEvent evt) {
        int index = getThumbnailIndexAtPoint(evt.getX(), evt.getY());
        setToolTipText(createTooltipText(index));
    }

    private String createTooltipText(int index) {
        if (isThumbnailIndex(index)) {
            String filename = filenames.get(index);
            String flagText = ""; // NOI18N
            ThumbnailFlag flag = getFlagOfThumbnail(index);
            if (flag != null) {
                flagText = " - " + flag.getString(); // NOI18N
            }
            return filename + flagText;
        } else {
            return ""; // NOI18N
        }
    }

    @Override
    public void actionPerformed(DatabaseAction action) {
        DatabaseAction.Type type = action.getType();
        if (type.equals(DatabaseAction.Type.ThumbnailUpdated)) {
            repaintThumbnail(action.getFilename());
        } else if (type.equals(DatabaseAction.Type.ImageFilesDeleted)) {
            // TODO: Thumbnail entfernen
        }
    }
}
