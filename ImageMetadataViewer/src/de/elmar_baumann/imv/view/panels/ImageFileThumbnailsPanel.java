package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.controller.thumbnail.ControllerDoubleklickThumbnail;
import de.elmar_baumann.imv.data.ThumbnailFlag;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.RefreshListener;
import de.elmar_baumann.imv.io.FileSort;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JViewport;

/**
 * Zeigt Thumbnails von Bilddateien.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ImageFileThumbnailsPanel extends ThumbnailsPanel {

    private DatabaseImageFiles db = DatabaseImageFiles.getInstance();
    private List<File> files = new ArrayList<File>();
    private PopupMenuPanelThumbnails popupMenu = PopupMenuPanelThumbnails.getInstance();
    private ControllerDoubleklickThumbnail controllerDoubleklick;
    private FileSort fileSort = FileSort.NamesAscending;
    private boolean hadFiles = false;
    private Content content = Content.Undefined;
    private Map<Content, List<RefreshListener>> refreshListenersOfContent = new HashMap<Content, List<RefreshListener>>();

    public ImageFileThumbnailsPanel() {
        initMap();
        setNewThumbnails(0);
        controllerDoubleklick = new ControllerDoubleklickThumbnail(this);
    }

    private void initMap() {
        for (Content c : Content.values()) {
            refreshListenersOfContent.put(c, new ArrayList<RefreshListener>());
        }
    }

    public Content getContent() {
        return content;
    }

    /**
     * Adds a refresh listener for a specific content.
     * 
     * @param listener  listener
     * @param content   content
     */
    public void addRefreshListener(RefreshListener listener, Content content) {
        refreshListenersOfContent.get(content).add(listener);
    }

    /**
     * Removes a refresh listener for a specific content.
     * 
     * @param listener  listener
     * @param content   content
     */
    public void removeRefreshListener(RefreshListener listener, Content content) {
        refreshListenersOfContent.get(content).remove(listener);
    }

    private void notifyRefreshListeners() {
        for (RefreshListener listener : refreshListenersOfContent.get(content)) {
            listener.refresh();
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
     * @param files    files
     * @param content  content description of the files
     */
    public void setFiles(List<File> files, Content content) {
        boolean scrollToTop = hadFiles && files != this.files;
        this.files = files;
        this.content = content;
        Collections.sort(files, fileSort.getComparator());
        setNewThumbnails(files.size());
        scrollToTop(scrollToTop);
        setMissingFilesFlags();
        checkDivider();
        hadFiles = true;
    }

    /**
     * Sorts the files.
     * 
     * @see #setSort(de.elmar_baumann.imv.io.FileSort)
     */
    public void sort() {
        List<File> selectedFiles = getSelectedFiles();
        setFiles(files, content);
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
            setFiles(files, content);
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

    public void setDefaultThumbnailWidth(int width) {
        setThumbnailWidth(width);
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
     * Calls <code>refresh()</code> by all added
     * {@link de.elmar_baumann.imv.event.RefreshListener} objects.
     */
    public void refresh() {
        JViewport viewport = getViewport();
        Point viewportPosition = null;
        if (viewport != null) {
            viewportPosition = viewport.getViewPosition();
        }
        notifyRefreshListeners();
        if (viewport != null) {
            viewport.setViewPosition(viewportPosition);
        }
    }

    /**
     * Repaints a file.
     * 
     * @param file  file
     */
    public void repaint(File file) {
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

    private List<File> getFiles(List<Integer> indices) {
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
            popupMenu.setContent(content);
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

    private void checkDivider() {
        if (!hadFiles) {
            AppPanel appPanel = Panels.getInstance().getAppPanel();
            int location = PersistentSettings.getInstance().getInt(
                appPanel.getKeyDividerLocationThumbnails());
            appPanel.getSplitPaneThumbnailsMetadata().setDividerLocation(location);
        }
    }

}
