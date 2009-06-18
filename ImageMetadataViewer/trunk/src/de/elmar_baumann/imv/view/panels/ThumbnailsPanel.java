package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.event.ThumbnailsPanelAction;
import de.elmar_baumann.imv.event.ThumbnailsPanelListener;
import de.elmar_baumann.imv.data.ThumbnailFlag;
import de.elmar_baumann.lib.event.MouseEventUtil;
import de.elmar_baumann.lib.util.MathUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.TransferHandler;

/**
 * Panel zum Anzeigen mehrerer Thumbnails mit einem Text darunter. Diese Klasse
 * übernimmt das Zeichnen und reagiert auf Ereignisse. Eine spezialisierte
 * Klasse liefert die Thumbnails sowie den Text, der unter die Thumbnails
 * gezeichnet werden soll.
 * 
 * Mit {@link #setNewThumbnails(int)} wird bekanntgegeben, dass und wie viele 
 * Thumbnails anzueigen sind. Mit {@link #getThumbnail(int)} erfragt diese
 * Klasse Thumbnails von spezialisierten Klassen mit Indizes zwischen 0 und 
 * Anzahl - 1.
 * 
 * Jedes Thumbnail ist eingebettet in eine Fläche, die definiert ist durch
 * ein "internes Polster" (<code>internalPadding</code>). Darum wird ein Rand
 * gezeichnet mit einer bestimmten Dicke (<code>thumbnailBorderWidth</code>).
 * 
 * Der Abstand zwischen den Thumbnails wird als "externes Polster" bezeichnet
 * (<code>externalPadding</code>). Der Text liegt darin.
 * 
 * Jedes Thumbnail wird gecached und es werden nur Thumbnails gezeichnet, die
 * sich innerhalb der Clip-Bounds befinden.
 * 
 * Zum Benutzen sind unbedingt folgende Operationen <em>als erstes</em> aufzurufen:
 * <ol>
 *     <li>{@link #setNewThumbnails(int)}</li>
 *     <li>{@link #setThumbnailWidth(int)}</li>
 * </ol>
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public abstract class ThumbnailsPanel extends JPanel
        implements ComponentListener, MouseListener, MouseMotionListener,
                   KeyListener {

    /**
     * Width of a thumbnail flag in pixel
     */
    private static final int FLAG_WIDTH = 10;
    /**
     * Height of a thumbnail flag in pixel
     */
    private static final int FLAG_HEIGHT = 10;
    /**
     * Height of the text font for text below the thumbnails in pixel
     */
    private static final int FONT_HEIGHT = 10;
    /**
     * Color of the text below the thumbnails
     */
    private static final Color COLOR_TEXT = Color.lightGray;
    /**
     * Background color of this panel
     */
    private static final Color COLOR_BACKGROUND_PANEL = new Color(95, 95, 95);
    /**
     * Background color of the space between a thumbnail and it's border
     */
    private static final Color COLOR_BACKGROUND_PADDING_THUMBNAIL = new Color(
            125, 125, 125);
    /**
     * Color of the border surrounding the thumbnails
     */
    private static final Color COLOR_BORDER_THUMBNAIL = new Color(255, 255, 255);
    /**
     * Color of the text below the thumbnails of a higlighted thumbnail. Depends
     * on {@link #COLOR_BACKGROUND_PADDING_THUMBNAIL_HIGHLIGHTED}
     */
    private static final Color COLOR_TEXT_HIGHLIGHTED = new Color(255, 255, 168);
    /**
     * Color of the background surrounding a highlighted thumbnail. When
     * changing, look for {@link #COLOR_TEXT_HIGHLIGHTED}.
     */
    private static final Color COLOR_BACKGROUND_PADDING_THUMBNAIL_HIGHLIGHTED = new Color(
            245, 245, 245);
    /**
     * Font of the text below the thumbnails
     */
    private static final Font FONT = new Font("Arial", Font.PLAIN, FONT_HEIGHT);  // NOI18N
    /**
     * Maximimum character count of the text below the thumbnails when the
     * width of a thumbnail is 150 pixels
     */
    private static final int MAX_CHAR_COUNT_PER_150_PX = 25;
    /**
     * Empty space surrounding a thumbnail outside it's border in pixel
     */
    private static final int MARGIN_THUMBNAIL = FONT_HEIGHT + 10;
    /**
     * Empty space surrounding a thumbnail within the border (space between
     * the thumbnail's image and the border) in pixel
     */
    private static final int PADDING_THUMBNIAL = 10;
    /**
     * Width of the border surrounding the thumbnails in pixel
     */
    private static final int WIDHT_BORDER_THUMBNAIL = 1;
    /**
     * Contains the flags of thumbnails at specific indices
     */
    private final Map<Integer, ThumbnailFlag> flagOfThumbnail =
            new HashMap<Integer, ThumbnailFlag>();
    /**
     * Caches the thumbnails at specific indices
     */
    private final Map<Integer, Image> thumbnailAtIndex =
            new HashMap<Integer, Image>();
    /**
     * Listens to thumbnail events
     */
    private final List<ThumbnailsPanelListener> panelListeners =
            new ArrayList<ThumbnailsPanelListener>();
    /**
     * Contains the indices of the selected thumbnails
     */
    private List<Integer> selectedThumbnails = new ArrayList<Integer>();
    /**
     * Monitors all accesses to all fields that represent the state of
     * the displayed thumbnails
     */
    //private final Object thumbnailsMonitor = new Object();
    /**
     * The viewport of this
     */
    private JViewport viewport;
    /**
     * Maximum character count of the text below a thumbnail
     */
    private int maxCharCountText = 25;
    /**
     * Width of a thumbnail
     */
    private int thumbnailWidth = 0;
    /**
     * Total count of thumbnails
     */
    private int thumbnailCount = 0;
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

    public ThumbnailsPanel() {
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setBackground(COLOR_BACKGROUND_PANEL);
        addComponentListener(this);
    }

    private void empty() {
        thumbnailAtIndex.clear();
        clearSelection();
        flagOfThumbnail.clear();
        System.gc();
    }

    private void clearSelection() {
        int selectionCount = selectedThumbnails.size();
        if (selectionCount > 0) {
            selectedThumbnails.clear();
            notifyAllThumbnailsDeselected();
        }
    }

    private int getFirstSelectedIndex() {
        if (selectedThumbnails.size() > 0) {
            return selectedThumbnails.get(0);
        }
        return -1;
    }

    private int getSelectedIndex() {
        int indexSelectedThumbnail = -1;
        if (selectedThumbnails.size() == 1) {
            indexSelectedThumbnail = selectedThumbnails.get(0);
        }
        return indexSelectedThumbnail;
    }

    /**
     * Sets the width of a thumbnail.
     * 
     * @param width  width in pixel
     */
    public void setThumbnailWidth(int width) {
        if (width != thumbnailWidth) {
            thumbnailAtIndex.clear();
            System.gc();
            thumbnailWidth = width;
            setCountPerRow();
            maxCharCountText = (int) (((double) MAX_CHAR_COUNT_PER_150_PX *
                    (double) width / 150.0));
            repaint();
        }
    }

    /**
     * Enables the Drag gesture whithin the thumbnails panel. Whitout calling
     * this, {@link #handleMouseDragged(java.awt.event.MouseEvent)} will never called.
     * 
     * @param enabled true if enabled. Default: false
     */
    public void setDragEnabled(boolean enabled) {
        dragEnabled = enabled;
    }

    private boolean isValidIndex(int thumbnailIndex) {
        return thumbnailIndex >= 0 && thumbnailIndex < thumbnailCount;
    }

    public int getThumbnailWidth() {
        return thumbnailWidth;
    }

    /**
     * Liefert die Indexe aller selektierten Thumbnails.
     * 
     * @return Indexe
     */
    public List<Integer> getSelected() {
        return selectedThumbnails;
    }

    /**
     * Setzt die Indexe selektierter Thumbnails.
     * 
     * @param indices Indexe
     */
    public void setSelected(List<Integer> indices) {
        selectedThumbnails = indices;
        repaint();
        if (indices.size() > 0) {
            Collections.sort(selectedThumbnails);
            notifyThumbnailSelected();
        }
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

    /**
     * Liefert ein bestimmtes Thumbnail.
     * 
     * @param index Index
     * @return      Thumbnail. Null bei ungültigem Index.
     */
    public abstract Image getThumbnail(int index);

    /**
     * Liefert den Text für ein bestimmtes Thumbnail.
     * 
     * @param index Thumbnailindex
     * @return      Titel
     */
    protected abstract String getText(int index);

    /**
     * Ein Popupmenü soll angezeigt werden. Per Default wird nichts übernommen.
     * Spezialisierte Klassen können diese Operation überschreiben.
     * 
     * @param e Mausereignis
     */
    protected void showPopupMenu(MouseEvent e) {
    }

    /**
     * Setzt neue Thumbnails.
     * 
     * @param count Anzahl
     */
    protected void setNewThumbnails(int count) {
        empty();
        thumbnailCount = count;
        forceRepaint();
        notifyThumbnailsChanged();
    }

    /**
     * Unternimmt nichts, Nachkommen können auf einen Doppelklick reagieren.
     * 
     * @param index Index des angeklickten Thumbnails.
     */
    protected void doubleClickAt(int index) {
    }

    /**
     * Unternimmt nichts, Nachkommen können ein Tooltip anzeigen
     * 
     * @param evt Mausereignis
     */
    protected void showToolTip(MouseEvent evt) {
    }

    /**
     * Setzt ein Thumbnail an einem bestimmten Index (neu). Der Cache wird mit
     * diesem Thumbnail aktualisiert.
     * 
     * @param index     Index
     * @param thumbnail Thumbnail
     */
    public void set(int index, Image thumbnail) {
        thumbnailAtIndex.put(index, thumbnail);
        repaint();
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
    public void addFlag(int index, ThumbnailFlag flag) {
        flagOfThumbnail.put(index, flag);
    }

    /**
     * Liefert, ob ein Thumbnail ein Flag hat.
     * 
     * @param index Thumbnailindex
     * @return      true, wenn das Thumbnail ein Flag hat
     */
    public boolean isFlagged(int index) {
        return flagOfThumbnail.containsKey(index);
    }

    /**
     * Liefert das Flag eines Thumbnails.
     * 
     * @param index Thumbnailindex
     * @return      Flag oder null, wenn das Thumbnail kein Flag hat
     */
    public ThumbnailFlag getFlag(int index) {
        return flagOfThumbnail.get(index);
    }

    private int getCountHoricontalLeftFromX(int x) {
        return x / (getThumbnailAreaWidth() + MARGIN_THUMBNAIL);
    }

    private int getCountVerticalAboveY(int y) {
        return y / (getThumbnailAreaHeight() + MARGIN_THUMBNAIL);
    }

    private boolean isThumbnailAreaInWidth(int x) {
        int startExtPadding = getCountHoricontalLeftFromX(x) *
                (getThumbnailAreaWidth() + MARGIN_THUMBNAIL);
        int endExtPadding = startExtPadding + MARGIN_THUMBNAIL;
        return x < startExtPadding || (x > endExtPadding &&
                endExtPadding + getThumbnailAreaWidth() + MARGIN_THUMBNAIL <=
                getWidth());
    }

    private boolean isThumbnailAreaInHeight(int y) {
        int startExtPadding = getCountVerticalAboveY(y) *
                (getThumbnailAreaHeight() + MARGIN_THUMBNAIL);
        int endExtPadding = startExtPadding + MARGIN_THUMBNAIL;
        return y < startExtPadding || y > endExtPadding;
    }

    private boolean isThumbnailArea(int x, int y) {
        return isThumbnailAreaInWidth(x) && isThumbnailAreaInHeight(y);
    }

    protected int getIndexAtPoint(int x, int y) {
        if (isThumbnailArea(x, y)) {
            int tnOffset = (x - MARGIN_THUMBNAIL) / (getThumbnailAreaWidth() +
                    MARGIN_THUMBNAIL);
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
                (getThumbnailAreaHeight() + MARGIN_THUMBNAIL) + 0.5);
    }

    private Image getCashedThumbnail(int index) {
        Image thumbnail = null;
        if (thumbnailAtIndex.containsKey(index)) {
            thumbnail = thumbnailAtIndex.get(index);
        } else {
            thumbnail = getThumbnail(index);
            if (thumbnail != null) {
                thumbnailAtIndex.put(index, thumbnail);
            }
        }
        return thumbnail;
    }

    /**
     * Liefert, ob ein Thumbnail mit bestimmtem Index selektiert ist.
     * 
     * @param index Thumbnailindex
     * @return      true, wenn das Thumbnail selektiert ist
     */
    public boolean isSelected(int index) {
        return selectedThumbnails.contains(index);
    }

    /**
     * Liefert die Anzahl der selektierten Thumbnails.
     * 
     * @return Anzahl
     */
    public int getSelectionCount() {
        return selectedThumbnails.size();
    }

    private int getThumbnailAreaHeight() {
        return thumbnailWidth + 2 * PADDING_THUMBNIAL + 2 *
                WIDHT_BORDER_THUMBNAIL;
    }

    private int getThumbnailAreaWidth() {
        return thumbnailWidth + 2 * PADDING_THUMBNIAL + 2 *
                WIDHT_BORDER_THUMBNAIL;
    }

    private int getThumbnailLeftIndent(Image thumbnail) {
        int indentLeft = (int) ((double) ((getThumbnailAreaWidth() - thumbnail.
                getWidth(this))) / 2.0 + 0.5);
        if (indentLeft < 0) {
            indentLeft = 0;
        }
        return indentLeft;
    }

    private int getThumbnailTopIndent(Image thumbnail) {
        int indentTop = (int) ((double) ((getThumbnailAreaHeight() - thumbnail.
                getHeight(this))) / 2.0 + 0.5);
        if (indentTop < 0) {
            indentTop = 0;
        }
        return indentTop;
    }

    private void handleKeyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_RIGHT) {
            setSelectedNext();
        } else if (keyCode == KeyEvent.VK_LEFT) {
            setSelectedPrevious();
        } else if (keyCode == KeyEvent.VK_UP) {
            setSelectedUp();
        } else if (keyCode == KeyEvent.VK_DOWN) {
            setSelectedDown();
        } else if (keyCode == KeyEvent.VK_ENTER) {
            handleMouseDoubleKlicked();
        } else if ((e.getModifiers() & KeyEvent.CTRL_MASK) ==
                KeyEvent.CTRL_MASK && keyCode == KeyEvent.VK_A) {
            setSelectedAll(true);
        } else if (keyCode == KeyEvent.VK_HOME) {
            scrollToTop(true);
        } else if (keyCode == KeyEvent.VK_END) {
            scrollToBottom();
        }
    }

    private boolean isIndex(int index) {
        return index >= 0 && index < thumbnailCount;
    }

    protected int getDropIndex(int x, int y) {
        int maxBottom = MARGIN_THUMBNAIL + getRowCount() *
                getThumbnailAreaHeight();
        int maxRight = MARGIN_THUMBNAIL + getColumnCount() *
                getThumbnailAreaWidth();
        boolean inTnArea = x < maxRight && y < maxBottom;
        boolean xIsOut = x > maxRight;
        boolean yIsOut = y > maxBottom;
        return inTnArea
               ? (y - MARGIN_THUMBNAIL) / getThumbnailAreaHeight() *
                getColumnCount() +
                (x - MARGIN_THUMBNAIL) / getThumbnailAreaWidth()
               : xIsOut && !yIsOut
                 ? (y - MARGIN_THUMBNAIL) / getThumbnailAreaHeight() *
                getColumnCount() +
                getColumnCount() - 1
                 : yIsOut
                   ? thumbnailCount - 1
                   : -1;
    }

    private int getColumnCount() {
        int width = getWidth();
        int tnWidth = getThumbnailAreaWidth();
        int count = (int) ((double) (width - MARGIN_THUMBNAIL) /
                (double) tnWidth);
        return count > thumbnailCount
               ? thumbnailCount
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
        int previousSelectionCount = selectedThumbnails.size();
        selectedThumbnails.clear();
        if (select) {
            for (int index = 0; index < thumbnailCount; index++) {
                selectedThumbnails.add(index);
            }
            if (selectedThumbnails.size() > 0) {
                notifyThumbnailSelected();
            }
        } else {
            if (previousSelectionCount > 0) {
                notifyAllThumbnailsDeselected();
            }
        }
        repaint();
    }

    private void enhanceSelectionTo(int index) {
        if (getSelectionCount() <= 0) {
            setSelected(index);
        } else {
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
            if (selectedThumbnails.size() > 0) {
                notifyThumbnailSelected();
            }
            repaint();
        }
    }

    private void setSelected(int index) {
        selectedThumbnails.clear();
        selectedThumbnails.add(index);
        notifyThumbnailSelected();
        repaint();
    }

    private void addToSelection(int index) {
        if (!isSelected(index)) {
            selectedThumbnails.add(index);
            Collections.sort(selectedThumbnails);
            notifyThumbnailSelected();
            repaint();
        }
    }

    private void removeSelection(int index) {
        if (isSelected(index)) {
            selectedThumbnails.remove(new Integer(index));
            repaint();
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

    private void forceRepaint() {
        invalidate();
        validate();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        paintPanelBackground(g);
        if (thumbnailCount > 0) {
            Rectangle rectClip = g.getClipBounds();
            int firstIndex = getFirstPaintIndexAtHeight(rectClip.y);
            int lastIndex = getLastPaintIndexAtHeight(rectClip.y +
                    rectClip.height);
            g.setFont(FONT);
            for (int index = firstIndex; index < lastIndex && index <
                    thumbnailCount; index++) {
                paintThumbnail(index, g);
            }
            paintPanelFocusBorder(g);
        }
    }

    private void paintPanelBackground(Graphics g) {
        Color oldColor = g.getColor();
        g.setColor(COLOR_BACKGROUND_PANEL);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(oldColor);
    }

    private void paintThumbnail(int index, Graphics g) {
        int rowIndex = getRowIndexAt(index);
        int columnIndex = getColumnIndexAt(index);
        int areaX = MARGIN_THUMBNAIL + columnIndex * (getThumbnailAreaWidth() +
                MARGIN_THUMBNAIL);
        int areaY = MARGIN_THUMBNAIL + rowIndex * (getThumbnailAreaHeight() +
                MARGIN_THUMBNAIL);
        Image thumbnail = getCashedThumbnail(index);

        paintThumbnailBackground(g, areaX, areaY, isSelected(index));
        paintThumbnailFlag(index, g, areaX, areaY);
        if (thumbnail != null) {
            paintThumbnail(getScaledInstance(index, thumbnail), g, areaX, areaY);
            paintThumbnailTextAt(g, index, areaX, areaY);
        }
    }

    private void paintThumbnailBackground(Graphics g, int thumbnailAreaX,
            int thumbnailAreaY, boolean isSelected) {
        Color backgroundColor = isSelected
                                ? COLOR_BACKGROUND_PADDING_THUMBNAIL_HIGHLIGHTED
                                : COLOR_BACKGROUND_PADDING_THUMBNAIL;
        Color oldColor = g.getColor();
        g.setColor(backgroundColor);
        g.fillRect(
                thumbnailAreaX + 1,
                thumbnailAreaY + 1,
                thumbnailWidth + 2 * PADDING_THUMBNIAL,
                thumbnailWidth + 2 * PADDING_THUMBNIAL);
        g.setColor(COLOR_BORDER_THUMBNAIL);
        g.drawRect(
                thumbnailAreaX,
                thumbnailAreaY,
                thumbnailWidth + 2 * PADDING_THUMBNIAL + WIDHT_BORDER_THUMBNAIL,
                thumbnailWidth + 2 * PADDING_THUMBNIAL + 2);
        g.setColor(oldColor);
    }

    private void paintThumbnailFlag(int index, Graphics g, int thumbnailAreaX,
            int thumbnailAreaY) {
        ThumbnailFlag flag = getFlag(index);
        if (flag != null) {
            Color oldColor = g.getColor();
            g.setColor(flag.getColor());
            g.fillRect(
                    thumbnailAreaX + thumbnailWidth + 2 * PADDING_THUMBNIAL -
                    FLAG_WIDTH,
                    thumbnailAreaY + thumbnailWidth + 2 * PADDING_THUMBNIAL -
                    FLAG_HEIGHT,
                    FLAG_WIDTH,
                    FLAG_HEIGHT);
            g.setColor(oldColor);
        }
    }

    private void paintThumbnail(Image thumbnail, Graphics g, int areaX,
            int areaY) {
        int indentTop = getThumbnailTopIndent(thumbnail);
        int indentLeft = getThumbnailLeftIndent(thumbnail);

        g.drawImage(thumbnail, areaX + indentLeft, areaY + indentTop, this);
    }

    private void paintThumbnailTextAt(Graphics g, int index,
            int areaX, int areaY) {
        String text = getFormattedText(index);
        int width = getThumbnailAreaWidth();
        int lenTitle = g.getFontMetrics().stringWidth(text);
        int space = width - lenTitle;
        int xText =
                space > 0
                ? areaX + (int) ((double) space / 2.0 + 0.5)
                : areaX;

        Color oldColor = g.getColor();
        g.setColor(isSelected(index)
                   ? COLOR_TEXT_HIGHLIGHTED
                   : COLOR_TEXT);
        g.drawString(text, xText, areaY + getThumbnailAreaHeight() +
                FONT_HEIGHT + 4);
        g.setColor(oldColor);
    }

    private String getFormattedText(int index) {
        String text = getText(index);
        int charCountText = text.length();

        if (charCountText <= maxCharCountText) {
            return text;
        }

        return "..." + text.substring(text.length() - maxCharCountText + 2); // NOI18N
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
        double count = (double) thumbnailCount / (double) thumbnailCountPerRow;
        return thumbnailCount > thumbnailCountPerRow
               ? (int) (MathUtil.isInteger(count)
                        ? count
                        : count + 1)
               : thumbnailCount == 0
                 ? 0
                 : 1;
    }

    private void notifyAllThumbnailsDeselected() {
        synchronized (panelListeners) {
            for (ThumbnailsPanelListener listener : panelListeners) {
                listener.selectionChanged(
                        new ThumbnailsPanelAction(-1, -1, -1, this));
            }
        }
    }

    private void notifyThumbnailSelected() {
        synchronized (panelListeners) {
            for (ThumbnailsPanelListener listener : panelListeners) {
                listener.selectionChanged(new ThumbnailsPanelAction(
                        getSelectedIndex(),
                        getXSelectedThumbnail(),
                        getYSelectedThumbnail(),
                        this));
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
                getRowCount() * (getThumbnailAreaHeight() + MARGIN_THUMBNAIL);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        setCountPerRow();
    }

    private void setCountPerRow() {
        int width = getWidth();
        int tnAreaWidth = getThumbnailAreaWidth();
        int prevCount = thumbnailCountPerRow;
        double count =
                (double) (width - MARGIN_THUMBNAIL) /
                (double) (tnAreaWidth + MARGIN_THUMBNAIL);
        thumbnailCountPerRow = count >= 1
                               ? (int) count
                               : 1;
        if (prevCount != thumbnailCountPerRow) {
            setSize(getWidth(), getCalculatedHeight());
        }
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
            notifyThumbnailSelected();
        }
    }

    private void setSelectedDown() {
        int indexSelectedThumbnail = getSelectedIndex();
        int indexToSelect = indexSelectedThumbnail + thumbnailCountPerRow;
        if (indexSelectedThumbnail >= 0 && isIndex(indexToSelect)) {
            setSelected(indexToSelect);
            notifyThumbnailSelected();
        }
    }

    private void setSelectedNext() {
        int indexSelectedThumbnail = getSelectedIndex();
        int indexToSelect = (indexSelectedThumbnail + 1) % thumbnailCount;
        if (indexSelectedThumbnail >= 0 && isIndex(indexToSelect)) {
            setSelected(indexToSelect);
            notifyThumbnailSelected();
        }
    }

    private void setSelectedPrevious() {
        int indexSelectedThumbnail = getSelectedIndex();
        int indexToSelect = (indexSelectedThumbnail - 1) % thumbnailCount;
        if (indexSelectedThumbnail >= 0 && isIndex(indexToSelect)) {
            setSelected(indexToSelect);
            notifyThumbnailSelected();
        }
    }

    private int getXSelectedThumbnail() {
        int indexSelectedThumbnail = getSelectedIndex();
        if (indexSelectedThumbnail >= 0) {
            int tnWidth = getThumbnailAreaWidth() + MARGIN_THUMBNAIL;
            int columnIndex = getColumnIndexAt(
                    indexSelectedThumbnail);
            return tnWidth * columnIndex;
        }
        return -1;
    }

    private int getYSelectedThumbnail() {
        int indexSelectedThumbnail = getSelectedIndex();
        if (indexSelectedThumbnail >= 0) {
            int tnHeight = getThumbnailAreaHeight() + MARGIN_THUMBNAIL;
            int rowIndex = getRowIndexAt(indexSelectedThumbnail);
            return tnHeight * rowIndex;
        }
        return -1;
    }

    /**
     * Returns an image with a longer side that fits the attribute thumbnailWidth.
     * 
     * @param  cacheIndex  index of the image in the cache. If the image will be
     *                     scaled the cache will be refreshed with the scaled
     *                     instance
     * @param  image       image to scale
     * @return image if it must not be scaled or scaled instance
     */
    private Image getScaledInstance(int cacheIndex, Image image) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        double longer = width > height
                        ? width
                        : height;
        if (longer == thumbnailWidth) {
            return image;
        }
        double scaleFactor = (double) thumbnailWidth / longer;

        Image scaled = image.getScaledInstance(
                width > height
                ? thumbnailWidth
                : (int) ((double) width * scaleFactor + 0.5),
                height > width
                ? thumbnailWidth
                : (int) ((double) height * scaleFactor + 0.5),
                Image.SCALE_AREA_AVERAGING);
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(image, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException ex) {
            AppLog.logWarning(ThumbnailsPanel.class, ex);
        }
        thumbnailAtIndex.put(cacheIndex, scaled);
        return scaled;
    }

    /**
     * Entfernt aus dem internen Bildcache Thumbnails und liest sie
     * bei Bedarf - wenn sie gezeichnet werden müssen - erneut ein.
     * 
     * @param thumbnailIndices Indexe
     */
    protected void removeFromCache(ArrayList<Integer> thumbnailIndices) {
        for (Integer index : thumbnailIndices) {
            thumbnailAtIndex.remove(index);
        }
        repaint();
    }

    /**
     * Entfernt aus dem internen Bildcache ein Thumbnail und liest es
     * bei Bedarf - wenn es gezeichnet werden muss - erneut ein.
     * 
     * @param index Index
     */
    protected void removeFromCache(int index) {
        thumbnailAtIndex.remove(index);
        repaint();
    }

    /**
     * Sets the viewport. Have to be called before adding files.
     * If a viewport ist set, some additional functions supported, e.g.
     * special keyboard keys that are not handled through the viewport
     * and a scroll pane.
     * 
     * @param viewport  Viewport
     */
    public void setViewport(JViewport viewport) {
        this.viewport = viewport;
    }

    /**
     * Returns the viewport within this panel is displayed.
     * 
     * @return viewport or null if not set or if the panel is not in a viewport
     */
    public JViewport getViewport() {
        return viewport;
    }

    protected void scrollToTop(boolean scroll) {
        if (scroll && viewport != null) {
            viewport.setViewPosition(new Point(0, 0));
        }
    }

    protected void scrollToBottom() {
        if (viewport != null) {
            viewport.setViewPosition(new Point(0, getHeight()));
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
        handleKeyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public boolean isFocusable() {
        return true;
    }
}
