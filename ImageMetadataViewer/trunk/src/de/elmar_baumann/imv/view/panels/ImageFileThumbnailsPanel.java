package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.controller.thumbnail.ControllerDoubleklickThumbnail;
import de.elmar_baumann.imv.data.ThumbnailFlag;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.datatransfer.TransferHandlerPanelThumbnails;
import de.elmar_baumann.imv.event.listener.AppExitListener;
import de.elmar_baumann.imv.event.listener.RefreshListener;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.comparator.FileSort;
import de.elmar_baumann.imv.types.FileAction;
import de.elmar_baumann.imv.types.SizeUnit;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JViewport;

/**
 * Shows thumbnails of image files.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ImageFileThumbnailsPanel extends ThumbnailsPanel
        implements AppExitListener {

    private static final String KEY_THUMBNAIL_WIDTH =
            "ImageFileThumbnailsPanel.ThumbnailWidth"; // NOI18N
    private final Map<Content, List<RefreshListener>> refreshListenersOfContent =
            new HashMap<Content, List<RefreshListener>>();
    private final PopupMenuThumbnails popupMenu =
            PopupMenuThumbnails.INSTANCE;
    private final List<File> files = Collections.synchronizedList(
            new ArrayList<File>());
    private ControllerDoubleklickThumbnail controllerDoubleklick;
    private FileSort fileSort = FileSort.NAMES_ASCENDING;
    private volatile boolean hadFiles;
    private Content content = Content.UNDEFINED;
    private FileAction fileAction = FileAction.UNDEFINED;

    public ImageFileThumbnailsPanel() {
        initRefreshListeners();
        controllerDoubleklick = new ControllerDoubleklickThumbnail(this);
        setDragEnabled(true);
        setTransferHandler(new TransferHandlerPanelThumbnails());
        readProperties();
    }

    private void initRefreshListeners() {
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
            AppLog.logInfo(getClass(), "ImageFileThumbnailsPanel.Info.Refresh"); // NOI18N
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
    public synchronized int getFileCount() {
        return files.size();
    }

    /**
     * Returns whether a file is selected.
     * 
     * @param   file  file
     * @return  true if selected
     */
    public synchronized boolean isSelected(File file) {
        return getSelectedFiles().contains(file);
    }

    /**
     * Sets the files to display. Previous desplayed files will be hided.
     * The new files will be displayed in the defined sort order.
     *
     * @param files    files
     * @param content  content description of the files
     */
    public void setFiles(List<File> files, Content content) {
        synchronized (this) {
            this.files.clear();
            if (!content.equals(Content.IMAGE_COLLECTION)) {
                Collections.sort(files, fileSort.getComparator());
            }
            this.files.addAll(files);
            this.content = content;
            thumbnailCount = files.size();
        }

        setNewThumbnails();
        if (hadFiles) scrollToTop();
        hadFiles = true;
        setMissingFilesFlags();
    }

    /**
     * Sorts the files.
     * 
     * @see #setSort(de.elmar_baumann.lib.comparator.FileSort)
     */
    public synchronized void sort() {
        if (!content.equals(Content.IMAGE_COLLECTION)) {
            List<File> selectedFiles = getSelectedFiles();
            setFiles(new ArrayList<File>(files), content);
            setSelected(getIndices(selectedFiles, true));
        }
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
            thumbCache.updateFiles(oldFile, newFile);
            xmpCache.updateFiles(oldFile, newFile);
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
        List<File> selectedFiles = getSelectedFiles();
        if (files.removeAll(filesToRemove)) {
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

    private void setMissingFilesFlags() {
        int count = files.size();
        boolean missing = false;
        for (int i = 0; i < count; i++) {
            if (!files.get(i).exists()) {
                addFlag(i, ThumbnailFlag.ERROR_FILE_NOT_FOUND);
                missing = true;
            }
        }
        if (missing) repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        if (e.getKeyCode() == KeyEvent.VK_F5) {
            refresh();
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
        notifyRefreshListeners(); // does set the images
        if (viewport != null) {
            viewport.setViewPosition(viewportPosition);
        }
    }

    /**
     * Entfernt aus dem internen Bildcache ein Thumbnail und ImageFile und
     * liest sie bei Bedarf - wenn sie gezeichnet werden müssen - erneut ein.
     *
     * @param index Index
     */
    protected void removeFromCache(int index) {
        synchronized(this) {
            File file = files.get(index);
            thumbCache.removeEntry(file);
            xmpCache.removeEntry(file);
        }
        repaint();
    }
    
    public synchronized void removeFromCache(File file) {
        int index = files.indexOf(file);

        if (index >= 0) {
            removeFromCache(index);
        }
    }

    /**
     * Repaints a file.
     * 
     * @param file  file
     */
    public synchronized void removeAndRepaint(File file) {
        int index = getIndexOf(file);
        if (index >= 0) {
            removeFromCache(index);
        }
        repaint();
    }

    /**
     * Returns the index of a specific file.
     * 
     * @param  file  file
     * @return Index or -1 if not displayed
     */
    @Override
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
        if (selectedIndices.size() <= 0) return;
        Collections.sort(selectedIndices);
        if (selectedIndices.get(0) == index) return;
        List<File> selFiles = getFiles(selectedIndices);
        List<File> filesWithoutMoved = new ArrayList<File>(files);
        int fileCount = filesWithoutMoved.size();
        filesWithoutMoved.removeAll(selFiles);
        List<File> newOrderedFiles = new ArrayList<File>(fileCount);
        newOrderedFiles.addAll(filesWithoutMoved.subList(0, index));
        newOrderedFiles.addAll(selFiles);
        newOrderedFiles.addAll(filesWithoutMoved.subList(index,
                filesWithoutMoved.size()));
        files.clear();
        files.addAll(newOrderedFiles);
        clearSelection();
    }

    private List<Integer> getIndicesToEndFrom(int fromIndex) {
        int size = files.size();
        List<Integer> indices = new ArrayList<Integer>(size > 0
                                                       ? size
                                                       : 1);
        for (int i = fromIndex; i < size; i++) {
            indices.add(i);
        }
        return indices;
    }

    @Override
    protected String getText(int index) {
        if (isIndex(index)) {
            String filename = files.get(index).getAbsolutePath();
            int indexPathSeparator = filename.lastIndexOf(File.separator);
            if (indexPathSeparator >= 0 && indexPathSeparator + 1 < filename.
                    length()) {
                filename = filename.substring(indexPathSeparator + 1);
            }
            return filename;
        }
        return ""; // NOI18N
    }

    @Override
    protected synchronized List<String> getKeywords(int index) {
        Xmp xmp = xmpCache.getXmp(files.get(index));

        if (xmp == null) {
            return null;
        }

        return xmp.getDcSubjects();
    }

    @Override
    protected synchronized int getRating(int index) {
        Xmp xmp = xmpCache.getXmp(files.get(index));
        if (xmp == null) {
            return 0;
        }

        Long rating = xmp.getRating();
        if (rating == null) {
            return 0;
        }

        return xmp.getRating().intValue();
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
            File file = files.get(index);
            if (!file.exists()) return file.getAbsolutePath();
            ThumbnailFlag flag = getFlag(index);
            String flagText = flag == null
                              ? "" // NOI18N
                              : flag.getString();
            long length = file.length();
            SizeUnit unit = SizeUnit.unit(length);
            long unitLength = (long) (length / unit.bytes() + 0.5);
            Date date = new Date(file.lastModified());
            String unitString = unit.toString();
            return Bundle.getString("ImageFileThumbnailsPanel.TooltipText", // NOI18N
                    file, unitLength, unitString, date, date,
                    getSidecarFilename(file), flagText);
        } else {
            return ""; // NOI18N
        }
    }

    private static String getSidecarFilename(File file) {
        String sidecarfile = XmpMetadata.getSidecarFilenameOfImageFileIfExists(
                file.getAbsolutePath());
        return sidecarfile == null
               ? "" // NOI18N
               : sidecarfile;
    }

    public synchronized Image getThumbnail(int index) {
        return thumbCache.getThumbnail(getFile(index));
    }

    @Override
    protected synchronized Image getScaledThumbnail(int index, int thumbnailWidth) {
        return thumbCache.getScaledThumbnail(getFile(index), thumbnailWidth);
    }

    @Override
    protected void prefetch(int low, int high, boolean xmp) {
        File file;
        for (int i = low; i <= high; i++) {
            file = getFile(i);
            assert file != null;
            thumbCache.prefetch(file);
            if (xmp) {
                xmpCache.prefetch(file);
            }
        }
    }

    @Override
    public void appWillExit() {
        UserSettings.INSTANCE.getSettings().setInt(
                getThumbnailWidth(), KEY_THUMBNAIL_WIDTH);
        UserSettings.INSTANCE.writeToFile();
    }

    private void readProperties() {
        int tnWidth = UserSettings.INSTANCE.getSettings().getInt(
                KEY_THUMBNAIL_WIDTH);
        if (tnWidth > 0) {
            setThumbnailWidth(tnWidth);
        }
    }
}
