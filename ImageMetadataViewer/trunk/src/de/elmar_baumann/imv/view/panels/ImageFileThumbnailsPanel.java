package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.controller.thumbnail.ControllerDoubleklickThumbnail;
import de.elmar_baumann.imv.data.ThumbnailFlag;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.datatransfer.TransferHandlerThumbnailsPanel;
import de.elmar_baumann.imv.event.listener.RefreshListener;
import de.elmar_baumann.lib.comparator.FileSort;
import de.elmar_baumann.imv.types.FileAction;
import de.elmar_baumann.imv.view.InfoSetThumbnails;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
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
public final class ImageFileThumbnailsPanel extends ThumbnailsPanel {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final Map<Content, List<RefreshListener>> refreshListenersOfContent =
            new HashMap<Content, List<RefreshListener>>();
    private final PopupMenuPanelThumbnails popupMenu =
            PopupMenuPanelThumbnails.INSTANCE;
    private final List<File> files = Collections.synchronizedList(
            new ArrayList<File>());
    private ControllerDoubleklickThumbnail controllerDoubleklick;
    private FileSort fileSort = FileSort.NAMES_ASCENDING;
    private volatile boolean hadFiles;
    private Content content = Content.UNDEFINED;
    private FileAction fileAction = FileAction.UNDEFINED;

    public ImageFileThumbnailsPanel() {
        initMap();
        controllerDoubleklick = new ControllerDoubleklickThumbnail(this);
        setDragEnabled(true);
        setTransferHandler(new TransferHandlerThumbnailsPanel());
    }

    private void initMap() {
        for (Content c : Content.values()) {
            refreshListenersOfContent.put(c, new ArrayList<RefreshListener>());
        }
    }

    public synchronized Content getContent() {
        return content;
    }

    /**
     * Returns the file action.
     * 
     * <em>This class is not responsible to take care of the file action! I.e.
     * if the action is not longer valid, the caller must set it to a valid
     * action type.</em>
     * 
     * @return file action
     */
    public synchronized FileAction getFileAction() {
        return fileAction;
    }

    /**
     * Sets the file action.
     * 
     * @param fileAction  file action
     */
    public synchronized void setFileAction(FileAction fileAction) {
        this.fileAction = fileAction;
    }

    /**
     * Adds a refresh listener for a specific content.
     * 
     * @param listener  listener
     * @param content   content
     */
    public void addRefreshListener(RefreshListener listener, Content content) {
        synchronized (refreshListenersOfContent) {
            refreshListenersOfContent.get(content).add(listener);
        }
    }

    private void notifyRefreshListeners() {
        synchronized (refreshListenersOfContent) {
            for (RefreshListener listener : refreshListenersOfContent.get(
                    content)) {
                listener.refresh();
            }
        }
    }

    /**
     * Returns all displayed files.
     * 
     * @return files
     */
    public synchronized List<File> getFiles() {
        return new ArrayList<File>(files);
    }

    /**
     * Returns the number of Thumbnails.
     * 
     * @return thumbnail count
     */
    public synchronized int getCount() {
        return files.size();
    }

