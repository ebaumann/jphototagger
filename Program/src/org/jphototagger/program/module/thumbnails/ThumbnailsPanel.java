package org.jphototagger.program.module.thumbnails;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.applifecycle.AppWillExitEvent;
import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.concurrent.CancelableComparator;
import org.jphototagger.api.file.event.DirectoryRenamedEvent;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressHandle;
import org.jphototagger.api.progress.ProgressHandleFactory;
import org.jphototagger.domain.event.listener.ThumbnailUpdateListener;
import org.jphototagger.domain.filefilter.UserDefinedFileFilter;
import org.jphototagger.domain.filefilter.UserDefinedFileFilter.RegexFileFilter;
import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;
import org.jphototagger.domain.repository.event.imagefiles.ImageFileDeletedEvent;
import org.jphototagger.domain.repository.event.imagefiles.ImageFileInsertedEvent;
import org.jphototagger.domain.repository.event.imagefiles.ImageFileMovedEvent;
import org.jphototagger.domain.repository.event.userdefinedfilefilters.UserDefinedFileFilterUpdatedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.domain.thumbnails.MainWindowThumbnailsComponent;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.ThumbnailFlag;
import org.jphototagger.domain.thumbnails.ThumbnailsPanelSettings;
import org.jphototagger.domain.thumbnails.event.ThumbnailsChangedEvent;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.domain.thumbnails.event.TypedThumbnailUpdateEvent;
import org.jphototagger.lib.api.PositionProviderAscendingComparator;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.comparator.FileSort;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.swing.MouseEventUtil;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.MathUtil;
import org.jphototagger.lib.util.ObjectUtil;
import org.jphototagger.program.filefilter.AppFileFilters;
import org.jphototagger.program.module.thumbnails.cache.RenderedThumbnailCache;
import org.jphototagger.program.settings.AppPreferencesKeys;
import org.jphototagger.program.tasks.ReplaceableThread;
import org.jphototagger.program.types.ByteSizeUnit;
import org.jphototagger.program.types.FileAction;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann, Tobias Stening
 */
