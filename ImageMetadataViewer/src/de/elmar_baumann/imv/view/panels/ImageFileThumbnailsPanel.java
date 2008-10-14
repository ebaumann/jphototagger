package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.controller.thumbnail.ControllerDoubleklickThumbnail;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.data.ThumbnailFlag;
import de.elmar_baumann.imv.event.DatabaseAction;
import de.elmar_baumann.imv.event.DatabaseListener;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.UserSettingsChangeListener;
import de.elmar_baumann.imv.io.FileSort;
import de.elmar_baumann.imv.view.dialogs.UserSettingsDialog;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
    private ControllerDoubleklickThumbnail controllerDoubleklick;
    private boolean persitentSelectionsApplied = false;
    private JViewport viewport;
    private static final String keyFilenames = "de.elmar_baumann.imv.view.panels.Filenames"; // NOI18N
    private static final String keySort = "de.elmar_baumann.imv.view.panels.Sort"; // NOI18N
    private FileSort fileSort = FileSort.NamesAscending;

    public ImageFileThumbnailsPanel() {
        setCount(0);
        controllerDoubleklick = new ControllerDoubleklickThumbnail(this);
        UserSettingsDialog.getInstance().addChangeListener(this);
        db.addDatabaseListener(this);
        readPersistent();
    }

    @Override
    public void applySettings(UserSettingsChangeEvent evt) {
        if (evt.getType().equals(UserSettingsChangeEvent.Type.OtherOpenImageApps)) {
            popupMenu.addOtherOpenImageApps();
        }
    }

    /**
     * Returns the names of all displayed files.
     * 
     * @return filenames
     */
    public List<String> getFilenames() {
        return filenames;
    }

    /**
     * Returns the number of Thumbnails.
     * 
     * @return thumbnail count
     */
    public int getCount() {
        return filenames.size();
    }

    /**
     * Sets the viewport. Have to be called before adding files.
     * 
     * @param viewport  Viewport
     */
    public void setViewport(JViewport viewport) {
        this.viewport = viewport;
    }

    /**
     * Returns whether a thumbnail is selected.
     * 
     * @param   filename  filename
     * @return  true if selected
     */
    public boolean isSelected(String filename) {
        List<String> files = getSelectedFilenames();
        for (String file : files) {
            if (file.equals(filename)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the files to display. Removes the existing files.
     * 
     * @param filenames  filenames
     */
    public void setFilenames(List<String> filenames) {
        if (filenames != this.filenames) {
            this.filenames = filenames;
        }
        sortFilenames();
        empty();
        if (getThumbnailWidth() <= 0) {
            setThumbnailWidth(UserSettings.getInstance().getMaxThumbnailWidth());
        }
        setMissingFilesFlags();
        setCount(filenames.size());
        readPersistentSelectedFiles();
        if (filenames != this.filenames) {
            scrollToFirstRow();
        }
        repaint();
    }

    private void sortFilenames() {
        List<File> files = FileUtil.getAsFiles(filenames);
        Collections.sort(files, fileSort.getComparator());
        filenames = FileUtil.getAsFilenames(files);
    }

    /**
     * Sorts the files.
     * 
     * @see #setSort(de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel.Sort)
     */
    public void sort() {
        List<String> selectedNames = getSelectedFilenames();
        sortFilenames();
        setFilenames(filenames);
        setSelected(getIndices(selectedNames, true));
    }

    /**
     * Sets a sort type, does <em>not</em> sort.
     * 
     * @param fileSort  sort type
     * @see #sort()
     */
    public void setSort(FileSort fileSort) {
        this.fileSort = fileSort;
    }

    /**
     * Returns the sort type.
     * 
     * @return sort type
     */
    public FileSort getSort() {
        return fileSort;
    }

    /**
     * Renames a filename <strong>on the display</strong>, <em>not</em> in the
     * file system.
     * 
     * @param oldFilename old name
     * @param newFilename new name
     */
    public void rename(String oldFilename, String newFilename) {
        int index = filenames.indexOf(oldFilename);
        if (index >= 0) {
            filenames.set(index, newFilename);
            repaint();
        }
    }

    /**
     * Removes files from the <strong>display</strong>, <em>not</em> from the
     * file system.
     * 
     * @param names  file names to remove
     */
    public void remove(List<String> names) {
        int removed = 0;
        List<String> selectedFilenames = getSelectedFilenames();
        for (String filename : names) {
            int index = getIndexOf(filename);
            if (index >= 0) {
                filenames.remove(index);
                selectedFilenames.remove(filename);
                removed++;
            }
        }
        if (removed > 0) {
            setFilenames(filenames);
            setSelected(getIndices(selectedFilenames, true));
        }
    }

    private  List<Integer> getIndices(List<String> fNames, boolean onlyIfExists) {
        List<Integer> indices = new ArrayList<Integer>(fNames.size());
        for (String filename : fNames) {
            int index = filenames.indexOf(filename);
            if (!onlyIfExists || (onlyIfExists && index >= 0)) {
                indices.add(index);
            }
        }
        return indices;
    }

    private void scrollToFirstRow() {
        if (viewport != null) {
            viewport.setViewPosition(new Point(0, 0));
        }
    }

    private void setMissingFilesFlags() {
        int count = filenames.size();
        for (int i = 0; i < count; i++) {
            if (!FileUtil.existsFile(filenames.get(i))) {
                addFlag(i, ThumbnailFlag.ErrorFileNotFound);
            }
        }
    }

    /**
     * Tells that the application will quit. Writes persistent some values.
     */
    public void beforeQuit() {
        controllerDoubleklick.stop();
        writePersistent();
    }

    private void writePersistent() {
        writePersistentSelection();
        writePersistentSort();
    }

    private void writePersistentSelection() {
        List<Integer> indices = getSelected();
        List<String> selectedFilenames = new ArrayList<String>();
        int countFilenames = filenames.size();
        for (Integer index : indices) {
            if (index >= 0 && index < countFilenames) {
                selectedFilenames.add(filenames.get(index));
            }
        }
        PersistentSettings.getInstance().setStringArray(selectedFilenames, keyFilenames);
    }

    private void writePersistentSort() {
        PersistentSettings.getInstance().setString(fileSort.name(), keySort);
    }

    private void readPersistent() {
        readPersistentSort();
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
            setSelected(indices);
            persitentSelectionsApplied = true;
        }
    }

    private void readPersistentSort() {
        String name = PersistentSettings.getInstance().getString(keySort);
        try {
            fileSort = FileSort.valueOf(name);
        } catch (Exception ex) {
        }
    }

    private void repaint(String filename) {
        int index = getIndexOf(filename);
        if (index >= 0) {
            removeFromCache(index);
        }
    }

    @Override
    public Image getThumbnail(int index) {
        return db.getThumbnail(filenames.get(index));
    }

    /**
     * Returns the index of a specific thumbnail.
     * 
     * @param  filename  filename
     * @return Index or -1 if not displayed
     */
    public int getIndexOf(String filename) {
        return filenames.indexOf(filename);
    }

    /**
     * Returns wheter an index of a thumbnail is valid.
     * 
     * @param  index index
     * @return true if valid
     */
    public boolean isIndex(int index) {
        return index >= 0 && index < filenames.size();
    }

    /**
     * Returns the filename at an index.
     * 
     * @param index index
     * @return      filename
     * @see         #isIndex(int)
     */
    public String geFilename(int index) {
        if (isIndex(index)) {
            return filenames.get(index);
        }
        return ""; // NOI18N
    }

    /**
     * Returns the filenames of selected thumbnails.
     * 
     * @return filenames
     */
    public List<String> getSelectedFilenames() {
        return getFilenames(getSelected());
    }

    /**
     * Returns the filenames at specific indices.
     * 
     * @param  indices  indices
     * @return filenames
     */
    public List<String> getFilenames(List<Integer> indices) {
        List<String> fNames = new ArrayList<String>();
        for (Integer index : indices) {
            fNames.add(geFilename(index.intValue()));
        }
        return fNames;
    }

    @Override
    protected String getText(int index) {
        if (isIndex(index)) {
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
    protected void doubleClickAt(int index) {
        controllerDoubleklick.doubleClickAtIndex(index);
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
        int index = getIndexAtPoint(evt.getX(), evt.getY());
        setToolTipText(createTooltipText(index));
    }

    private String createTooltipText(int index) {
        if (isIndex(index)) {
            String filename = filenames.get(index);
            String flagText = ""; // NOI18N
            ThumbnailFlag flag = getFlag(index);
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
            repaint(action.getFilename());
        } else if (type.equals(DatabaseAction.Type.ImageFilesDeleted)) {
            // TODO: Thumbnail entfernen
        }
    }
}