    /**
     * Returns whether a file is selected.
     * 
     * @param   file  file
     * @return  true if selected
     */
    public synchronized boolean isSelected(File file) {
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
    public synchronized void setFiles(List<File> files, Content content) {
        this.files.clear();
        this.files.addAll(files);
        this.content = content;
        Thread thread = new Thread(new SetFiles(this));
        thread.setName("Setting files to thumbnails panel" + " @ " + // NOI18N
                getClass().getName());
        thread.start();
    }

    private class SetFiles implements Runnable {

        private final ThumbnailsPanel panel;

        public SetFiles(ThumbnailsPanel panel) {
            this.panel = panel;
        }

        @Override
        public void run() {
            InfoSetThumbnails info = new InfoSetThumbnails();
            if (!content.equals(Content.IMAGE_COLLECTION)) {
                Collections.sort(files, fileSort.getComparator());
            }
            setNewThumbnails(files.size());
            scrollToTop(hadFiles);
            hadFiles = true;
            setMissingFilesFlags();
            panel.forceRepaint();
            info.hide();
        }
    }

    /**
     * Sorts the files.
     * 
     * @see #setSort(de.elmar_baumann.lib.comparator.FileSort)
     */
    public synchronized void sort() {
        List<File> selectedFiles = getSelectedFiles();
        setFiles(new ArrayList<File>(files), content);
        setSelected(getIndices(selectedFiles, true));
    }

    /**
     * Sets a sort type, does <em>not</em> sort.
     * 
     * @param fileSort  sort type
     * @see #sort()
     */
    public synchronized void setSort(FileSort fileSort) {
        this.fileSort = fileSort;
    }

    /**
     * Returns the sort type.
     * 
     * @return sort type
     */
    public synchronized FileSort getSort() {
        return fileSort;
    }

    /**
     * Renames a file <strong>on the display</strong>, <em>not</em> in the
     * file system.
     * 
     * @param oldFile  old file
     * @param newFile  new file
     */
    public synchronized void rename(File oldFile, File newFile) {
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
    public synchronized void remove(List<File> filesToRemove) {
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
            refresh();
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

    public synchronized void setDefaultThumbnailWidth(int width) {
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
     * {@link de.elmar_baumann.imv.event.listener.RefreshListener} objects.
     */
    public synchronized void refresh() {
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
    public synchronized void repaint(File file) {
        int index = getIndexOf(file);
        if (index >= 0) {
            removeFromCache(index);
        }
    }

    @Override
    public synchronized Image getThumbnail(int index) {
        return db.getThumbnail(files.get(index).getAbsolutePath());
    }

    /**
     * Returns the index of a specific file.
     * 
     * @param  file  file
     * @return Index or -1 if not displayed
     */
    public synchronized int getIndexOf(File file) {
        return files.indexOf(file);
    }

    /**
     * Returns wheter an index of a file is valid.
     * 
     * @param  index index
     * @return true if valid
     */
    public synchronized boolean isIndex(int index) {
        return index >= 0 && index < files.size();
    }

    /**
     * Returns a specific file.
     * 
     * @param  index index
     * @return file or null if the index is invalid
     * @see    #isIndex(int)
     */
    public synchronized File getFile(int index) {
        return isIndex(index)
                ? files.get(index)
                : null;
    }

    /**
     * Returns the filenames of selected thumbnails.
     * 
     * @return filenames
     */
    public synchronized List<File> getSelectedFiles() {
        return getFiles(getSelectedIndices());
    }

    /**
     * Returns the files with an specific index.
     * 
     * @param  indices  file indices
     * @return files of valid indices
     */
    public synchronized List<File> getFiles(List<Integer> indices) {
        List<File> f = new ArrayList<File>();
        for (Integer index : indices) {
            if (isIndex(index)) {
                f.add(files.get(index));
            }
        }
        return f;
    }

    public synchronized void moveSelectedToIndex(int index) {
        if (!isValidIndex(index)) return;
        List<Integer> selectedIndices = getSelectedIndices();
        Collections.sort(selectedIndices);
        List<File> selFiles = getFiles(selectedIndices);
        List<File> filesWithoutMoved = new ArrayList<File>(files);
        int fileCount = filesWithoutMoved.size();
        filesWithoutMoved.removeAll(selFiles);
        List<File> newOrderedFiles = new ArrayList<File>(fileCount);
        newOrderedFiles.addAll(filesWithoutMoved.subList(0, index));
        newOrderedFiles.addAll(selFiles);
        newOrderedFiles.addAll(filesWithoutMoved.subList(index,
                filesWithoutMoved.size()));
        setFiles(newOrderedFiles, content);
    }

    @Override
    protected String getText(int index) {
        if (isIndex(index)) {
            String heading = files.get(index).getAbsolutePath();
            int indexPathSeparator = heading.lastIndexOf(File.separator);
            if (indexPathSeparator >= 0 && indexPathSeparator + 1 < heading.
                    length()) {
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
        popupMenu.show(this, e.getX(), e.getY());
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
}
