/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.view.panels;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.cache.RenderedThumbnailCache;
import de.elmar_baumann.jpt.controller.thumbnail.ControllerDoubleklickThumbnail;
import de.elmar_baumann.jpt.data.ThumbnailFlag;
import de.elmar_baumann.jpt.event.listener.RefreshListener;
import de.elmar_baumann.jpt.event.listener.ThumbnailsPanelListener;
import de.elmar_baumann.jpt.datatransfer.TransferHandlerThumbnailsPanel;
import de.elmar_baumann.jpt.event.ThumbnailUpdateEvent;
import de.elmar_baumann.jpt.event.listener.AppExitListener;
import de.elmar_baumann.jpt.event.listener.ThumbnailUpdateListener;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.types.Content;
import de.elmar_baumann.jpt.types.FileAction;
import de.elmar_baumann.jpt.types.SizeUnit;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails;
import de.elmar_baumann.jpt.view.renderer.ThumbnailPanelRenderer;
import de.elmar_baumann.lib.comparator.FileSort;
import de.elmar_baumann.lib.event.util.MouseEventUtil;
import de.elmar_baumann.lib.util.MathUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.TransferHandler;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ThumbnailsPanel extends JPanel
        implements ComponentListener, MouseListener, MouseMotionListener,
        KeyListener, ThumbnailUpdateListener, AppExitListener {

    private static final long serialVersionUID = 1034671645083632578L;

    /**
     * Background color of this panel
     */
    public static final Color COLOR_BACKGROUND_PANEL = new Color(32, 32, 32);
    /**
     * Empty space surrounding a thumbnail outside it's border in pixel
     */
    private static final int MARGIN_THUMBNAIL = 3;

    private static final String KEY_THUMBNAIL_WIDTH = "ThumbnailsPanel.ThumbnailWidth";

    private static String getSidecarFilename(File file) {
        String sidecarfile = XmpMetadata.getSidecarFilename(file.getAbsolutePath());
        return sidecarfile == null ? "" : sidecarfile;
    }
    /**
     * Contains the flags of thumbnails at specific indices
     */
    private final Map<Integer, ThumbnailFlag> flagOfThumbnail =
            new HashMap<Integer, ThumbnailFlag>();
    /**
     * Listens to thumbnail events
     */
    private final List<ThumbnailsPanelListener> panelListeners =
            new ArrayList<ThumbnailsPanelListener>();
    /**
     * Contains the indices of the selected thumbnails
     */
    private final List<Integer> selectedThumbnails = new ArrayList<Integer>();
    /**
     * The viewport of this
     */
    private JViewport viewport;
    /**
     * Count of thumbnails horicontal
     */
    private int thumbnailCountPerRow = 0;
    /**
     * Accept dragging thumbnails?
     */
    private boolean dragEnabled = false;
    /**
     * Transfer data of dragged thumbnails
     */
    private boolean transferData = false;
    /**
     * Has the mouse clicked into a thumbnail?
     */
    private int clickInSelection = -1;
    private boolean keywordsOverlay;
    public RenderedThumbnailCache renderedThumbnailCache =
            RenderedThumbnailCache.INSTANCE;
    ThumbnailPanelRenderer renderer = new ThumbnailPanelRenderer(this);

    private Content content = Content.UNDEFINED;
    private ControllerDoubleklickThumbnail controllerDoubleklick;
    private FileAction fileAction = FileAction.UNDEFINED;
    private Comparator<File> fileSortComparator = FileSort.NAMES_ASCENDING.getComparator();
    private final List<File> files = Collections.synchronizedList(new ArrayList<File>());
    private volatile boolean hadFiles;
    private final PopupMenuThumbnails popupMenu = PopupMenuThumbnails.INSTANCE;
    private final Map<Content, List<RefreshListener>> refreshListenersOfContent = new HashMap<Content, List<RefreshListener>>();

    public ThumbnailsPanel() {
        initRefreshListeners();
        controllerDoubleklick = new ControllerDoubleklickThumbnail(this);
        setDragEnabled(true);
        setTransferHandler(new TransferHandlerThumbnailsPanel());
        readProperties();
        // hack to activate Drag events
        //new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this, true, null);

        renderedThumbnailCache.setRenderer(renderer);
        renderedThumbnailCache.addThumbnailUpdateListener(this);
        setBackground(COLOR_BACKGROUND_PANEL);
        listen();
    }

    private void listen() {
        addComponentListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
    }

    private void empty() {
        clearSelection();
        flagOfThumbnail.clear();
    }

    private synchronized void rerender(int index) {
        File file = getFile(index);
        assert file != null;
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
                rerender(i.intValue());
            }
        }
    }

    /* Convert index-based selection to a new set of files
     */
    public void convertSelection(List<File> oldFiles, List<File> newFiles) {
        List<Integer> newSelection = new ArrayList<Integer>();
        for (int i : selectedThumbnails) {
            File file;
            if (oldFiles.size() >= i) continue;
            file = oldFiles.get(i);
            int newI = newFiles.indexOf(file);
            if (newI < 0) continue;
            newSelection.add(new Integer(newI));
        }
        selectedThumbnails.clear();
        selectedThumbnails.addAll(newSelection);
    }

    public void clearSelection() {
        if (selectedThumbnails.size() > 0) {
            List<Integer> rerenderTargets =
                    new ArrayList<Integer>(selectedThumbnails);
            synchronized (this) {
                selectedThumbnails.clear();
            }
            rerender(rerenderTargets);
            notifySelectionChanged();
        }
    }

    /* Also clears the selection, but takes the new indices of the selection
     * as arguments, if files have been reordered etc.
     */
    private void clearSelection(List<Integer> indices) {
        if (indices.size() > 0) {
            synchronized (this) {
                selectedThumbnails.clear();
            }
            rerender(indices);
            notifySelectionChanged();
        }
    }


    private int getFirstSelectedIndex() {
        if (selectedThumbnails.size() > 0) {
            return selectedThumbnails.get(0);
        }
        return -1;
    }

    private int getSelectedIndex() {
        if (selectedThumbnails.size() == 1) {
            return selectedThumbnails.get(0);
        }
        return -1;
    }

    /**
     * Sets the width of a thumbnail.
     *
     * @param width  width in pixel
     */
    public synchronized void setThumbnailWidth(int width) {
        if (width != renderer.getThumbnailWidth()) {
            float oldPosition = getRelativeScrollPosition();
            renderer.setThumbnailWidth(width);
            setCountPerRow();
            setSize(getWidth(), getCalculatedHeight());
            setRelativeScrollPosition(oldPosition);
            repaint();
        }
    }

    private float getRelativeScrollPosition() {
        if (viewport != null) {
            int middle = viewport.getViewRect().y +
                    viewport.getExtentSize().height / 2;
            return (float) middle / (float) getHeight();
        }
        return (float) 0.0;
    }

    private void setRelativeScrollPosition(float p) {
        if (viewport != null) {
            int newY = ((int) (p * getHeight())) -
                    viewport.getExtentSize().height / 2;
            viewport.setViewPosition(new Point(0, Math.max(0, newY)));
        }
    }

    /**
     * Enables the Drag gesture whithin the thumbnails panel. Whitout calling
     * this, {@link #handleMouseDragged(java.awt.event.MouseEvent)} will never called.
     *
     * @param enabled true if enabled. Default: false
     */
    public synchronized void setDragEnabled(boolean enabled) {
        dragEnabled = enabled;
    }

    protected boolean isValidIndex(int thumbnailIndex) {
        return thumbnailIndex >= 0 && thumbnailIndex < files.size();
    }

    public synchronized int getThumbnailWidth() {
        return renderer.getThumbnailWidth();
    }

    /**
     * Liefert die Indexe aller selektierten Thumbnails.
     *
     * @return Indexe
     */
    public synchronized List<Integer> getSelectedIndices() {
        return new ArrayList<Integer>(selectedThumbnails);
    }

    /**
     * Setzt die Indexe selektierter Thumbnails.
     *
     * @param indices Indexe
     */
    public void setSelected(List<Integer> indices) {
        Set<Integer> rerenderTargets =
                new HashSet<Integer>(selectedThumbnails);
        rerenderTargets.addAll(indices);
        synchronized (this) {
            selectedThumbnails.clear();
            selectedThumbnails.addAll(indices);
        }
        if (indices.size() > 0) {
            Collections.sort(selectedThumbnails);
        }
        rerender(rerenderTargets);
        notifySelectionChanged();
        repaint();
    }

    /**
     * Setzt neue Thumbnails.
     */
    protected void setNewThumbnails() {
        forceRepaint();
        notifyThumbnailsChanged();
    }

    /**
     * Setzt ein Thumbnail an einem bestimmten Index (neu). Der Cache wird mit
     * diesem Thumbnail aktualisiert.
     *
     * @param index Index
     */
    protected synchronized void repaint(int index) {
        repaint(getTopLeftOfTnIndex(index).x, getTopLeftOfTnIndex(index).y,
                renderer.getThumbnailAreaWidth(),
                renderer.getThumbnailAreaHeight());
    }

    @Override
    public synchronized void actionPerformed(ThumbnailUpdateEvent event) {
        int index = getIndexOf(event.getSource());
        //renderedThumbnailCache.remove(event.getSource());
        // drop stale update request
        if (index >= 0) {
            repaint(index);
        }
    }

    /**
     * Zum Überschreiben für abgeleitete Klassen. Diese Klasse unternimmt
     * nichts.
     *
     * @param e              Auslösendes Mausereignis
     * @param thumbnailIndex Index des Thumbnails, bei dem geklickt wurde
     */
    public void showPopupMenu(MouseEvent e, int thumbnailIndex) {
    }

    /**
     * Fügt einem Thumbnail ein Flag hinzu.
     *
     * @param index Index des Thumbnails
     * @param flag  Flag
     */
    public synchronized void addFlag(int index, ThumbnailFlag flag) {
        flagOfThumbnail.put(index, flag);
    }

    /**
     * Liefert, ob ein Thumbnail ein Flag hat.
     *
     * @param index Thumbnailindex
     * @return      true, wenn das Thumbnail ein Flag hat
     */
    public synchronized boolean isFlagged(int index) {
        return flagOfThumbnail.containsKey(index);
    }

    /**
     * Liefert das Flag eines Thumbnails.
     *
     * @param index Thumbnailindex
     * @return      Flag oder null, wenn das Thumbnail kein Flag hat
     */
    public synchronized ThumbnailFlag getFlag(int index) {
        return flagOfThumbnail.get(index);
    }

    public synchronized ThumbnailFlag getFlag(File file) {
        return flagOfThumbnail.get(getIndexOf(file));
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
        int startExtPadding = getCountHorizontalLeftFromX(x) *
                (renderer.getThumbnailAreaWidth() + MARGIN_THUMBNAIL);
        int endExtPadding = startExtPadding + MARGIN_THUMBNAIL;
        return x < startExtPadding || (x > endExtPadding && endExtPadding +
                renderer.getThumbnailAreaWidth() + MARGIN_THUMBNAIL <=
                getWidth());
    }

    private boolean isThumbnailAreaInHeight(int y) {
        int startExtPadding = getCountVerticalAboveY(y) *
                (renderer.getThumbnailAreaHeight() + MARGIN_THUMBNAIL);
        int endExtPadding = startExtPadding + MARGIN_THUMBNAIL;
        return y < startExtPadding || y > endExtPadding;
    }

    private boolean isThumbnailArea(int x, int y) {
        return isThumbnailAreaInWidth(x) && isThumbnailAreaInHeight(y);
    }

    protected int getIndexAtPoint(int x, int y) {
        if (isThumbnailArea(x, y)) {
            int tnOffset = (x - MARGIN_THUMBNAIL) /
                    (renderer.getThumbnailAreaWidth() + MARGIN_THUMBNAIL);
            int firstInRow = getFirstPaintIndexAtHeight(y);
            return firstInRow + tnOffset;
        }
        return -1;
    }

    private int getFirstPaintIndexAtHeight(int height) {
        int rowsToStart = getRowCountInHeight(height);
        return rowsToStart * thumbnailCountPerRow;
    }

    private int getLastPaintIndexAtHeight(int height) {
        int rowsToEnd = getRowCountInHeight(height);
        return (rowsToEnd + 1) * thumbnailCountPerRow;
    }

    private int getRowCountInHeight(int height) {
        return (int) ((height - MARGIN_THUMBNAIL) /
                (renderer.getThumbnailAreaHeight() + MARGIN_THUMBNAIL) + 0.5);
    }

    /**
     * Liefert, ob ein Thumbnail mit bestimmtem Index selektiert ist.
     *
     * @param index Thumbnailindex
     * @return      true, wenn das Thumbnail selektiert ist
     */
    public synchronized boolean isSelected(int index) {
        return selectedThumbnails.contains(index);
    }

    public synchronized boolean isSelected(File file) {
        return selectedThumbnails.contains(getIndexOf(file));
    }

    /**
     * Liefert die Anzahl der selektierten Thumbnails.
     *
     * @return Anzahl
     */
    public synchronized int getSelectionCount() {
        return selectedThumbnails.size();
    }

    private Point getTopLeftOfTnIndex(int index) {
        int rowIndex = getRowIndexAt(index);
        int columnIndex = getColumnIndexAt(index);
        int x = MARGIN_THUMBNAIL + columnIndex *
                (renderer.getThumbnailAreaWidth() + MARGIN_THUMBNAIL);
        int y = MARGIN_THUMBNAIL + rowIndex *
                (renderer.getThumbnailAreaHeight() + MARGIN_THUMBNAIL);
        return new Point(x, y);
    }

    public synchronized int getDnDIndex(MouseEvent e) {
        return getDnDIndex(e.getX(), e.getY());
    }

    public synchronized int getDnDIndex(int x, int y) {
        int row = Math.max(0,
                (y - MARGIN_THUMBNAIL) /
                (renderer.getThumbnailAreaHeight() + MARGIN_THUMBNAIL));
        int col = Math.max(0, Math.min(getColumnCount(),
                (x - MARGIN_THUMBNAIL) /
                (renderer.getThumbnailAreaWidth() + MARGIN_THUMBNAIL)));
        if (row < 0 || col < 0) {
            return -1;
        }
        int index = Math.min(row * getColumnCount() + col, files.size() - 1);

        return index;
    }

    private int getColumnCount() {
        int width = getWidth();
        int tnWidth = renderer.getThumbnailAreaWidth();
        int count = (int) ((double) (width - MARGIN_THUMBNAIL) /
                (double) tnWidth);
        return count > files.size()
                ? files.size()
                : count;
    }

    private void transferData(MouseEvent e) {
        if (dragEnabled && getSelectionCount() > 0) {
            TransferHandler transferHandler = getTransferHandler();
            if (transferHandler != null) {
                transferHandler.exportAsDrag(this, e, TransferHandler.COPY);
            }
        }
    }

    private boolean isClickInSelection(MouseEvent e) {
        int clickIndex = getIndexAtPoint(e.getX(), e.getY());
        return selectedThumbnails.contains(clickIndex);
    }

    private void handleMousePressed(MouseEvent e) {
        boolean isLeftClick = MouseEventUtil.isLeftClick(e);
        if (isLeftClick && !hasFocus()) {
            requestFocus();
        }
        if (isLeftClick) {
            int thumbnailIndex = getIndexAtPoint(e.getX(), e.getY());
            if (isValidIndex(thumbnailIndex)) {
                transferData = true;
                if (MouseEventUtil.isDoubleClick(e)) {
                    doubleClickAt(thumbnailIndex);
                    setSelected(thumbnailIndex);
                } else if (e.isControlDown()) {
                    if (!isSelected(thumbnailIndex)) {
                        addToSelection(thumbnailIndex);
                    } else {
                        removeSelection(thumbnailIndex);
                    }
                } else if (e.isShiftDown()) {
                    enhanceSelectionTo(thumbnailIndex);
                } else {
                    if (isClickInSelection(e)) {
                        clickInSelection = thumbnailIndex;
                    } else {
                        setSelected(thumbnailIndex);
                    }
                }
            } else {
                setSelectedAll(false);
            }
        } else if (MouseEventUtil.isPopupTrigger(e)) {
            showPopupMenu(e);
        }
    }

    private void handleMouseReleased() {
        if (clickInSelection >= 0) {
            setSelected(clickInSelection);
            clickInSelection = -1;
        }
    }

    private void handleMouseDragged(MouseEvent e) {
        clickInSelection = -1;
        if (dragEnabled && transferData && getSelectionCount() > 0) {
            transferData(e);
            transferData = false;
        }
    }

    private void handleMouseMoved(MouseEvent e) {
        if (dragEnabled) {
            setCursor(Cursor.getDefaultCursor());
        }
        showToolTip(e);
    }

    private void setSelectedAll(boolean select) {
        Set<Integer> rerenderTargets =
                new HashSet<Integer>(selectedThumbnails);
        selectedThumbnails.clear();
        if (select) {
            for (int index = 0; index < files.size(); index++) {
                selectedThumbnails.add(index);
            }
            renderedThumbnailCache.clear();
            notifySelectionChanged();
            repaint();
        } else {
            rerender(rerenderTargets);
            notifySelectionChanged();
        }

    }

    private void enhanceSelectionTo(int index) {
        if (getSelectionCount() <= 0) {
            setSelected(index);
        } else {
            Set<Integer> rerenderTargets =
                    new HashSet<Integer>(selectedThumbnails);
            int firstSelected = getFirstSelectedIndex();
            selectedThumbnails.clear();
            int startIndex = index > firstSelected
                    ? firstSelected
                    : index;
            int endIndex = index > firstSelected
                    ? index
                    : firstSelected;
            for (int i = startIndex; i <= endIndex; i++) {
                selectedThumbnails.add(i);
            }
            rerenderTargets.addAll(selectedThumbnails);
            rerender(rerenderTargets);
            notifySelectionChanged();
            repaint();
        }
    }

    private void setSelected(int index) {
        Set<Integer> rerenderTargets =
                new HashSet<Integer>(selectedThumbnails);
        selectedThumbnails.clear();
        selectedThumbnails.add(index);
        rerenderTargets.add(index);
        rerender(rerenderTargets);
        notifySelectionChanged();
        repaint();
    }

    private void addToSelection(int index) {
        if (!isSelected(index)) {
            selectedThumbnails.add(index);
            Collections.sort(selectedThumbnails);
            rerender(index);
            notifySelectionChanged();
        }
    }

    private void removeSelection(int index) {
        if (isSelected(index)) {
            selectedThumbnails.remove(new Integer(index));
            //renderedThumbnailCache.remove(getFile(index));
            rerender(index);
            notifySelectionChanged();
        }
    }

    private int getColumnIndexAt(int thumbnailIndex) {
        return thumbnailIndex > 0 && thumbnailCountPerRow > 0
                ? thumbnailIndex % thumbnailCountPerRow
                : 0;
    }

    private int getRowIndexAt(int thumbnailIndex) {
        return thumbnailCountPerRow > 0
                ? thumbnailIndex / thumbnailCountPerRow
                : 0;
    }

    private void handleMouseDoubleKlicked() {
        int indexSelectedThumbnail = getSelectedIndex();
        if (indexSelectedThumbnail >= 0) {
            doubleClickAt(indexSelectedThumbnail);
        }
    }

    public void forceRepaint() {
        revalidate();
        repaint();
    }

    protected void prefetch(int low, int high, boolean xmp) {
        File file;
        for (int i = low; i <= high; i++) {
            file = getFile(i);
            assert file != null: "X: " + i + ", " + low + ", " + high + ".";
            renderedThumbnailCache.prefetch(file,
                    renderer.getThumbnailWidth(), xmp);
        }
    }

    @Override
    public synchronized void paintComponent(Graphics g) {
        paintPanelBackground(g);
        if (files.size() > 0) {
            Rectangle rectClip = g.getClipBounds();
            int firstIndex = Math.min(files.size(),
                    getFirstPaintIndexAtHeight(rectClip.y));
            int lastIndex = Math.min(
                    getLastPaintIndexAtHeight(rectClip.y + rectClip.height),
                    files.size());
            int firstColumn = Math.max(0,
                    getCountHorizontalLeftFromX(rectClip.x));
            int lastColumn = Math.min(thumbnailCountPerRow - 1,
                    getCountHorizontalRightFromX(rectClip.x + rectClip.width));
            //g.setFont(FONT);
            for (int index = firstIndex; index < lastIndex; index++) {
                if (index % thumbnailCountPerRow >= firstColumn &&
                        index % thumbnailCountPerRow <= lastColumn) {
                    paintThumbnail(index, g);
                }
            }
            paintPanelFocusBorder(g);

            int prefetchLowStart = Math.max(0,
                    firstIndex - thumbnailCountPerRow * 5);
            int prefetchLowEnd = firstIndex - 1;
            int prefetchHighStart = lastIndex;
            int prefetchHighEnd = Math.min(files.size() - 1,
                    lastIndex + thumbnailCountPerRow * 5);

            prefetch(prefetchHighStart, prefetchHighEnd, isKeywordsOverlay());
            prefetch(prefetchLowStart, prefetchLowEnd, isKeywordsOverlay());
        }
    }

    private void paintPanelBackground(Graphics g) {
        Color oldColor = g.getColor();
        g.setColor(COLOR_BACKGROUND_PANEL);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(oldColor);
    }

    private void paintThumbnail(int index, Graphics g) {
        Point topLeft = getTopLeftOfTnIndex(index);
        Image im = renderedThumbnailCache.getThumbnail(
                getFile(index), renderer.getThumbnailWidth(),
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
        synchronized (refreshListenersOfContent) {
            refreshListenersOfContent.get(content).add(listener);
        }
    }

    @Override
    public void appWillExit() {
        UserSettings.INSTANCE.getSettings().setInt(getThumbnailWidth(), ThumbnailsPanel.KEY_THUMBNAIL_WIDTH);
        UserSettings.INSTANCE.writeToFile();
    }

    private String createTooltipText(int index) {
        if (isIndex(index)) {
            File file = files.get(index);
            if (!file.exists()) {
                return file.getAbsolutePath();
            }
            ThumbnailFlag flag = getFlag(index);
            String flagText = flag == null ? "" : flag.getString();
            long length = file.length();
            SizeUnit unit = SizeUnit.unit(length);
            long unitLength = (long) (length / unit.bytes() + 0.5);
            Date date = new Date(file.lastModified());
            String unitString = unit.toString();
            return Bundle.getString("ThumbnailsPanel.TooltipText", file, unitLength, unitString, date, date, getSidecarFilename(file), flagText);
        } else {
            return "";
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
     * Returns the number of Thumbnails.
     *
     * @return thumbnail count
     */
    /**
     * Returns the number of Thumbnails.
     *
     * @return thumbnail count
     */
    public synchronized int getFileCount() {
        return files.size();
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
    public synchronized List<File> getFiles(List<Integer> indices) {
        List<File> f = new ArrayList<File>();
        for (Integer index : indices) {
            if (isIndex(index)) {
                f.add(files.get(index));
            }
        }
        return f;
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

    /**
     * Returns the filenames of selected thumbnails.
     *
     * @return filenames
     */
    public synchronized List<File> getSelectedFiles() {
        return getFiles(getSelectedIndices());
    }

    /**
     * Returns the sort comparator.
     *
     * @return sort type
     */
    public synchronized Comparator<File> getFileSortComparator() {
        return fileSortComparator;
    }

    private void initRefreshListeners() {
        for (Content c : Content.values()) {
            refreshListenersOfContent.put(c, new ArrayList<RefreshListener>());
        }
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

    public synchronized void moveSelectedToIndex(int index) {
        if (!isValidIndex(index)) {
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
        List<File> selFiles = getFiles(selectedIndices);
        List<File> filesWithoutMoved = new ArrayList<File>(files);
        int fileCount = filesWithoutMoved.size();
        filesWithoutMoved.removeAll(selFiles);
        List<File> newOrderedFiles = new ArrayList<File>(fileCount);
        newOrderedFiles.addAll(filesWithoutMoved.subList(0, index));
        newOrderedFiles.addAll(selFiles);
        newOrderedFiles.addAll(filesWithoutMoved.subList(index, filesWithoutMoved.size()));
        files.clear();
        files.addAll(newOrderedFiles);
        clearSelection(getIndices(selFiles, true));
        repaint();
    }

    private void notifyRefreshListeners() {
        synchronized (refreshListenersOfContent) {
            AppLog.logInfo(getClass(), "ThumbnailsPanel.Info.Refresh");

            for (RefreshListener listener : refreshListenersOfContent.get(content)) {
                listener.refresh();
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
     * {@link de.elmar_baumann.jpt.event.listener.RefreshListener} objects.
     */
    public synchronized void refresh() {
        JViewport vp = getViewport();
        Point viewportPosition = null;
        if (vp != null) {
            viewportPosition = vp.getViewPosition();
        }
        notifyRefreshListeners();
        // does set the images
        if (vp != null) {
            vp.setViewPosition(viewportPosition);
        }
    }

    public synchronized boolean displaysFile(File file) {
        return files.contains(file);
    }

    /**
     * Removes files from the <strong>display</strong>, <em>not</em> from the
     * file system.
     *
     * @param filesToRemove  files to remove
     */
    public synchronized void remove(List<File> filesToRemove) {
        List<File> oldFiles = new ArrayList<File>(files);
        if (files.removeAll(filesToRemove)) {
            convertSelection(oldFiles, files);
            setFiles(files, content);
            renderedThumbnailCache.remove(filesToRemove);
            refresh();
        }
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
        }
    }

    /**
     * Repaints a file.
     *
     * @param file  file
     */
    public synchronized void repaint(File file) {
        repaint();
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
    public void setFiles(List<File> files, Content content) {
        empty();
        synchronized (this) {
            this.files.clear();
            if (!content.equals(Content.IMAGE_COLLECTION)) {
                Collections.sort(files, fileSortComparator);
            }
            this.files.addAll(files);
            this.content = content;
        }
        setNewThumbnails();
        if (hadFiles) {
            scrollToTop();
        }
        hadFiles = true;
        setMissingFilesFlags();
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
        if (missing) {
            repaint();
        }
    }

    /**
     * Sets a file sort comparator, does <em>not</em> sort.
     * This is done via {@link #sort()}.
     *
     * @param comparator comparator
     */
    public synchronized void setFileSortComparator(Comparator<File> comparator) {
        fileSortComparator = comparator;
    }

    /**
     * Sorts the files.
     *
     * @see #setFileSortComparator(java.util.Comparator)
     */
    public synchronized void sort() {
        if (!content.equals(Content.IMAGE_COLLECTION)) {
            List<File> selectedFiles = getSelectedFiles();
            setFiles(new ArrayList<File>(files), content);
            setSelected(getIndices(selectedFiles, true));
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

    private int getRowCount() {
        double count = (double) files.size() / (double) thumbnailCountPerRow;
        return files.size() > thumbnailCountPerRow
                ? (int) (MathUtil.isInteger(count)
                ? count
                : count + 1)
                : files.size() == 0
                ? 0
                : 1;
    }

    /**
     * Fügt einen Beobachter hinzu.
     *
     * @param listener Beobachter
     */
    public void addThumbnailsPanelListener(ThumbnailsPanelListener listener) {
        synchronized (panelListeners) {
            panelListeners.add(listener);
        }
    }

    private void notifySelectionChanged() {
        synchronized (panelListeners) {
            for (ThumbnailsPanelListener listener : panelListeners) {
                listener.thumbnailsSelectionChanged();
            }
        }
    }

    private void notifyThumbnailsChanged() {
        synchronized (panelListeners) {
            for (ThumbnailsPanelListener listener : panelListeners) {
                listener.thumbnailsChanged();
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Component parent = getParent();
        int width = parent instanceof JViewport
                ? parent.getWidth()
                : getWidth();
        int heigth = getCalculatedHeight();
        return new Dimension(width, heigth);
    }

    private int getCalculatedHeight() {
        return MARGIN_THUMBNAIL +
                getRowCount() * (renderer.getThumbnailAreaHeight() +
                MARGIN_THUMBNAIL);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        setCountPerRow();
    }

    private void setCountPerRow() {
        int width = getWidth();
        int tnAreaWidth = renderer.getThumbnailAreaWidth();
        double count =
                (double) (width - MARGIN_THUMBNAIL) /
                (double) (tnAreaWidth + MARGIN_THUMBNAIL);
        thumbnailCountPerRow = count >= 1
                ? (int) count
                : 1;
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    private void setSelectedUp() {
        int indexSelectedThumbnail = getSelectedIndex();
        int indexToSelect = indexSelectedThumbnail - thumbnailCountPerRow;
        if (indexSelectedThumbnail >= 0 && isIndex(indexToSelect)) {
            setSelected(indexToSelect);
        }
    }

    private void setSelectedDown() {
        int indexSelectedThumbnail = getSelectedIndex();
        int indexToSelect = indexSelectedThumbnail + thumbnailCountPerRow;
        if (indexSelectedThumbnail >= 0 && isIndex(indexToSelect)) {
            setSelected(indexToSelect);
        }
    }

    private void setSelectedNext() {
        int indexSelectedThumbnail = getSelectedIndex();
        int indexToSelect = (indexSelectedThumbnail + 1) % files.size();
        if (indexSelectedThumbnail >= 0 && isIndex(indexToSelect)) {
            if (indexToSelect == 0) scrollToTop();
            setSelected(indexToSelect);
        }
    }

    private void setSelectedPrevious() {
        int indexSelectedThumbnail = getSelectedIndex();
        int indexToSelect = (indexSelectedThumbnail - 1) % files.size();
        if (indexToSelect < 0) indexToSelect = files.size() - 1;
        if (indexSelectedThumbnail >= 0 && isIndex(indexToSelect)) {
            if (indexToSelect >= files.size() - 1) scrollToBottom();
            setSelected(indexToSelect);
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
        this.viewport = viewport;
    }

    /**
     * Returns the viewport within this panel is displayed.
     *
     * @return viewport or null if not set or if the panel is not in a viewport
     */
    public synchronized JViewport getViewport() {
        return viewport;
    }

    protected void scrollToTop() {
        if (viewport != null) {
            viewport.setViewPosition(new Point(0, 0));
        }
    }

    protected void scrollToBottom() {
        if (viewport != null) {
            viewport.setViewPosition(new Point(0, getHeight()));
        }
    }

    private void checkScrollUp() {
        if (viewport != null && getSelectedIndex() >= 0) {
            int tnHeight = renderer.getThumbnailAreaHeight();
            int topSel = getTopLeftOfTnIndex(getSelectedIndex()).y - tnHeight;
            int viewPosBottom = viewport.getViewPosition().y;
            if (topSel < viewPosBottom) {
                scrollOneImageUp();
            }
        }
    }

    private void checkScrollDown() {
        if (viewport != null && getSelectedIndex() >= 0) {
            int tnHeight = renderer.getThumbnailAreaHeight();
            int bottomSel = getTopLeftOfTnIndex(getSelectedIndex()).y + tnHeight;
            int viewPosBottom =
                    viewport.getViewPosition().y + viewport.getHeight();
            if (bottomSel > viewPosBottom) {
                scrollOneImageDown();
            }
        }
    }

    private void scrollOneImageUp() {
        if (viewport != null) {
            Point p = viewport.getViewPosition();
            int tnHeight = renderer.getThumbnailAreaHeight();
            int y = p.y - tnHeight >= 0
                    ? p.y - tnHeight
                    : 0;
            viewport.setViewPosition(new Point(0, y));
        }
    }

    private void scrollOneImageDown() {
        if (viewport != null) {
            Point p = viewport.getViewPosition();
            viewport.setViewPosition(
                    new Point(0, p.y + renderer.getThumbnailAreaHeight()));
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        handleMousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        handleMouseReleased();
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        handleMouseMoved(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        handleMouseDragged(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
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
        } else if ((e.getModifiers() & KeyEvent.CTRL_MASK) ==
                KeyEvent.CTRL_MASK && keyCode == KeyEvent.VK_A) {
            setSelectedAll(true);
        } else if (keyCode == KeyEvent.VK_HOME) {
            clearSelection();
            scrollToTop();
        } else if (keyCode == KeyEvent.VK_END) {
            clearSelection();
            scrollToBottom();
        } else if (keyCode == KeyEvent.VK_PAGE_DOWN ||
                keyCode == KeyEvent.VK_PAGE_UP) {
            clearSelection();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    /**
     * @return the keywordsOverlay
     */
    public boolean isKeywordsOverlay() {
        return keywordsOverlay;
    }

    /**
     * @param keywordsOverlay the keywordsOverlay to set
     */
    public void setKeywordsOverlay(boolean keywordsOverlay) {
        this.keywordsOverlay = keywordsOverlay;
        //renderedThumbnailCache.rerenderAll(keywordsOverlay);
        repaint();
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

    protected void doubleClickAt(int index) {
        controllerDoubleklick.doubleClickAtIndex(index);
    }

    protected void showPopupMenu(MouseEvent e) {
        popupMenu.show(this, e.getX(), e.getY());
    }

    protected void showToolTip(MouseEvent evt) {
        int index = getIndexAtPoint(evt.getX(), evt.getY());
        setToolTipText(createTooltipText(index));
    }
}
