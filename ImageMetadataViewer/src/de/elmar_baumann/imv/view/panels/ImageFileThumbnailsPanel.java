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
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.dialogs.UserSettingsDialog;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Zeigt Thumbnails von Bilddateien.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ImageFileThumbnailsPanel extends ThumbnailsPanel
    implements UserSettingsChangeListener, DatabaseListener {

    private List<File> files = new ArrayList<File>();
    private Database db = Database.getInstance();
    private PopupMenuPanelThumbnails popupMenu = PopupMenuPanelThumbnails.getInstance();
    private ControllerDoubleklickThumbnail controllerDoubleklick;
    private boolean persitentSelectionsApplied = false;
    private static final String keyFilenames = "de.elmar_baumann.imv.view.panels.Filenames"; // NOI18N
    private static final String keySort = "de.elmar_baumann.imv.view.panels.Sort"; // NOI18N
    private static final String keyThumbnailPanelViewportViewPosition = "de.elmar_baumann.imv.view.panels.AppPanel.scrollPaneThumbnailsPanel"; // NOI18N
    private FileSort fileSort = FileSort.NamesAscending;

    public ImageFileThumbnailsPanel() {
        setNewThumbnails(0);
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
     * Returns all displayed files.
     * 
     * @return files
     */
    public List<File> getFiles() {
        return files;
    }

    /**
     * Returns the number of Thumbnails.
     * 
     * @return thumbnail count
     */
    public int getCount() {
        return files.size();
    }

    /**
     * Returns whether a file is selected.
     * 
     * @param   file  file
     * @return  true if selected
     */
    public boolean isSelected(File file) {
        List<File> selectedFiles = getSelectedFiles();
        for (File selectedFile : selectedFiles) {
            if (file.equals(selectedFile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the files to display. Previous desplayed files will be hided.
     * The new files will be displayed in the defined sort order.
     * 
     * @param files  files
     */
    public void setFiles(List<File> files) {
        boolean scrollToTop = files != this.files;
        this.files = files;
        Collections.sort(files, fileSort.getComparator());
        setDefaultThumbnailWidth();
        setNewThumbnails(files.size());
        scrollToTop(scrollToTop);
        setMissingFilesFlags();
        readPersistentSelectedFiles();
    }

    /**
     * Sorts the files.
     * 
     * @see #setSort(de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel.Sort)
     */
    public void sort() {
        List<File> selectedFiles = getSelectedFiles();
        setFiles(files);
        setSelected(getIndices(selectedFiles, true));
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
     * Renames a file <strong>on the display</strong>, <em>not</em> in the
     * file system.
     * 
     * @param oldFile  old file
     * @param newFile  new file
     */
    public void rename(File oldFile, File newFile) {
        int index = files.indexOf(oldFile);
        if (index >= 0) {
            files.set(index, newFile);
            repaint();
        }
    }

    /**
     * Removes files from the <strong>display</strong>, <em>not</em> from the
     * file system.
     * 
     * @param filesToRemove  files to remove
     */
    public void remove(List<File> filesToRemove) {
        int removed = 0;
        List<File> selectedFiles = getSelectedFiles();
        for (File fileToRemove : filesToRemove) {
            int index = getIndexOf(fileToRemove);
            if (index >= 0) {
                files.remove(index);
                selectedFiles.remove(fileToRemove);
                removed++;
            }
        }
        if (removed > 0) {
            setFiles(files);
            setSelected(getIndices(selectedFiles, true));
        }
    }

    private List<Integer> getIndices(List<File> fileArray, boolean onlyIfExists) {
        List<Integer> indices = new ArrayList<Integer>(fileArray.size());
        for (File file : fileArray) {
            int index = files.indexOf(file);
            if (!onlyIfExists || (onlyIfExists && index >= 0)) {
                indices.add(index);
            }
        }
        return indices;
    }

    private void setDefaultThumbnailWidth() {
        if (getThumbnailWidth() <= 0) {
            setThumbnailWidth(UserSettings.getInstance().getMaxThumbnailWidth());
        }
    }

    private void setMissingFilesFlags() {
        int count = files.size();
        for (int i = 0; i < count; i++) {
            if (!files.get(i).exists()) {
                addFlag(i, ThumbnailFlag.ErrorFileNotFound);
            }
        }
    }

    /**
     * Refreshes the display, e.g. after a rename action.
     */
    public void refresh() {
        sort();
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
        writePersistentViewportViewPosition();
    }

    private void writePersistentSelection() {
        PersistentSettings.getInstance().setStringArray(
            FileUtil.getAsFilenames(getSelectedFiles()), keyFilenames);
    }

    private void writePersistentSort() {
        PersistentSettings.getInstance().setString(fileSort.name(), keySort);
    }

    private void readPersistentSelectedFiles() {
        if (!persitentSelectionsApplied && getSelectionCount() == 0) {
            List<String> storedFilenames = PersistentSettings.getInstance().getStringArray(keyFilenames);
            List<Integer> indices = new ArrayList<Integer>();
            for (String filename : storedFilenames) {
                int index = files.indexOf(new File(filename));
                if (index >= 0) {
                    indices.add(index);
                }
            }
            setSelected(indices);
            persitentSelectionsApplied = true;
        }
    }

    private void readPersistent() {
        readPersistentSort();
        // to early, even in first call of setFiles()
        //readPersistentViewportViewPosition();
    }

    private void readPersistentSort() {
        String name = PersistentSettings.getInstance().getString(keySort);
        try {
            fileSort = FileSort.valueOf(name);
        } catch (Exception ex) {
        }
    }

    private void readPersistentViewportViewPosition() {
        PersistentSettings.getInstance().getScrollPane(
            Panels.getInstance().getAppPanel().getScrollPaneThumbnailsPanel(),
            keyThumbnailPanelViewportViewPosition);
    }

    private void writePersistentViewportViewPosition() {
        PersistentSettings.getInstance().setScrollPane(
            Panels.getInstance().getAppPanel().getScrollPaneThumbnailsPanel(),
            keyThumbnailPanelViewportViewPosition);
    }

    private void repaint(File file) {
        int index = getIndexOf(file);
        if (index >= 0) {
            removeFromCache(index);
        }
    }

    @Override
    public Image getThumbnail(int index) {
        return db.getThumbnail(files.get(index).getAbsolutePath());
    }

    /**
     * Returns the index of a specific file.
     * 
     * @param  file  file
     * @return Index or -1 if not displayed
     */
    public int getIndexOf(File file) {
        return files.indexOf(file);
    }

    /**
     * Returns wheter an index of a file is valid.
     * 
     * @param  index index
     * @return true if valid
     */
    public boolean isIndex(int index) {
        return index >= 0 && index < files.size();
    }

    /**
     * Returns a specific file.
     * 
     * @param  index index
     * @return file or null if the index is invalid
     * @see    #isIndex(int)
     */
    public File getFile(int index) {
        return isIndex(index) ? files.get(index) : null;
    }

    /**
     * Returns the filenames of selected thumbnails.
     * 
     * @return filenames
     */
    public List<File> getSelectedFiles() {
        return getFiles(getSelected());
    }

    /**
     * Returns files at specific indices.
     * 
     * @param  indices  indices
     * @return files
     */
    public List<File> getFiles(List<Integer> indices) {
        List<File> f = new ArrayList<File>();
        for (Integer index : indices) {
            if (isIndex(index)) {
                f.add(files.get(index));
            }
        }
        return f;
    }

    @Override
    protected String getText(int index) {
        if (isIndex(index)) {
            String heading = files.get(index).getAbsolutePath();
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
            String filename = files.get(index).getAbsolutePath();
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
            repaint(new File(action.getFilename()));
        } else if (type.equals(DatabaseAction.Type.ImageFilesDeleted)) {
            List<File> deleted = FileUtil.getAsFiles(action.getFilenames());
            remove(deleted);
        }
    }
}
