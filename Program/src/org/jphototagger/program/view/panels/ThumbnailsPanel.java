package org.jphototagger.program.view.panels;

import org.jphototagger.lib.comparator.FileSort;
import org.jphototagger.lib.event.util.MouseEventUtil;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.MathUtil;
import org.jphototagger.program.app.AppFileFilters;
import org.jphototagger.program.app.AppLifeCycle;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.cache.RenderedThumbnailCache;
import org.jphototagger.program.controller.thumbnail.ControllerThumbnailDoubleklick;
import org.jphototagger.program.data.Exif;
import org.jphototagger.program.data.ThumbnailFlag;
import org.jphototagger.program.data.UserDefinedFileFilter;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.DatabaseUserDefinedFileFilters;
import org.jphototagger.program.datatransfer.TransferHandlerThumbnailsPanel;
import org.jphototagger.program.event.UserSettingsEvent;
import org.jphototagger.program.event.listener.AppExitListener;
import org.jphototagger.program.event.listener.DatabaseImageFilesListener;
import org.jphototagger.program.event.listener.DatabaseUserDefinedFileFiltersListener;
import org.jphototagger.program.event.listener.RefreshListener;
import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.event.listener.ThumbnailUpdateListener;
import org.jphototagger.program.event.RefreshEvent;
import org.jphototagger.program.event.ThumbnailUpdateEvent;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.types.FileAction;
import org.jphototagger.program.types.SizeUnit;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;
import org.jphototagger.program.view.renderer.ThumbnailPanelRenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

import java.io.File;
import java.io.FileFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TooManyListenersException;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.TransferHandler;
import org.jphototagger.program.event.listener.UserSettingsListener;

/**
 *
 * @author Elmar Baumann, Tobias Stening
 */