public class ThumbnailsPanel extends PanelExt
        implements ComponentListener, MouseListener, MouseMotionListener, KeyListener, ThumbnailUpdateListener {

    private static final String KEY_THUMBNAIL_WIDTH = "ThumbnailsPanel.ThumbnailWidth";
    private static final String KEY_SHOW_METADATA_OVERLAY = "UserSettings.ShowMetadataOverlay";
    private static final long serialVersionUID = 1L;
    private static final int MARGIN_THUMBNAIL = 3;
    public static final Color COLOR_FOREGROUND_PANEL = Color.WHITE;
    public static final Color COLOR_BACKGROUND_PANEL = new Color(32, 32, 32);
    private static final DateFormat TOOLTIP_FILE_DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
    private static final ReplaceableThread SCHEDULER = new ReplaceableThread();
    private int isClickInSelection = -1;
    private final Map<Integer, Set<ThumbnailFlag>> thumbnailFlagsAtIndex = new HashMap<>();
    private final List<Integer> selectedThumbnailIndices = new ArrayList<>();
    private int thumbnailCountPerRow = 0;
    private boolean dragThumbnailsEnabled = false;
    private boolean isDisplayThumbnailTooltip = getPersistedDisplayThumbnailTooltip();
    private boolean transferDataOfDraggedThumbnails = false;
    private final ThumbnailPanelRenderer renderer = new ThumbnailPanelRenderer(this);
    private final transient RenderedThumbnailCache renderedThumbnailCache = RenderedThumbnailCache.INSTANCE;
    private final ThumbnailsPopupMenu popupMenu = ThumbnailsPopupMenu.INSTANCE;
    private Comparator<File> fileSortComparator = FileSort.NAMES_ASCENDING.getComparator();
    private FileFilter fileFilter = AppFileFilters.INSTANCE.getAllAcceptedImageFilesFilter();
    private final List<File> files = Collections.synchronizedList(new ArrayList<File>());
    private FileAction fileAction = FileAction.UNDEFINED;
    private OriginOfDisplayedThumbnails originOfOfDisplayedThumbnails = OriginOfDisplayedThumbnails.UNDEFINED_ORIGIN;
    private final transient ThumbnailDoubleklickController ctrlDoubleklick;
    private boolean drag;
    private boolean metaDataOverlay = readPersistedMetaDataOverlay();
    private volatile boolean notifySelChanged;
    private volatile boolean publishesChangedEvent;
    private volatile boolean notifyRefresh;
    private JViewport viewport;
    private static final Logger LOGGER = Logger.getLogger(ThumbnailsPanel.class.getName());
    private final Object thumbnailsChangedNotifyMonitor = new Object();
    private final XmpSidecarFileResolver xmpSidecarFileResolver = Lookup.getDefault().lookup(XmpSidecarFileResolver.class);
    private final Set<ThumbnailFlag> flagsToDisplay = EnumSet.of(ThumbnailFlag.ERROR_FILE_NOT_FOUND);
    private Popup messagePopup;
    private Object messagePopupOwner;
    private int cursorPos = -1;

    public ThumbnailsPanel() {
        ctrlDoubleklick = new ThumbnailDoubleklickController(this);
        init();
    }

    private void init() {
        setDragEnabled(true);
        setTransferHandler(new ThumbnailsPanelTransferHandler());
        readProperties();
        renderedThumbnailCache.setRenderer(renderer);
        setBackground(COLOR_BACKGROUND_PANEL);
        listen();
    }

    private boolean getPersistedDisplayThumbnailTooltip() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs == null || !prefs.containsKey(AppPreferencesKeys.KEY_UI_DISPLAY_THUMBNAIL_TOOLTIP)
                ? true
                : prefs.getBoolean(AppPreferencesKeys.KEY_UI_DISPLAY_THUMBNAIL_TOOLTIP);
    }

    private void listen() {
        renderedThumbnailCache.addThumbnailUpdateListener(this);
        AnnotationProcessor.process(this);
        addComponentListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        try {
            getDropTarget().addDropTargetListener(renderer);
        } catch (TooManyListenersException ex) {
            Logger.getLogger(ThumbnailsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clearSelectionAndFlags() {
        clearSelection();
        thumbnailFlagsAtIndex.clear();
    }

    /**
     *
     * @param index valid index
     * @throws IllegalArgumentException if index is not vaid
     */
    public synchronized void rerender(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Illegal index: " + index);
        }
        File file = getFileAtIndex(index);
        if (file == null) {
            throw new IllegalArgumentException("Illegal index: " + index);
        }
        renderedThumbnailCache.rerender(file);
    }

    private synchronized void rerender(Collection<Integer> rerenderTargets) {
        // some heuristic, for larger rerender requests clear the cache and
        // recreate stuff lazily
        if (rerenderTargets.size() > 700) {
            renderedThumbnailCache.clear();
            repaint();
        } else {
            for (Integer i : rerenderTargets) {
                if (isIndex(i)) {
                    rerender(i);
                }
            }
        }
    }

    private synchronized void convertSelection(List<File> oldFiles, List<File> newFiles) {
        List<Integer> newSelection = new ArrayList<>();
        for (int i : selectedThumbnailIndices) {
            File file;
            if (oldFiles.size() >= i) {
                continue;
            }
            file = oldFiles.get(i);
            int newI = newFiles.indexOf(file);
            if (newI < 0) {
                continue;
            }
            newSelection.add(newI);
        }
        selectedThumbnailIndices.clear();
        selectedThumbnailIndices.addAll(newSelection);
    }

    public synchronized void clearSelection() {
        clearSelectionAtIndices(new ArrayList<>(selectedThumbnailIndices));
    }

    public synchronized void selectAll() {
        setSelectedAll(true);
    }

    private void clearSelectionAtIndices(List<Integer> indices) {
        if (!indices.isEmpty()) {
            synchronized (this) {
                selectedThumbnailIndices.clear();
                rerender(indices);
            }
            notifySelectionChanged();
        }
    }

    private synchronized int getFirstSelectedIndex() {
        if (selectedThumbnailIndices.size() > 0) {
            return selectedThumbnailIndices.get(0);
        }
        return -1;
    }

    private synchronized int getSelectedIndex() {
        if (selectedThumbnailIndices.size() == 1) {
            return selectedThumbnailIndices.get(0);
        }
        return -1;
    }

    public synchronized void setThumbnailWidth(int widthInPixels) {
        if (widthInPixels != renderer.getThumbnailWidth()) {
            float oldPosition = getRelativeScrollPosition();
            renderer.setThumbnailWidth(widthInPixels);
            setCountPerRow();
            setSize(getWidth(), getCalculatedHeight());
            setRelativeScrollPosition(oldPosition);
            repaint();
        }
    }

    private float getRelativeScrollPosition() {
        if (viewport != null) {
            int middle = viewport.getViewRect().y + viewport.getExtentSize().height / 2;
            return (float) middle / (float) getHeight();
        }
        return (float) 0.0;
    }

    private void setRelativeScrollPosition(float p) {
        if (viewport != null) {
            int newY = ((int) (p * getHeight())) - viewport.getExtentSize().height / 2;
            validateScrollPane();
            viewport.setViewPosition(new Point(0, Math.max(0, newY)));
        }
    }

    /**
     * Enables the Drag gesture whithin the thumbnails panel. Whitout calling this,
     * {@code #handleMouseDragged(java.awt.event.MouseEvent)} will never called.
     *
     * @param enabled true if enabled. Default: false
     */
    private synchronized void setDragEnabled(boolean enabled) {
        dragThumbnailsEnabled = enabled;
    }

    private synchronized int getThumbnailWidth() {
        return renderer.getThumbnailWidth();
    }

    public synchronized List<Integer> getSelectedIndices() {
        return new ArrayList<>(selectedThumbnailIndices);
    }

    private synchronized void repaintAtIndex(int index) {
        repaint(getTopLeftOfTnIndex(index).x, getTopLeftOfTnIndex(index).y, renderer.getThumbnailAreaWidth(),
                renderer.getThumbnailAreaHeight());
    }

    private synchronized void repaintAtIndices(Collection<Integer> indices) {
        if (indices == null) {
            throw new NullPointerException("indices == null");
        }
        for (int index : indices) {
            repaintAtIndex(index);
        }
    }

    @Override
    public synchronized void thumbnailUpdated(final TypedThumbnailUpdateEvent event) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                int index = getIndexOf(event.getSource());
                if (index >= 0) {
                    repaintAtIndex(index);
                }
            }
        });
    }

    synchronized void setDisplayFlag(ThumbnailFlag flag, boolean display) {
        if (flag == ThumbnailFlag.ERROR_FILE_NOT_FOUND) { // Not existing files will always be flagged
            return;
        }
        if (display) {
            flagsToDisplay.add(flag);

        } else {
            flagsToDisplay.remove(flag);
        }
        setFlags();
        rerenderAll();
    }

    synchronized boolean isDisplayFlag(ThumbnailFlag flag) {
        return flagsToDisplay.contains(flag);
    }

    private synchronized void setFlags() {
        int count = files.size();
        thumbnailFlagsAtIndex.clear();
        for (int i = 0; i < count; i++) {
            for (ThumbnailFlag flag : flagsToDisplay) {
                if (flag.matches(files.get(i))) {
                    addFlag(i, flag);
                }
            }
        }
    }

    private synchronized void rerenderAll() {
        int filecount = files.size();
        for (int i = 0; i < filecount; i++) {
            rerender(i);
        }
    }

    private synchronized void addFlag(int index, ThumbnailFlag flag) {
        Set<ThumbnailFlag> flags = thumbnailFlagsAtIndex.get(index);
        if (flags == null) {
            flags = EnumSet.noneOf(ThumbnailFlag.class);
            thumbnailFlagsAtIndex.put(index, flags);
        }
        flags.add(flag);
    }

    private synchronized Set<ThumbnailFlag> getFlagsAtIndex(int index) {
        return thumbnailFlagsAtIndex.get(index);
    }

    /**
     * @param file
     * @return modifiable copy, empty if this file has no flags
     */
    public synchronized List<ThumbnailFlag> getFlagsOfFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        Set<ThumbnailFlag> flags = thumbnailFlagsAtIndex.get(getIndexOf(file));
        return flags == null
                ? new ArrayList<ThumbnailFlag>(0)
                : new ArrayList<>(flags);
    }

    private int getCountHorizontalLeftFromX(int x) {
        return x / (renderer.getThumbnailAreaWidth() + MARGIN_THUMBNAIL);
    }

    private int getCountHorizontalRightFromX(int x) {
        return getCountHorizontalLeftFromX(x - 1);
    }

    private int getCountVerticalAboveY(int y) {
        return y / (renderer.getThumbnailAreaHeight() + MARGIN_THUMBNAIL);
    }

    private boolean isThumbnailAreaInWidth(int x) {
        int startExtPadding = getCountHorizontalLeftFromX(x) * (renderer.getThumbnailAreaWidth() + MARGIN_THUMBNAIL);
        int endExtPadding = startExtPadding + MARGIN_THUMBNAIL;
        return (x < startExtPadding)
                || ((x > endExtPadding)
                && (endExtPadding + renderer.getThumbnailAreaWidth() + MARGIN_THUMBNAIL <= getWidth()));
    }

    private boolean isThumbnailAreaInHeight(int y) {
        int startExtPadding = getCountVerticalAboveY(y) * (renderer.getThumbnailAreaHeight() + MARGIN_THUMBNAIL);
        int endExtPadding = startExtPadding + MARGIN_THUMBNAIL;
        return (y < startExtPadding) || (y > endExtPadding);
    }

    private boolean isThumbnailArea(int x, int y) {
        return isThumbnailAreaInWidth(x) && isThumbnailAreaInHeight(y);
    }

    public int getThumbnailIndexAtPoint(int x, int y) {
        if (isThumbnailArea(x, y)) {
            int tnOffset = (x - MARGIN_THUMBNAIL) / (renderer.getThumbnailAreaWidth() + MARGIN_THUMBNAIL);
            int firstInRow = getFirstPaintIndexAtHeight(y);
            return firstInRow + tnOffset;
        }
        return -1;
    }

    private synchronized int getFirstPaintIndexAtHeight(int height) {
        int rowsToStart = getRowCountInHeight(height);
        return rowsToStart * thumbnailCountPerRow;
    }

    private synchronized int getLastPaintIndexAtHeight(int height) {
        int rowsToEnd = getRowCountInHeight(height);
        return (rowsToEnd + 1) * thumbnailCountPerRow;
    }

    private int getRowCountInHeight(int height) {
        return (height - MARGIN_THUMBNAIL) / (renderer.getThumbnailAreaHeight() + MARGIN_THUMBNAIL);
    }

    public synchronized boolean isSelectedAtIndex(int index) {
        return selectedThumbnailIndices.contains(index);
    }

    public synchronized boolean isFileSelected(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        return selectedThumbnailIndices.contains(getIndexOf(file));
    }

    public synchronized int getSelectionCount() {
        return selectedThumbnailIndices.size();
    }

    public synchronized boolean isAFileSelected() {
        return !selectedThumbnailIndices.isEmpty();
    }

    public Point getTopLeftOfTnIndex(int index) {
        int rowIndex = getRowIndexAt(index);
        int columnIndex = getColumnIndexAt(index);
        int x = MARGIN_THUMBNAIL + columnIndex * (renderer.getThumbnailAreaWidth() + MARGIN_THUMBNAIL);
        int y = MARGIN_THUMBNAIL + rowIndex * (renderer.getThumbnailAreaHeight() + MARGIN_THUMBNAIL);
        return new Point(x, y);
    }

    public synchronized void setDrag(boolean drag) {
        this.drag = drag;
    }

    /**
     * Returns the drop index when images moved within the panel itself.
     * <p>
     * This is different to the drop index when other content will be dropped, e.g. metadata.
     *
     * @param x x coordinate of current move position
     * @param y y coordinate of current move position
     * @return index or -1 if no valid index is near the move position
     */
    public synchronized int getImageMoveDropIndex(int x, int y) {
        int row = Math.max(0, (y - MARGIN_THUMBNAIL) / (renderer.getThumbnailAreaHeight() + MARGIN_THUMBNAIL));
        int col = Math.max(0, Math.min(getColumnCount(), (x - MARGIN_THUMBNAIL) / (renderer.getThumbnailAreaWidth() + MARGIN_THUMBNAIL)));
        if ((row < 0) || (col < 0)) {
            return -1;
        }
        int index = Math.min(row * getColumnCount() + col, files.size() - 1);
        return index;
    }

    private synchronized int getColumnCount() {
        int width = getWidth();
        int tnWidth = renderer.getThumbnailAreaWidth();
        int count = (int) ((double) (width - MARGIN_THUMBNAIL) / (double) tnWidth);
        return (count > files.size())
                ? files.size()
                : count;
    }

    private void transferData(MouseEvent evt) {
        if (dragThumbnailsEnabled && isAFileSelected()) {
            TransferHandler transferHandler = getTransferHandler();
            if (transferHandler != null) {
                transferHandler.exportAsDrag(this, evt, TransferHandler.COPY);
            }
        }
    }

    private synchronized boolean isClickInSelection(MouseEvent evt) {
        int clickIndex = getThumbnailIndexAtPoint(evt.getX(), evt.getY());
        return selectedThumbnailIndices.contains(clickIndex);
    }

    private synchronized void handleMousePressed(MouseEvent evt) {
        boolean isLeftClick = MouseEventUtil.isLeftClick(evt);
        if (isLeftClick && !hasFocus()) {
            requestFocus();
        }
        if (isLeftClick) {
            int thumbnailIndex = getThumbnailIndexAtPoint(evt.getX(), evt.getY());
            if (isIndex(thumbnailIndex)) {
                transferDataOfDraggedThumbnails = true;
                if (MouseEventUtil.isDoubleClick(evt)) {
                    doubleClickAt(thumbnailIndex);
                    setSelectedAtIndex(thumbnailIndex);
                } else if (evt.isControlDown()) {
                    if (!isSelectedAtIndex(thumbnailIndex)) {
                        addIndexToSelection(thumbnailIndex);
                    } else {
                        removeIndexFromSelection(thumbnailIndex);
                    }
                } else if (evt.isShiftDown()) {
                    enhanceSelectionTo(thumbnailIndex);
                } else {
                    if (isClickInSelection(evt)) {
                        isClickInSelection = thumbnailIndex;
                    } else {
                        setSelectedAtIndex(thumbnailIndex);
                    }
                }
            } else {
                setSelectedAll(false);
            }
        } else if (MouseEventUtil.isPopupTrigger(evt)) {
            handlePopupTrigger(evt);
        }
    }

    private synchronized void handlePopupTrigger(MouseEvent evt) {
        int clickIndex = getThumbnailIndexAtPoint(evt.getX(), evt.getY());
        boolean isClickInSel = selectedThumbnailIndices.contains(clickIndex);
        if (!isClickInSel) {
            if (isIndex(clickIndex)) {
                setSelectedAtIndex(clickIndex);
            } else {
                clearSelection();
            }
        }
        showPopupMenu(evt);
    }

    private synchronized void handleMouseReleased() {
        if (isClickInSelection >= 0) {
            setSelectedAtIndex(isClickInSelection);
            isClickInSelection = -1;
        }
    }

    private synchronized void handleMouseDragged(MouseEvent evt) {
        isClickInSelection = -1;
        if (dragThumbnailsEnabled && transferDataOfDraggedThumbnails && isAFileSelected()) {
            transferData(evt);
            transferDataOfDraggedThumbnails = false;
        }
    }

    private void handleMouseMoved(MouseEvent evt) {
        if (dragThumbnailsEnabled) {
            setCursor(Cursor.getDefaultCursor());
        }
        showToolTip(evt);
    }

    private void setSelectedAll(boolean select) {
        synchronized (this) {
            Set<Integer> currentSelection = new HashSet<>(selectedThumbnailIndices);
            selectedThumbnailIndices.clear();
            if (select) {
                for (int index = 0; index < files.size(); index++) {
                    selectedThumbnailIndices.add(index);
                }
                renderedThumbnailCache.clear();
                repaint();
            } else {
                // Clear the selection highlighting
                rerender(currentSelection);
            }
        }
        notifySelectionChanged();
    }

    private void enhanceSelectionTo(int index) {
        boolean isEnhance;
        synchronized (this) {
            isEnhance = isAFileSelected();
            if (isEnhance) {
                Set<Integer> rerenderTargets = new HashSet<>(selectedThumbnailIndices);
                int firstSelected = getFirstSelectedIndex();
                selectedThumbnailIndices.clear();
                int startIndex = (index > firstSelected)
                        ? firstSelected
                        : index;
                int endIndex = (index > firstSelected)
                        ? index
                        : firstSelected;
                for (int i = startIndex; i <= endIndex; i++) {
                    selectedThumbnailIndices.add(i);
                }
                rerenderTargets.addAll(selectedThumbnailIndices);
                rerender(rerenderTargets);
                repaint();
            } else {
                setSelectedAtIndex(index);
            }
        }
        if (isEnhance) {
            notifySelectionChanged();
        }
    }

    public void setSelectedIndices(List<Integer> indices) {
        if (indices == null) {
            throw new NullPointerException("indices == null");
        }
        synchronized (this) {
            Set<Integer> rerenderTargets = getValidIndicesOf(indices);
            selectedThumbnailIndices.clear();
            selectedThumbnailIndices.addAll(indices);
            if (selectedThumbnailIndices.size() > 0) {
                Collections.sort(selectedThumbnailIndices);
            }
            rerender(rerenderTargets);
        }
        notifySelectionChanged();
        repaint();
    }

    public synchronized void setSelectedFiles(Collection<? extends File> files) {
        List<Integer> indices = new ArrayList<>(files.size());
        for (File file : files) {
            int index = getIndexOf(file);
            if (index >= 0) {
                indices.add(index);
            }
        }
        setSelectedIndices(indices);
    }

    private synchronized Set<Integer> getValidIndicesOf(Collection<Integer> indices) {
        Set<Integer> validIndices = new HashSet<>(indices.size());
        if (indices.isEmpty() || files.isEmpty()) {
            return validIndices;
        }
        int maxIndex = files.size() - 1;
        for (int index : indices) {
            if ((index >= 0) && (index <= maxIndex)) {
                validIndices.add(index);
            }
        }
        return validIndices;
    }

    private void setSelectedAtIndex(int index) {
        synchronized (this) {
            if (!isIndex(index)) {
                assert false : "Invalid index: " + index;

                return;
            }
            Set<Integer> rerenderTargets = new HashSet<>(selectedThumbnailIndices);
            selectedThumbnailIndices.clear();
            selectedThumbnailIndices.add(index);
            rerenderTargets.add(index);
            rerender(rerenderTargets);
        }
        notifySelectionChanged();
        repaint();
    }

    private void addIndexToSelection(int index) {
        boolean isSelect;
        synchronized (this) {
            isSelect = !isSelectedAtIndex(index);
            if (isSelect) {
                selectedThumbnailIndices.add(index);
                Collections.sort(selectedThumbnailIndices);
                rerender(index);
            }
        }
        if (isSelect) {
            notifySelectionChanged();
        }
    }

    private void removeIndexFromSelection(int index) {
        boolean isRemove;
        synchronized (this) {
            isRemove = isSelectedAtIndex(index);
            if (isRemove) {
                // Do NOT call removeFiles(int)!
                selectedThumbnailIndices.remove(Integer.valueOf(index));
                rerender(index);
            }
        }
        if (isRemove) {
            notifySelectionChanged();
        }
    }

    private synchronized int getColumnIndexAt(int thumbnailIndex) {
        return ((thumbnailIndex > 0) && (thumbnailCountPerRow > 0))
                ? thumbnailIndex % thumbnailCountPerRow
                : 0;
    }

    private synchronized int getRowIndexAt(int thumbnailIndex) {
        return (thumbnailCountPerRow > 0)
                ? thumbnailIndex / thumbnailCountPerRow
                : 0;
    }

    private synchronized void handleMouseDoubleKlicked() {
        int indexSelectedThumbnail = getSelectedIndex();
        if (indexSelectedThumbnail >= 0) {
            doubleClickAt(indexSelectedThumbnail);
        }
    }

    private void forceRepaint() {
        revalidate();
        repaint();
    }

    private synchronized void prefetch(int low, int high, boolean xmp) {
        if (!isIndex(low)) {
            throw new IllegalArgumentException("Illegal low index: " + low);
        }
        if (!isIndex(high)) {
            throw new IllegalArgumentException("Illegal high index: " + high);
        }
        File file;
        for (int i = low; i <= high; i++) {
            file = getFileAtIndex(i);
            assert file != null : "X: " + i + ", " + low + ", " + high + ".";
            renderedThumbnailCache.prefetch(file, renderer.getThumbnailWidth(), xmp);
        }
    }

    @Override
    public synchronized void paintComponent(Graphics g) {
        paintPanelBackground(g);
        if (files.size() > 0) {
            Rectangle rectClip = g.getClipBounds();
            int firstIndex = Math.min(files.size(), getFirstPaintIndexAtHeight(rectClip.y));
            int lastIndex = Math.min(getLastPaintIndexAtHeight(rectClip.y + rectClip.height), files.size());
            int firstColumn = Math.max(0, getCountHorizontalLeftFromX(rectClip.x));
            int lastColumn = Math.min(thumbnailCountPerRow - 1, getCountHorizontalRightFromX(rectClip.x + rectClip.width));
            for (int index = firstIndex; index < lastIndex; index++) {
                if ((index % thumbnailCountPerRow >= firstColumn) && (index % thumbnailCountPerRow <= lastColumn)) {
                    paintThumbnail(index, g);
                }
            }
            paintPanelFocusBorder(g);
            int prefetchLowStart = Math.max(0, firstIndex - thumbnailCountPerRow * 5);
            int prefetchLowEnd = firstIndex - 1;
            int prefetchHighStart = lastIndex;
            int prefetchHighEnd = Math.min(files.size() - 1, lastIndex + thumbnailCountPerRow * 5);
            if (isIndex(prefetchHighStart) && isIndex(prefetchHighEnd)) {
                prefetch(prefetchHighStart, prefetchHighEnd, isMetaDataOverlay());
            }
            if (isIndex(prefetchLowStart) && isIndex(prefetchLowEnd)) {
                prefetch(prefetchLowStart, prefetchLowEnd, isMetaDataOverlay());
            }
        }
        if (drag) {
            renderer.paintImgDropMarker(g);
        }
        renderer.paintCursor(g);
    }

    private void paintPanelBackground(Graphics g) {
        Color oldColor = g.getColor();
        g.setColor(COLOR_BACKGROUND_PANEL);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(oldColor);
    }

    private synchronized void paintThumbnail(int index, Graphics g) {
        Point topLeft = getTopLeftOfTnIndex(index);
        Image im = renderedThumbnailCache.getThumbnail(getFileAtIndex(index), renderer.getThumbnailWidth(), isMetaDataOverlay());
        if (im != null) {
            g.drawImage(im, topLeft.x, topLeft.y, viewport);
        }
    }

    @EventSubscriber(eventClass = AppWillExitEvent.class)
    public void appWillExit(AppWillExitEvent evt) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.setInt(ThumbnailsPanel.KEY_THUMBNAIL_WIDTH, getThumbnailWidth());
    }

    private String createTooltipTextForIndex(int index) {
        if (isIndex(index)) {
            File file = files.get(index);
            boolean fileExists = file.exists();
            Set<ThumbnailFlag> flags = getFlagsAtIndex(index);
            String flagText = flags == null
                    ? ""
                    : createFlagsDisplayName(new ArrayList<>(flags));
            long length = fileExists ? file.length() : 0;
            ByteSizeUnit unit = fileExists ? ByteSizeUnit.unit(length) : ByteSizeUnit.BYTE;
            long unitLength = fileExists ? length / unit.bytes() : 0;
            String unitString = unit.toString();
            String fileDate = file.exists() ? TOOLTIP_FILE_DATE_FORMAT.format(new Date(file.lastModified())) : "?";
            return Bundle.getString(ThumbnailsPanel.class, "ThumbnailsPanel.TooltipText",
                    file.getAbsolutePath(),
                    unitLength,
                    unitString,
                    fileDate,
                    getSidecarPathNameOfFile(file),
                    flagText);
        } else {
            return "";
        }
    }

    private String createFlagsDisplayName(List<ThumbnailFlag> flags) {
        Collections.sort(flags, PositionProviderAscendingComparator.INSTANCE);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < flags.size(); i++) {
            sb.append(i == 0 ? "" : ',')
                    .append(flags.get(i).getDisplayName());
        }
        return sb.toString();
    }

    private String getSidecarPathNameOfFile(File file) {
        File sidecarFile = xmpSidecarFileResolver.getXmpSidecarFileOrNullIfNotExists(file);
        return (sidecarFile == null)
                ? "-"
                : sidecarFile.getAbsolutePath();
    }

    public synchronized OriginOfDisplayedThumbnails getOriginOfDisplayedThumbnails() {
        return originOfOfDisplayedThumbnails;
    }

    public synchronized FileAction getFileAction() {
        return fileAction;
    }

    public synchronized int getFileCount() {
        return files.size();
    }

    public synchronized boolean hasFiles() {
        return !files.isEmpty();
    }

    public synchronized List<File> getFiles() {
        return Collections.unmodifiableList(files);
    }

    private synchronized List<File> getFilesAtIndices(List<Integer> indices) {
        List<File> f = new ArrayList<>();
        for (Integer index : indices) {
            if (isIndex(index)) {
                f.add(files.get(index));
            }
        }
        return f;
    }

    private synchronized List<Integer> getIndicesOfFiles(List<File> files, boolean onlyIfExists) {
        List<Integer> indices = new ArrayList<>(files.size());
        for (File file : files) {
            int index = files.indexOf(file);
            if (!onlyIfExists || (onlyIfExists && (index >= 0))) {
                indices.add(index);
            }
        }
        return indices;
    }

    public synchronized List<File> getSelectedFiles() {
        return getFilesAtIndices(getSelectedIndices());
    }

    public synchronized boolean isIndex(int index) {
        return (index >= 0) && (index < files.size());
    }

    public synchronized void moveSelectedToIndex(int index) {
        if (!isIndex(index)) {
            return;
        }
        List<Integer> selectedIndices = getSelectedIndices();
        if (selectedIndices.size() <= 0) {
            return;
        }
        Collections.sort(selectedIndices);
        if (selectedIndices.get(0) == index) {
            return;
        }
        List<File> selFiles = getFilesAtIndices(selectedIndices);
        List<File> filesWithoutMoved = new ArrayList<>(files);
        int fileCount = filesWithoutMoved.size();
        filesWithoutMoved.removeAll(selFiles);
        List<File> newOrderedFiles = new ArrayList<>(fileCount);
        newOrderedFiles.addAll(filesWithoutMoved.subList(0, index));
        newOrderedFiles.addAll(selFiles);
        newOrderedFiles.addAll(filesWithoutMoved.subList(index, filesWithoutMoved.size()));
        files.clear();
        files.addAll(newOrderedFiles);
        clearSelectionAtIndices(getIndicesOfFiles(selFiles, true));
        repaint();
    }

    private void notifyRefreshListeners(ThumbnailsPanelRefreshEvent evt) {
        if (!notifyRefresh) {
            notifyRefresh = true;
            LOGGER.log(Level.INFO, "Refreshing thumbnails view");
            EventBus.publish(evt);
            notifyRefresh = false;
        }
    }

    private void readProperties() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs == null) {
            return;
        }
        int tnWidth = prefs.getInt(ThumbnailsPanel.KEY_THUMBNAIL_WIDTH);
        if (tnWidth > 0) {
            setThumbnailWidth(tnWidth);
        }
    }

    public void refresh() {
        ThumbnailsPanelRefreshEvent evt = new ThumbnailsPanelRefreshEvent(
                this, originOfOfDisplayedThumbnails, getViewPosition());
        evt.setSelectedThumbnailIndices(new ArrayList<>(selectedThumbnailIndices));
        notifyRefreshListeners(evt);
        // viewport position has to be setTree by the refresh listeners because they
        // usually setTree new files in a *thread* so that setting the viewport has
        // no effect
    }

    /**
     * @param settings or null
     */
    public void applyThumbnailsPanelSettings(ThumbnailsPanelSettings settings) {
        if (settings == null) {
            return;
        }
        setViewPosition(settings.getViewPosition());
        if (settings.hasSelectedFiles()) {
            setSelectedFiles(settings.getSelectedFiles());
        } else if (settings.hasSelectedIndices()) {
            setSelectedIndices(settings.getSelectedIndices());
        }
    }

    private void setViewPosition(Point pos) {
        if (viewport != null) {
            validateScrollPane();
            viewport.setViewPosition(pos);
        }
    }

    private void validateScrollPane() {
        MainWindowThumbnailsComponent component = Lookup.getDefault().lookup(MainWindowThumbnailsComponent.class);
        component.validateViewportPosition();
    }

    public Point getViewPosition() {
        JViewport vp = getViewport();
        return vp == null
                ? new Point(0, 0)
                : vp.getViewPosition();
    }

    public synchronized boolean containsFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        return files.contains(file);
    }

    public synchronized void removeFiles(Collection<? extends File> filesToRemove) {
        if (filesToRemove == null) {
            throw new NullPointerException("filesToRemove == null");
        }
        List<File> oldFiles = new ArrayList<>(files);
        List<Integer> selIndicesToRemove = getSelectedIndicesOfFiles(filesToRemove);
        if (files.removeAll(filesToRemove)) {
            selectedThumbnailIndices.removeAll(selIndicesToRemove);
            convertSelection(oldFiles, files);
            Point viewPos = getViewPosition();
            setFiles(files, originOfOfDisplayedThumbnails);
            setViewPosition(viewPos);
            renderedThumbnailCache.remove(filesToRemove);
            refresh();
        }
    }

    private synchronized List<Integer> getSelectedIndicesOfFiles(Collection<? extends File> files) {
        List<Integer> selIndices = new ArrayList<>(files.size());
        for (File file : files) {
            int index = getIndexOf(file);
            if (selectedThumbnailIndices.contains(index)) {
                selIndices.add(index);
            }
        }
        return selIndices;
    }

    /**
     * Renames a file <strong>on the display</strong>, <em>not</em> in the file system.
     *
     * @param fromFile old file
     * @param toFile new file
     */
    public synchronized void renameFile(File fromFile, File toFile) {
        if (fromFile == null) {
            throw new NullPointerException("oldFile == null");
        }
        if (toFile == null) {
            throw new NullPointerException("newFile == null");
        }
        int index = files.indexOf(fromFile);
        if (index >= 0) {
            files.set(index, toFile);
        }
    }

    public synchronized void repaintFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        int index = getIndexOf(file);
        if (index > 0) {
            repaintAtIndices(Collections.singleton(index));
        }
    }

    public synchronized void setFileAction(FileAction fileAction) {
        if (fileAction == null) {
            throw new NullPointerException("fileAction == null");
        }
        this.fileAction = fileAction;
    }

    public synchronized void setFiles(Collection<? extends File> files, OriginOfDisplayedThumbnails originOfOfDisplayedThumbnails) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }
        if (originOfOfDisplayedThumbnails == null) {
            throw new NullPointerException("originOfOfDisplayedThumbnails == null");
        }
        synchronized (this) {
            Logger.getLogger(ThumbnailsPanel.class.getName()).log(Level.FINE, "{0} new files to display", files.size());
            SCHEDULER.setTask(new FileSetter(files, fileSortComparator, originOfOfDisplayedThumbnails));
        }
    }

    private final class FileSetter implements Runnable, Cancelable {

        private volatile boolean cancel;
        private volatile boolean sorting;
        private final OriginOfDisplayedThumbnails origin;
        private final Collection<? extends File> files;
        private final CancelableComparator<File> sortCmp;
        private final ProgressHandle progressHandle;

        private FileSetter(Collection<? extends File> files, Comparator<File> sortCmp, OriginOfDisplayedThumbnails origin) {
            this.files = files;
            this.sortCmp = new CancelableComparator<>(sortCmp);
            this.origin = origin;
            this.progressHandle = Lookup.getDefault().lookup(ProgressHandleFactory.class).createProgressHandle();
        }

        @Override
        public void run() {
            progressStarted();
            try {
                List<File> filteredFiles = origin.isFilterable()
                        ? FileUtil.filterFiles(files, fileFilter)
                        : new ArrayList<File>(files);
                if (!cancel && origin.isSortable()) {
                    sorting = true;
                    Collections.sort(filteredFiles, sortCmp);
                    sorting = false;
                }
                if (!cancel) {
                    setFiles(filteredFiles);
                }
            } finally {
                progressEnded();
            }
        }

        private void setFiles(final List<File> files) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {
                @Override
                public void run() {
                    ThumbnailsPanel.this.originOfOfDisplayedThumbnails = origin;
                    clearSelectionAndFlags();
                    ThumbnailsPanel.this.files.clear();
                    ThumbnailsPanel.this.files.addAll(files);
                    scrollToTop();
                    setFlags();
                    notifyThumbnailsChanged();
                    forceRepaint();
                    new WarnOnEqualBasenamesTask(files).start();
                }
            });
        }

        @Override
        public synchronized void cancel() {
            progressEnded();
            if (sorting) {
                sortCmp.cancel();
            }
            cancel = true;
        }

        private void progressStarted() {
            String message = Bundle.getString(ThumbnailsPanel.class, "ThumbnailsPanel.SetFiles.ProgressStarted", files.size());
            ProgressEvent evt = new ProgressEvent.Builder()
                    .source(this)
                    .indeterminate(true)
                    .stringPainted(true)
                    .stringToPaint(message)
                    .build();
            progressHandle.progressStarted(evt);
            showMessagePopup(message, ThumbnailsPanel.this);
        }

        private void progressEnded() {
            if (!cancel) {
                progressHandle.progressEnded();
                hideMessagePopup(ThumbnailsPanel.this);
            }
        }
    }

    public synchronized boolean isEmpty() {
        return files.isEmpty();
    }

    synchronized void setFileSortComparator(Comparator<File> comparator) {
        if (comparator == null) {
            throw new NullPointerException("comparator == null");
        }
        boolean comparatorChanged = !ObjectUtil.equals(comparator, fileSortComparator);
        if (comparatorChanged) {
            LOGGER.log(Level.FINEST, "Changing sort order to {0}", fileSortComparator);
            fileSortComparator = comparator;
            if (originOfOfDisplayedThumbnails.isSortable() && !isEmpty()) {
                setFiles(getFiles(), originOfOfDisplayedThumbnails);
            }
        }
    }

    synchronized void setFileFilter(FileFilter filter) {
        if (filter == null) {
            throw new NullPointerException("filter == null");
        }
        boolean filterChanged = !ObjectUtil.equals(fileFilter, filter);
        if (filterChanged) {
            LOGGER.log(Level.FINEST, "Changing file filter to {0}", filter);
            fileFilter = filter;
            if (originOfOfDisplayedThumbnails.isFilterable()) {
                refresh(); // Refresh and not setFiles() because a filter may display more files, e.g. prev. TIFF, now ALL types
            }
        }
    }

    synchronized void sort() {
        if (files.isEmpty()) {
            return;
        }
        if (originOfOfDisplayedThumbnails.isSortable()) {
            List<File> selFiles = getSelectedFiles();

            setFiles(new ArrayList<>(files), originOfOfDisplayedThumbnails);
            setSelectedIndices(getIndicesOfFiles(selFiles, true));
        }
    }

    private void paintPanelFocusBorder(Graphics g) {
        if (hasFocus()) {
            g.setColor(Color.white);
            int width = getWidth();
            int height = getHeight();
            g.drawRect(1, 1, width - 2, height - 2);
        }
    }

    private synchronized int getRowCount() {
        double count = (double) files.size() / (double) thumbnailCountPerRow;
        return (files.size() > thumbnailCountPerRow)
                ? (int) (MathUtil.isInteger(count)
                        ? count
                        : count + 1)
                : (files.isEmpty())
                        ? 0
                        : 1;
    }

    private void notifySelectionChanged() {
        synchronized (thumbnailsChangedNotifyMonitor) {
            if (!notifySelChanged) {
                notifySelChanged = true;
                EventBus.publish(new ThumbnailsSelectionChangedEvent(this, getSelectedFiles(), selectedThumbnailIndices));
                notifySelChanged = false;
            }
        }
    }

    private void notifyThumbnailsChanged() {
        synchronized (thumbnailsChangedNotifyMonitor) {
            if (!publishesChangedEvent) {
                publishesChangedEvent = true;
                EventBus.publish(new ThumbnailsChangedEvent(this, originOfOfDisplayedThumbnails, files));
                publishesChangedEvent = false;
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Component parent = getParent();
        int width = (parent instanceof JViewport)
                ? parent.getWidth()
                : getWidth();
        int heigth = getCalculatedHeight();
        return new Dimension(width, heigth);
    }

    private int getCalculatedHeight() {
        return MARGIN_THUMBNAIL + getRowCount() * (renderer.getThumbnailAreaHeight() + MARGIN_THUMBNAIL);
    }

    @Override
    public void componentResized(ComponentEvent evt) {
        setCountPerRow();
        repaint();
    }

    private synchronized void setCountPerRow() {
        int width = getWidth();
        int tnAreaWidth = renderer.getThumbnailAreaWidth();
        double count = (double) (width - MARGIN_THUMBNAIL) / (double) (tnAreaWidth + MARGIN_THUMBNAIL);
        thumbnailCountPerRow = (count >= 1)
                ? (int) count
                : 1;
    }

    @Override
    public void componentMoved(ComponentEvent evt) {
    }

    @Override
    public void componentShown(ComponentEvent evt) {
    }

    @Override
    public void componentHidden(ComponentEvent evt) {
    }

    private synchronized void setSelectedUp() {
        int indexSelectedThumbnail = getSelectedIndex();
        int indexToSelect = indexSelectedThumbnail - thumbnailCountPerRow;
        if ((indexSelectedThumbnail >= 0) && isIndex(indexToSelect)) {
            setSelectedAtIndex(indexToSelect);
        }
    }

    private synchronized void setSelectedDown() {
        int indexSelectedThumbnail = getSelectedIndex();
        int indexToSelect = indexSelectedThumbnail + thumbnailCountPerRow;
        if ((indexSelectedThumbnail >= 0) && isIndex(indexToSelect)) {
            setSelectedAtIndex(indexToSelect);
        }
    }

    private synchronized void setSelectedNext() {
        int indexSelectedThumbnail = getSelectedIndex();
        int indexToSelect = (indexSelectedThumbnail + 1) % files.size();
        if ((indexSelectedThumbnail >= 0) && isIndex(indexToSelect)) {
            if (indexToSelect == 0) {
                scrollToTop();
            }
            setSelectedAtIndex(indexToSelect);
        }
    }

    private synchronized void setSelectedPrevious() {
        int indexSelectedThumbnail = getSelectedIndex();
        int indexToSelect = (indexSelectedThumbnail - 1) % files.size();
        if (indexToSelect < 0) {
            indexToSelect = files.size() - 1;
        }
        if ((indexSelectedThumbnail >= 0) && isIndex(indexToSelect)) {
            if (indexToSelect >= files.size() - 1) {
                scrollToBottom();
            }
            setSelectedAtIndex(indexToSelect);
        }
    }

    /**
     * Sets the viewport. Have to be called before adding files. If a viewport ist setTree, some additional functions
     * supported, e.g. special keyboard keys that are not handled through the viewport and a scroll pane.
     *
     * @param viewport Viewport
     */
    public synchronized void setViewport(JViewport viewport) {
        if (viewport == null) {
            throw new NullPointerException("viewport == null");
        }
        this.viewport = viewport;
    }

    private synchronized JViewport getViewport() {
        return viewport;
    }

    public int getCursorPos() {
        return cursorPos;
    }

    public void scrollToTop() {
        setViewPosition(new Point(0, 0));
    }

    private void scrollToBottom() {
        setViewPosition(new Point(0, getHeight()));
    }

    private void checkScrollUp() {
        if ((viewport != null) && (getSelectedIndex() >= 0)) {
            int tnHeight = renderer.getThumbnailAreaHeight();
            int topSel = getTopLeftOfTnIndex(getSelectedIndex()).y - tnHeight;
            int viewPosBottom = viewport.getViewPosition().y;
            if (topSel < viewPosBottom) {
                scrollOneImageUp();
            }
        }
    }

    private void checkScrollDown() {
        if ((viewport != null) && (getSelectedIndex() >= 0)) {
            int tnHeight = renderer.getThumbnailAreaHeight();
            int bottomSel = getTopLeftOfTnIndex(getSelectedIndex()).y + tnHeight;
            int viewPosBottom = viewport.getViewPosition().y + viewport.getHeight();
            if (bottomSel > viewPosBottom) {
                scrollOneImageDown();
            }
        }
    }

    private void scrollOneImageUp() {
        if (viewport != null) {
            Point p = viewport.getViewPosition();
            int tnHeight = renderer.getThumbnailAreaHeight();
            int y = (p.y - tnHeight >= 0)
                    ? p.y - tnHeight
                    : 0;
            validateScrollPane();
            viewport.setViewPosition(new Point(0, y));
        }
    }

    private void scrollOneImageDown() {
        if (viewport != null) {
            Point p = viewport.getViewPosition();
            validateScrollPane();
            viewport.setViewPosition(new Point(0, p.y + renderer.getThumbnailAreaHeight()));
        }
    }

    @Override
    public void mouseEntered(MouseEvent evt) {
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        handleMousePressed(evt);
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
        handleMouseReleased();
    }

    @Override
    public void mouseExited(MouseEvent evt) {
    }

    @Override
    public void mouseMoved(MouseEvent evt) {
        handleMouseMoved(evt);
    }

    @Override
    public void mouseDragged(MouseEvent evt) {
        handleMouseDragged(evt);
    }

    @Override
    public void keyTyped(KeyEvent evt) {
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        int keyCode = evt.getKeyCode();
        if (keyCode == KeyEvent.VK_F5) {
            refresh();
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            setSelectedNext();
            checkScrollDown();
        } else if (keyCode == KeyEvent.VK_LEFT) {
            setSelectedPrevious();
            checkScrollUp();
        } else if (keyCode == KeyEvent.VK_UP) {
            setSelectedUp();
            checkScrollUp();
        } else if (keyCode == KeyEvent.VK_DOWN) {
            setSelectedDown();
            checkScrollDown();
        } else if (keyCode == KeyEvent.VK_ENTER) {
            handleMouseDoubleKlicked();
        } else if ((evt.getModifiers() & KeyEvent.CTRL_MASK) == KeyEvent.CTRL_MASK && (keyCode == KeyEvent.VK_A)) {
            setSelectedAll(true);
        } else if (keyCode == KeyEvent.VK_HOME) {
            clearSelection();
            scrollToTop();
        } else if (keyCode == KeyEvent.VK_END) {
            clearSelection();
            scrollToBottom();
        } else if ((keyCode == KeyEvent.VK_PAGE_DOWN) || (keyCode == KeyEvent.VK_PAGE_UP)) {
            clearSelection();
        }
    }

    @Override
    public void keyReleased(KeyEvent evt) {
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    synchronized boolean isMetaDataOverlay() {
        return metaDataOverlay;
    }

    synchronized void setMetaDataOverlay(boolean metaDataOverlay) {
        this.metaDataOverlay = metaDataOverlay;
        persistMetaDataOverlay();
        // renderedThumbnailCache.rerenderAll(keywordsOverlay);
        repaint();
    }

    /**
     * @param file
     * @return -1 if not displayed
     */
    public synchronized int getIndexOf(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        return files.indexOf(file);
    }

    /**
     * @param index
     * @return null if the index is invalid
     */
    public synchronized File getFileAtIndex(int index) {
        return isIndex(index)
                ? files.get(index)
                : null;
    }

    private void doubleClickAt(int index) {
        ctrlDoubleklick.doubleClickAtIndex(index);
    }

    private void showPopupMenu(MouseEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }
        popupMenu.show(this, evt.getX(), evt.getY());
    }

    private void showToolTip(MouseEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }
        if (!isDisplayThumbnailTooltip) {
            setToolTipText(null);
            return;
        }
        int index = getThumbnailIndexAtPoint(evt.getX(), evt.getY());
        String tooltipText = createTooltipTextForIndex(index);
        setToolTipText(tooltipText);
    }

    @EventSubscriber(eventClass = UserDefinedFileFilterUpdatedEvent.class)
    public synchronized void userDefinedFilterUpdated(final UserDefinedFileFilterUpdatedEvent evt) {
        updateUserDefinedFileFilter(evt.getFilter());
    }

    private void updateUserDefinedFileFilter(UserDefinedFileFilter userDefinedFileFilter) {
        boolean fileFilterIsRegex = fileFilter instanceof UserDefinedFileFilter.RegexFileFilter;
        RegexFileFilter updatedFilter = userDefinedFileFilter.getFileFilter();
        if (fileFilterIsRegex
                && userDefinedFileFilter.filtersEquals(updatedFilter, (UserDefinedFileFilter.RegexFileFilter) fileFilter)) {
            fileFilter = userDefinedFileFilter.getFileFilter();
            refresh();
        }
    }

    private synchronized void removeFilesNotAcceptedByFileFilter(File file) {
        // Insertion can't be decided because we don't know whether the image
        // file is e.g. in a displayed directory or matches the criteria of a
        // search
        if (!fileFilter.accept(file) && files.contains(file)) {
            removeFiles(Collections.singleton(file));
        }
    }

    @EventSubscriber(eventClass = ImageFileInsertedEvent.class)
    public void imageFileInserted(ImageFileInsertedEvent evt) {
        removeFilesNotAcceptedByFileFilter(evt.getImageFile());
    }

    @EventSubscriber(eventClass = ImageFileMovedEvent.class)
    public void imageFileRenamed(ImageFileMovedEvent evt) {
        removeFilesNotAcceptedByFileFilter(evt.getOldImageFile());
        removeFilesNotAcceptedByFileFilter(evt.getNewImageFile());
    }

    @EventSubscriber(eventClass = ImageFileDeletedEvent.class)
    public void imageFileDeleted(ImageFileDeletedEvent evt) {
        removeFilesNotAcceptedByFileFilter(evt.getImageFile());
    }

    @EventSubscriber(eventClass = XmpInsertedEvent.class)
    public void xmpInserted(XmpInsertedEvent evt) {
        removeFilesNotAcceptedByFileFilter(evt.getImageFile());
    }

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public void xmpUpdated(XmpUpdatedEvent evt) {
        removeFilesNotAcceptedByFileFilter(evt.getImageFile());
    }

    @EventSubscriber(eventClass = XmpDeletedEvent.class)
    public void xmpDeleted(XmpDeletedEvent evt) {
        removeFilesNotAcceptedByFileFilter(evt.getImageFile());
    }

    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void preferencesChanged(PreferencesChangedEvent evt) {
        if (AppPreferencesKeys.KEY_UI_DISPLAY_THUMBNAIL_TOOLTIP.equals(evt.getKey())) {
            boolean displayThumbnailTooltip = (Boolean) evt.getNewValue();
            isDisplayThumbnailTooltip = displayThumbnailTooltip;
            if (!displayThumbnailTooltip) {
                setToolTipText(null);
            }
        }
    }

    private boolean readPersistedMetaDataOverlay() {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        if (preferences != null && preferences.containsKey(KEY_SHOW_METADATA_OVERLAY)) {
            return preferences.getBoolean(KEY_SHOW_METADATA_OVERLAY);
        }
        return false;
    }

    private void persistMetaDataOverlay() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.setBoolean(KEY_SHOW_METADATA_OVERLAY, metaDataOverlay);
    }

    @Override
    public boolean requestFocusInWindow() {
        boolean requested = super.requestFocusInWindow();
        repaint(); // Border
        return requested;
    }

    @EventSubscriber(eventClass = DirectoryRenamedEvent.class)
    public void directoryRenamed(DirectoryRenamedEvent evt) {
        if (originOfOfDisplayedThumbnails.isInSameFileSystemDirectory() && !isEmpty()) {
            synchronized (this) {
                File thisDirectory = files.get(0).getParentFile();
                File oldDirectory = evt.getOldName();
                File newDirectory = evt.getNewName();
                if (!ObjectUtil.equals(thisDirectory, oldDirectory) || newDirectory == null) {
                    return;
                }
                List<File> newFiles = new ArrayList<>(files.size());
                String newDirectoryPath = newDirectory.getAbsolutePath();
                for (File file : files) {
                    File newFile = new File(newDirectoryPath + File.separator + file.getName());
                    newFiles.add(newFile);
                }
                setFiles(newFiles, originOfOfDisplayedThumbnails);
            }
        }
    }

    public void showMessagePopup(final String text, final Object owner) {
        if (text == null) {
            throw new NullPointerException("text == null");
        }
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                hideMessagePopup(ThumbnailsPanel.this);

                Component c = createPopupComponent(text);
                Dimension cSize = c.getPreferredSize();
                Rectangle thisRect = getVisibleRect();
                int x = thisRect.width > cSize.width
                        ? (thisRect.width - cSize.width) / 2
                        : 0;
                int y = thisRect.height > cSize.height
                        ? (thisRect.height - cSize.height) / 2
                        : 0;
                Point p = new Point(x, y);
                SwingUtilities.convertPointToScreen(p, ThumbnailsPanel.this);

                PopupFactory factory = PopupFactory.getSharedInstance();
                messagePopup = factory.getPopup(ThumbnailsPanel.this, c, p.x, p.y);
                messagePopupOwner = owner;
                messagePopup.show();
            }
        });
    }

    public void hideMessagePopup(final Object owner) {
        if (owner == null) {
            throw new NullPointerException("owner == null");
        }
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                if (messagePopup != null && (messagePopupOwner == owner || owner == ThumbnailsPanel.this)) {
                    messagePopup.hide();
                    messagePopup = null;
                    messagePopupOwner = null;
                }
            }
        });
    }

    private Component createPopupComponent(final String text) {
        JPanel panel = UiFactory.panel(new GridBagLayout());
        panel.setBackground(getBackground());
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, UiFactory.scale(1)));

        JLabel label = UiFactory.label(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, UiFactory.scale(24)));
        label.setForeground(Color.LIGHT_GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = UiFactory.insets(15, 15, 15, 15);

        panel.add(label, gbc);

        return panel;
    }
}