public class ThumbnailsPanel extends JPanel
        implements ComponentListener, MouseListener, MouseMotionListener, KeyListener, ThumbnailUpdateListener,
                   AppExitListener, DatabaseUserDefinedFileFiltersListener, DatabaseImageFilesListener, UserSettingsListener {

    private static final String KEY_THUMBNAIL_WIDTH = "ThumbnailsPanel.ThumbnailWidth";
    private static final long serialVersionUID = 1034671645083632578L;
    private static final int MARGIN_THUMBNAIL = 3;
    public static final Color COLOR_FOREGROUND_PANEL = Color.WHITE;
    public static final Color COLOR_BACKGROUND_PANEL = new Color(32, 32, 32);
    private int isClickInSelection = -1;
    private final Map<Integer, ThumbnailFlag> flagOfThumbnailIndex = new HashMap<Integer, ThumbnailFlag>();
    private final List<ThumbnailsPanelListener> panelListeners = new ArrayList<ThumbnailsPanelListener>();
    private final List<Integer> selectedThumbnailIndices = new ArrayList<Integer>();
    private int thumbnailCountPerRow = 0;
    private boolean dragThumbnailsEnabled = false;
    private boolean isDisplayThumbnailTooltip = UserSettings.INSTANCE.isDisplayThumbnailTooltip();
    private boolean transferDataOfDraggedThumbnails = false;
    private final ThumbnailPanelRenderer renderer = new ThumbnailPanelRenderer(this);
    private final transient RenderedThumbnailCache renderedThumbnailCache = RenderedThumbnailCache.INSTANCE;
    private final EnumMap<Content, List<RefreshListener>> refreshListenersOf = new EnumMap<Content, List<RefreshListener>>(Content.class);
    private final PopupMenuThumbnails popupMenu = PopupMenuThumbnails.INSTANCE;
    private Comparator<File> fileSortComparator = FileSort.NAMES_ASCENDING.getComparator();
    private FileFilter fileFilter = AppFileFilters.INSTANCE.getAllAcceptedImageFilesFilter();
    private final List<File> files = Collections.synchronizedList(new ArrayList<File>());
    private FileAction fileAction = FileAction.UNDEFINED;
    private Content content = Content.UNDEFINED;
    private final transient ControllerThumbnailDoubleklick ctrlDoubleklick;
    private boolean drag;
    private boolean keywordsOverlay;
    private volatile boolean notifySelChanged;
    private volatile boolean notifyTnsChanged;
    private volatile boolean notifyRefresh;
    private JViewport viewport;

    public ThumbnailsPanel() {
        initRefreshListeners();
        ctrlDoubleklick = new ControllerThumbnailDoubleklick(this);
        setDragEnabled(true);
        setTransferHandler(new TransferHandlerThumbnailsPanel());
        readProperties();
        renderedThumbnailCache.setRenderer(renderer);
        setBackground(COLOR_BACKGROUND_PANEL);
        listen();
    }

    private void listen() {
        renderedThumbnailCache.addThumbnailUpdateListener(this);
        DatabaseUserDefinedFileFilters.INSTANCE.addListener(this);
        DatabaseImageFiles.INSTANCE.addListener(this);
        UserSettings.INSTANCE.addUserSettingsListener(this);
        addComponentListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        AppLifeCycle.INSTANCE.addAppExitListener(this);

        try {
            getDropTarget().addDropTargetListener(renderer);
        } catch (TooManyListenersException ex) {
            AppLogger.logSevere(getClass(), ex);
        }
    }

    private void clearSelectionAndFlags() {
        clearSelection();
        flagOfThumbnailIndex.clear();
    }

    /**
     *
     * @param  index valid index
     * @throws       IllegalArgumentException if index is not vaid
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
                    rerender(i.intValue());
                }
            }
        }
    }

    private synchronized void convertSelection(List<File> oldFiles, List<File> newFiles) {
        List<Integer> newSelection = new ArrayList<Integer>();

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
        clearSelectionAtIndices(new ArrayList<Integer>(selectedThumbnailIndices));
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
     * Enables the Drag gesture whithin the thumbnails panel. Whitout calling
     * this, {@link #handleMouseDragged(java.awt.event.MouseEvent)} will never
     * called.
     *
     * @param enabled true if enabled. Default: false
     */
    private synchronized void setDragEnabled(boolean enabled) {
        dragThumbnailsEnabled = enabled;
    }

    private synchronized int getThumbnailWidth() {
        return renderer.getThumbnailWidth();
    }

    private synchronized List<Integer> getSelectedIndices() {
        return new ArrayList<Integer>(selectedThumbnailIndices);
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
    public synchronized void thumbnailUpdated(final ThumbnailUpdateEvent event) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                int index = getIndexOf(event.getSource());

                if (index >= 0) {
                    repaintAtIndex(index);
                }
            }
        });
    }

    private synchronized void addFlag(int index, ThumbnailFlag flag) {
        flagOfThumbnailIndex.put(index, flag);
    }

    private synchronized ThumbnailFlag getFlagAtIndex(int index) {
        return flagOfThumbnailIndex.get(index);
    }

    public synchronized ThumbnailFlag getFlagOfFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        return flagOfThumbnailIndex.get(getIndexOf(file));
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

    /**
     * Returns whether at least one image file is selected.
     *
     * @return true if ore more image files are selected
     */
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
     * This is different to the drop index when other content will be dropped,
     * e.g. metadata.
     *
     * @param  x x coordinate of current move position
     * @param  y y coordinate of current move position
     * @return     index or -1 if no valid index is near the move position
     */
    public synchronized int getImageMoveDropIndex(int x, int y) {
        int row = Math.max(0, (y - MARGIN_THUMBNAIL) / (renderer.getThumbnailAreaHeight() + MARGIN_THUMBNAIL));
        int col = Math.max(0, Math.min(getColumnCount(),
                                       (x - MARGIN_THUMBNAIL) / (renderer.getThumbnailAreaWidth() + MARGIN_THUMBNAIL)));

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

        if (isLeftClick &&!hasFocus()) {
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
                isClickInSel = true;
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
            Set<Integer> currentSelection = new HashSet<Integer>(selectedThumbnailIndices);

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
        boolean isEnhance = false;

        synchronized (this) {
            isEnhance = isAFileSelected();

            if (isEnhance) {
                Set<Integer> rerenderTargets = new HashSet<Integer>(selectedThumbnailIndices);
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

    private synchronized Set<Integer> getValidIndicesOf(Collection<Integer> indices) {
        Set<Integer> validIndices = new HashSet<Integer>(indices.size());

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

            Set<Integer> rerenderTargets = new HashSet<Integer>(selectedThumbnailIndices);

            selectedThumbnailIndices.clear();
            selectedThumbnailIndices.add(index);
            rerenderTargets.add(index);
            rerender(rerenderTargets);
        }

        notifySelectionChanged();
        repaint();
    }

    private void addIndexToSelection(int index) {
        boolean isSelect = false;

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
        boolean isRemove = false;

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

        File file = null;

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
            int lastColumn = Math.min(thumbnailCountPerRow - 1,
                                      getCountHorizontalRightFromX(rectClip.x + rectClip.width));

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
                prefetch(prefetchHighStart, prefetchHighEnd, isKeywordsOverlay());
            }

            if (isIndex(prefetchLowStart) && isIndex(prefetchLowEnd)) {
                prefetch(prefetchLowStart, prefetchLowEnd, isKeywordsOverlay());
            }
        }

        if (drag) {
            renderer.paintImgDropMarker(g);
        }
    }

    private void paintPanelBackground(Graphics g) {
        Color oldColor = g.getColor();

        g.setColor(COLOR_BACKGROUND_PANEL);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(oldColor);
    }

    private synchronized void paintThumbnail(int index, Graphics g) {
        Point topLeft = getTopLeftOfTnIndex(index);
        Image im = renderedThumbnailCache.getThumbnail(getFileAtIndex(index), renderer.getThumbnailWidth(),
                       isKeywordsOverlay());

        if (im != null) {
            g.drawImage(im, topLeft.x, topLeft.y, viewport);
        }
    }

    /**
     * Adds a refresh listener for a specific content.
     *
     * @param listener  listener
     * @param content   content
     */
    public void addRefreshListener(RefreshListener listener, Content content) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        if (content == null) {
            throw new NullPointerException("content == null");
        }

        synchronized (refreshListenersOf) {
            refreshListenersOf.get(content).add(listener);
        }
    }

    @Override
    public void appWillExit() {
        UserSettings.INSTANCE.getSettings().set(getThumbnailWidth(), ThumbnailsPanel.KEY_THUMBNAIL_WIDTH);
        UserSettings.INSTANCE.writeToFile();
    }

    private String createTooltipTextForIndex(int index) {
        if (isIndex(index)) {
            File file = files.get(index);

            if (!file.exists()) {
                return file.getAbsolutePath();
            }

            ThumbnailFlag flag = getFlagAtIndex(index);
            String flagText = (flag == null)
                              ? ""
                              : flag.getString();
            long length = file.length();
            SizeUnit unit = SizeUnit.unit(length);
            long unitLength = length / unit.bytes();
            Date date = new Date(file.lastModified());
            String unitString = unit.toString();

            return JptBundle.INSTANCE.getString("ThumbnailsPanel.TooltipText", file, unitLength, unitString, date,
                    date, getSidecarFileNameOfFile(file), flagText);
        } else {
            return "";
        }
    }

    private String getSidecarFileNameOfFile(File file) {
        File sidecarFile = XmpMetadata.getSidecarFile(file);

        return (sidecarFile == null)
               ? ""
               : sidecarFile.getAbsolutePath();
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
     * Returns the number of Thumbnails.
     *
     * @return thumbnail count
     */
    public synchronized int getFileCount() {
        return files.size();
    }

    public synchronized boolean hasFiles() {
        return !files.isEmpty();
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
     * Returns the files with a specific index.
     *
     * @param  indices  file indices
     * @return files of valid indices
     */
    private synchronized List<File> getFilesAtIndices(List<Integer> indices) {
        List<File> f = new ArrayList<File>();

        for (Integer index : indices) {
            if (isIndex(index)) {
                f.add(files.get(index));
            }
        }

        return f;
    }

    private synchronized List<Integer> getIndicesOfFiles(List<File> files, boolean onlyIfExists) {
        List<Integer> indices = new ArrayList<Integer>(files.size());

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

    public synchronized Comparator<File> getFileSortComparator() {
        return fileSortComparator;
    }

    private void initRefreshListeners() {
        synchronized (refreshListenersOf) {
            for (Content c : Content.values()) {
                refreshListenersOf.put(c, new ArrayList<RefreshListener>());
            }
        }
    }

    /**
     * Returns wheter an index of a file is valid.
     *
     * @param  index index
     * @return true if valid
     */
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
        List<File> filesWithoutMoved = new ArrayList<File>(files);
        int fileCount = filesWithoutMoved.size();

        filesWithoutMoved.removeAll(selFiles);

        List<File> newOrderedFiles = new ArrayList<File>(fileCount);

        newOrderedFiles.addAll(filesWithoutMoved.subList(0, index));
        newOrderedFiles.addAll(selFiles);
        newOrderedFiles.addAll(filesWithoutMoved.subList(index, filesWithoutMoved.size()));
        files.clear();
        files.addAll(newOrderedFiles);
        clearSelectionAtIndices(getIndicesOfFiles(selFiles, true));
        repaint();
    }

    private void notifyRefreshListeners(RefreshEvent evt) {
        synchronized (refreshListenersOf) {
            if (!notifyRefresh) {
                notifyRefresh = true;
                AppLogger.logInfo(getClass(), "ThumbnailsPanel.Info.Refresh");

                for (RefreshListener listener : refreshListenersOf.get(content)) {
                    listener.refresh(evt);
                }

                notifyRefresh = false;
            }
        }
    }

    private void readProperties() {
        int tnWidth = UserSettings.INSTANCE.getSettings().getInt(ThumbnailsPanel.KEY_THUMBNAIL_WIDTH);

        if (tnWidth > 0) {
            setThumbnailWidth(tnWidth);
        }
    }

    /**
     * Calls <code>refresh()</code> by all added
     * {@link org.jphototagger.program.event.listener.RefreshListener} objects.
     */
    public void refresh() {
        JViewport vp = getViewport();
        Point viewportPosition = new Point(0, 0);

        if (vp != null) {
            viewportPosition = vp.getViewPosition();
        }

        RefreshEvent evt = new RefreshEvent(this, viewportPosition);

        evt.setSelThumbnails(new ArrayList<Integer>(selectedThumbnailIndices));
        notifyRefreshListeners(evt);

        // viewport position has to be set by the refresh listeners because they
        // usually set new files in a *thread* so that setting the viewport has
        // no effect
    }

    /**
     * Applies settings to this panel.
     *
     * @param settings can be null (won't be applied in that case)
     */
    public void apply(Settings settings) {
        if (settings == null) {
            return;
        }

        if (settings.hasViewPosition()) {
            setViewPosition(settings.getViewPosition());
        }

        if (settings.hasSelThumbnails()) {
            setSelectedIndices(settings.getSelThumbnails());
        }
    }

    private void setViewPosition(Point pos) {
        if (viewport != null) {
            validateScrollPane();
            viewport.setViewPosition(pos);
        }
    }

    private void validateScrollPane() {

        // See: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5066771
        GUI.getAppPanel().getScrollPaneThumbnailsPanel().validate();
    }

    private Point getViewPosition() {
        JViewport vp = getViewport();

        return (vp == null)
               ? new Point(0, 0)
               : vp.getViewPosition();
    }

    public synchronized boolean containsFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        return files.contains(file);
    }

    /**
     * Removes files from the <strong>display</strong>, <em>not</em> from the
     * file system.
     *
     * @param filesToRemove  files to removeFiles
     */
    public synchronized void removeFiles(Collection<? extends File> filesToRemove) {
        if (filesToRemove == null) {
            throw new NullPointerException("filesToRemove == null");
        }

        List<File> oldFiles = new ArrayList<File>(files);
        List<Integer> selIndicesToRemove = getSelectedIndicesOfFiles(filesToRemove);

        if (files.removeAll(filesToRemove)) {
            selectedThumbnailIndices.removeAll(selIndicesToRemove);
            convertSelection(oldFiles, files);

            Point viewPos = getViewPosition();

            setFiles(files, content);
            setViewPosition(viewPos);
            renderedThumbnailCache.remove(filesToRemove);
            refresh();
        }
    }

    private synchronized List<Integer> getSelectedIndicesOfFiles(Collection<? extends File> files) {
        List<Integer> selIndices = new ArrayList<Integer>(files.size());

        for (File file : files) {
            int index = getIndexOf(file);

            if (selectedThumbnailIndices.contains(index)) {
                selIndices.add(index);
            }
        }

        return selIndices;
    }

    /**
     * Renames a file <strong>on the display</strong>, <em>not</em> in the
     * file system.
     *
     * @param fromFile  old file
     * @param toFile  new file
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

    /**
     * Repaints a file.
     *
     * @param file  file
     */
    public synchronized void repaintFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        int index = getIndexOf(file);

        if (index > 0) {
            repaintAtIndices(Collections.singleton(index));
        }
    }

    /**
     * Sets the file action.
     *
     * @param fileAction  file action
     */
    public synchronized void setFileAction(FileAction fileAction) {
        if (fileAction == null) {
            throw new NullPointerException("fileAction == null");
        }

        this.fileAction = fileAction;
    }

    /**
     * Color of the background surrounding a highlighted thumbnail. When
     * changing, look for {@link #COLOR_TEXT_HIGHLIGHTED}.
     */

    /**
     * Sets the files to display. Previous desplayed files will be hidden.
     * The new files will be displayed in the defined sort order.
     *
     * @param files    files
     * @param content  content description of the files
     */
    public synchronized void setFiles(List<File> files, Content content) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }

        if (content == null) {
            throw new NullPointerException("content == null");
        }

        synchronized (this) {
            AppLogger.logFine(getClass(), "ThumbnailsPanel.SetFiles.Start", files.size());
            clearSelectionAndFlags();

            List<File> filteredFiles = FileUtil.filterFiles(files, fileFilter);

            Collections.sort(filteredFiles, fileSortComparator);
            this.files.clear();
            this.files.addAll(filteredFiles);
            this.content = content;
            scrollToTop();
            setMissingFilesFlags();
        }

        notifyThumbnailsChanged();
        forceRepaint();
    }

    private synchronized void setMissingFilesFlags() {
        int count = files.size();

        for (int i = 0; i < count; i++) {
            if (!files.get(i).exists()) {
                addFlag(i, ThumbnailFlag.ERROR_FILE_NOT_FOUND);
            }
        }
    }

    /**
     * Sets a file sort comparator, does <em>not</em> sort.
     * This is done via {@link #sort()}.
     *
     * @param comparator comparator
     */
    public synchronized void setFileSortComparator(Comparator<File> comparator) {
        if (comparator == null) {
            throw new NullPointerException("comparator == null");
        }

        fileSortComparator = comparator;
    }

    public synchronized void setFileFilter(FileFilter filter) {
        if (filter == null) {
            throw new NullPointerException("filter == null");
        }

        if (!fileFilter.equals(filter)) {
            fileFilter = filter;
            refresh();
        }
    }

    /**
     * Sorts the files.
     *
     * @see #setFileSortComparator(java.util.Comparator)
     */
    public synchronized void sort() {
        if (!content.equals(Content.IMAGE_COLLECTION)) {
            List<File> selFiles = getSelectedFiles();

            setFiles(new ArrayList<File>(files), content);
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

    /**
     * Fügt einen Beobachter hinzu.
     *
     * @param listener Beobachter
     */
    public void addThumbnailsPanelListener(ThumbnailsPanelListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        synchronized (panelListeners) {
            panelListeners.add(listener);
        }
    }

    private void notifySelectionChanged() {
        synchronized (panelListeners) {
            if (!notifySelChanged) {
                notifySelChanged = true;

                for (ThumbnailsPanelListener listener : panelListeners) {
                    listener.thumbnailsSelectionChanged();
                }

                notifySelChanged = false;
            }
        }
    }

    private void notifyThumbnailsChanged() {
        synchronized (panelListeners) {
            if (!notifyTnsChanged) {
                notifyTnsChanged = true;

                for (ThumbnailsPanelListener listener : panelListeners) {
                    listener.thumbnailsChanged();
                }

                notifyTnsChanged = false;
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
    public void componentMoved(ComponentEvent evt) {}

    @Override
    public void componentShown(ComponentEvent evt) {}

    @Override
    public void componentHidden(ComponentEvent evt) {}

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
     * Sets the viewport. Have to be called before adding files.
     * If a viewport ist set, some additional functions supported, e.g.
     * special keyboard keys that are not handled through the viewport
     * and a scroll pane.
     *
     * @param viewport  Viewport
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
    public void mouseEntered(MouseEvent evt) {}

    @Override
    public void mouseClicked(MouseEvent evt) {}

    @Override
    public void mousePressed(MouseEvent evt) {
        handleMousePressed(evt);
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
        handleMouseReleased();
    }

    @Override
    public void mouseExited(MouseEvent evt) {}

    @Override
    public void mouseMoved(MouseEvent evt) {
        handleMouseMoved(evt);
    }

    @Override
    public void mouseDragged(MouseEvent evt) {
        handleMouseDragged(evt);
    }

    @Override
    public void keyTyped(KeyEvent evt) {}

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
    public void keyReleased(KeyEvent evt) {}

    @Override
    public boolean isFocusable() {
        return true;
    }

    /**
     * @return the keywordsOverlay
     */
    public synchronized boolean isKeywordsOverlay() {
        return keywordsOverlay;
    }

    /**
     * @param keywordsOverlay the keywordsOverlay to set
     */
    public synchronized void setKeywordsOverlay(boolean keywordsOverlay) {
        this.keywordsOverlay = keywordsOverlay;

        // renderedThumbnailCache.rerenderAll(keywordsOverlay);
        repaint();
    }

    /**
     * Returns the index of a specific file.
     *
     * @param  file  file
     * @return Index or -1 if not displayed
     */
    public synchronized int getIndexOf(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        return files.indexOf(file);
    }

    /**
     * Returns a specific file.
     *
     * @param  index index
     * @return file or null if the index is invalid
     * @see    #isIndex(int)
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

    @Override
    public synchronized void filterUpdated(final UserDefinedFileFilter filter) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateFilter(filter);
            }
        });
    }

    private void updateFilter(UserDefinedFileFilter filter) {
        if ((fileFilter instanceof UserDefinedFileFilter.RegexFileFilter)
                && filter.filterEquals(filter.getFileFilter(), (UserDefinedFileFilter.RegexFileFilter) fileFilter)) {
            fileFilter = filter.getFileFilter();
            refresh();
        }
    }

    @Override
    public void filterInserted(UserDefinedFileFilter filter) {

        // ignore
    }

    private synchronized void updateViaFileFilter(File file) {

        // Insertion can't be decided because we don't know whether the image
        // file is e.g. in a displayed directory or matches the criteria of a
        // search
        if (!fileFilter.accept(file) && files.contains(file)) {
            removeFiles(Collections.singleton(file));
        }
    }

    @Override
    public void filterDeleted(UserDefinedFileFilter filter) {

        // ignore
    }

    @Override
    public void imageFileInserted(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        updateViaFileFilter(imageFile);
    }

    @Override
    public void imageFileRenamed(File oldImageFile, File newImageFile) {
        if (oldImageFile == null) {
            throw new NullPointerException("oldImageFile == null");
        }

        if (newImageFile == null) {
            throw new NullPointerException("newImageFile == null");
        }

        updateViaFileFilter(oldImageFile);
        updateViaFileFilter(newImageFile);
    }

    @Override
    public void imageFileDeleted(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        updateViaFileFilter(imageFile);
    }

    @Override
    public void xmpInserted(File imageFile, Xmp xmp) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        updateViaFileFilter(imageFile);
    }

    @Override
    public void xmpUpdated(File imageFile, Xmp oldXmp, Xmp updatedXmp) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        updateViaFileFilter(imageFile);
    }

    @Override
    public void xmpDeleted(File imageFile, Xmp xmp) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        updateViaFileFilter(imageFile);
    }

    @Override
    public void exifInserted(File imageFile, Exif exif) {

        // ignore
    }

    @Override
    public void exifUpdated(File imageFile, Exif oldExif, Exif updatedExif) {

        // ignore
    }

    @Override
    public void exifDeleted(File imageFile, Exif exif) {

        // ignore
    }

    @Override
    public void thumbnailUpdated(File imageFile) {

        // ignore
    }

    @Override
    public void dcSubjectInserted(String dcSubject) {

        // ignore
    }

    @Override
    public void dcSubjectDeleted(String dcSubject) {

        // ignore
    }

    @Override
    public void applySettings(UserSettingsEvent evt) {
        if (evt.getType().equals(UserSettingsEvent.Type.DISPLAY_THUMBNAIL_TOOLTIP)) {
            boolean displayThumbnailTooltip = UserSettings.INSTANCE.isDisplayThumbnailTooltip();

            isDisplayThumbnailTooltip = displayThumbnailTooltip;

            if (!displayThumbnailTooltip) {
                setToolTipText(null);
            }
        }
    }

    public static class Settings {
        private final List<Integer> selThumbnails;
        private final Point viewPosition;

        public Settings(Point viewPosition, List<Integer> selThumbnails) {
            if (viewPosition == null) {
                throw new NullPointerException("viewPosition == null");
            }

            if (selThumbnails == null) {
                throw new NullPointerException("selThumbnails == null");
            }

            this.viewPosition = viewPosition;
            this.selThumbnails = selThumbnails;
        }

        public List<Integer> getSelThumbnails() {
            return Collections.unmodifiableList(selThumbnails);
        }

        public Point getViewPosition() {
            return viewPosition;
        }

        public boolean hasSelThumbnails() {
            return selThumbnails != null;
        }

        public boolean hasViewPosition() {
            return viewPosition != null;
        }
    }
}
