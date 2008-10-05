package de.elmar_baumann.imagemetadataviewer.view.panels;

import de.elmar_baumann.imagemetadataviewer.event.ThumbnailsPanelAction;
import de.elmar_baumann.imagemetadataviewer.event.ThumbnailsPanelListener;
import de.elmar_baumann.imagemetadataviewer.data.ThumbnailFlag;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JPanel;

/**
 * Panel zum Anzeigen mehrerer Thumbnails mit einem Text darunter. Diese Klasse
 * übernimmt das Zeichnen und reagiert auf Ereignisse. Eine spezialisierte
 * Klasse liefert die Thumbnails sowie den Text, der unter die Thumbnails
 * gezeichnet werden soll.
 * 
 * Die Größe eines Thumbnails ist vorerst festgelegt auf eine bestimmte Breite
 * und Höhe (<code>defaultThumbnailWidth</code> und
 * <code>defaultThumbnailHeight</code>). Wird im Verlauf des Renderns ein
 * größeres gefunden, werden Breite und/oder Höhe auf dessen Abmessungen gesetzt.
 * 
 * Jedes Thumbnail ist eingebettet in eine Fläche, die definiert ist durch
 * ein "internes Polster" (<code>internalPadding</code>). Um diese wird ein Rand
 * gezeichnet mit einer bestimmten Dicke (<code>thumbnailBorderWidth</code>).
 * 
 * Der Abstand zwischen den Thumbnails wird als "externes Polster" bezeichnet
 * (<code>externalPadding</code>).
 * 
 * Jedes Thumbnail wird gecached und es werden nur Thumbnails gezeichnet, die
 * sich innerhalb der Clip-Bounds befinden.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/19
 */
public abstract class ThumbnailsPanel extends JPanel
    implements MouseListener, MouseMotionListener, KeyListener {

    private static final int lengthFlag = 10;
    private static final int heightFlag = 10;
    private static final int fontHeightThumbnailText = 10;
    private static final Color colorText = Color.lightGray;
    private static final Color colorPanelBackground = new Color(95, 95, 95);
    private static final Color colorThumbnailBackground = new Color(125, 125, 125);
    private static final Color colorThumbnailBorder = new Color(255, 255, 255);
    private static final Color colorHighlightedText = new Color(255, 255, 168);
    private static final Color colorHighlightedBackground = new Color(245, 245, 245);
    private static final Font fontTitle = new Font("Arial", Font.PLAIN, fontHeightThumbnailText);  // NOI18N
    private static final int maxCharCountText = 25;
    private static final int defaultThumbnailWidth = 150;
    private static final int defaultThumbnailHeight = 150;
    private static final int externalPadding = fontHeightThumbnailText + 10;
    private static final int internalPadding = 10;
    private static final int thumbnailBorderWidth = 1;
    private int thumbnailWidth = defaultThumbnailWidth;
    private int thumbnailHeight = defaultThumbnailHeight;
    private int thumbnailCount = 0;
    private int thumbnailCountPerRow = 3;
    private ArrayList<Integer> indicesSelectedThumbnails = new ArrayList<Integer>();
    private HashMap<Integer, ThumbnailFlag> flagOfThumbnailIndex = new HashMap<Integer, ThumbnailFlag>();
    private HashMap<Integer, Image> thumbnailAtIndex = new HashMap<Integer, Image>();
    private ArrayList<ThumbnailsPanelListener> panelListener = new ArrayList<ThumbnailsPanelListener>();

    protected void empty() {
        thumbnailAtIndex.clear();
        indicesSelectedThumbnails.clear();
        flagOfThumbnailIndex.clear();
        System.gc();
        thumbnailCount = 0;
        thumbnailWidth = defaultThumbnailWidth;
        thumbnailHeight = defaultThumbnailHeight;
        notifyAllThumbnailsDeselected();
    }

    public ThumbnailsPanel() {
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setBackground(colorPanelBackground);
    }

    private int getIndexSelectedThumbnail() {
        int indexSelectedThumbnail = -1;
        if (indicesSelectedThumbnails.size() == 1) {
            indexSelectedThumbnail = indicesSelectedThumbnails.get(0);
        }
        return indexSelectedThumbnail;
    }

    /**
     * Liefert die Indexe aller selektierten Thumbnails.
     * 
     * @return Indexe
     */
    public ArrayList<Integer> getIndicesSelectedThumbnails() {
        return indicesSelectedThumbnails;
    }

    /**
     * Setzt die Indexe selektierter Thumbnails.
     * 
     * @param indices Indexe
     */
    protected void setIndicesSelectedThumbnails(ArrayList<Integer> indices) {
        indicesSelectedThumbnails = indices;
        repaint();
        notifyThumbnailSelected();
    }

    /**
     * Fügt einen Beobachter hinzu.
     * 
     * @param listener Beobachter
     */
    public void addThumbnailsPanelListener(ThumbnailsPanelListener listener) {
        panelListener.add(listener);
    }

    /**
     * Entfernt einen Beobachter.
     * 
     * @param listener Beobachter
     */
    public void removeThumbnailsPanelListener(ThumbnailsPanelListener listener) {
        panelListener.remove(listener);
    }

    /**
     * Liefert ein bestimmtes Thumbnail.
     * 
     * @param index Index
     * @return      Thumbnail. Null bei ungültigem Index.
     */
    public abstract Image getThumbnailAtIndex(int index);

    /**
     * Liefert den Text für ein bestimmtes Thumbnail.
     * 
     * @param index Thumbnailindex
     * @return      Titel
     */
    protected abstract String getTextForThumbnailAtIndex(int index);

    /**
     * Ein Popupmenü soll angezeigt werden. Per Default wird nichts übernommen.
     * Spezialisierte Klassen können diese Operation überschreiben.
     * 
     * @param e Mausereignis
     */
    protected void showPopupMenu(MouseEvent e) {
    }

    /**
     * Setzt die Anzahl der anzuzeigenden Thumbnails.
     * 
     * @param count Anzahl
     */
    protected void setThumbnailCount(int count) {
        thumbnailCount = count;
        notifyThumbnailsCountChanged();
    }

    /**
     * Unternimmt nichts, Nachkommen können auf einen Doppelklick reagieren.
     * 
     * @param index Index des angeklickten Thumbnails.
     */
    protected void doubleClickAtIndex(int index) {
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
    public void setThumbnailAtIndex(int index, Image thumbnail) {
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
    public void addThumbnailFlag(int index, ThumbnailFlag flag) {
        flagOfThumbnailIndex.put(index, flag);
    }

    /**
     * Liefert, ob ein Thumbnail ein Flag hat.
     * 
     * @param index Thumbnailindex
     * @return      true, wenn das Thumbnail ein Flag hat
     */
    public boolean isFlagged(int index) {
        return flagOfThumbnailIndex.containsKey(index);
    }

    /**
     * Liefert das Flag eines Thumbnails.
     * 
     * @param index Thumbnailindex
     * @return      Flag oder null, wenn das Thumbnail kein Flag hat
     */
    public ThumbnailFlag getFlagOfThumbnail(int index) {
        return flagOfThumbnailIndex.get(index);
    }

    private void checkPopupMenu(MouseEvent e) {
        if (e.isPopupTrigger()) {
            showPopupMenu(e, getThumbnailIndexAtPoint(e.getX(), e.getY()));
        }
    }

    private int getImageCountHoricontalLeftFromX(int x) {
        return x / (getThumbnailAreaWidth() + externalPadding);
    }

    private int getImageCountVerticalAboveY(int y) {
        return y / (getThumbnailAreaHeight() + externalPadding);
    }

    private boolean isThumbnailAreaInWidth(int x) {
        int startExtPadding = getImageCountHoricontalLeftFromX(x) *
            (getThumbnailAreaWidth() + externalPadding);
        int endExtPadding = startExtPadding + externalPadding;
        return x < startExtPadding || x > endExtPadding;
    }

    private boolean isThumbnailAreaInHeight(int y) {
        int startExtPadding = getImageCountVerticalAboveY(y) *
            (getThumbnailAreaHeight() + externalPadding);
        int endExtPadding = startExtPadding + externalPadding;
        return y < startExtPadding || y > endExtPadding;
    }

    private boolean isThumbnailArea(int x, int y) {
        return isThumbnailAreaInWidth(x) && isThumbnailAreaInHeight(y);
    }

    protected int getThumbnailIndexAtPoint(int x, int y) {
        if (isThumbnailArea(x, y)) {
            int tnOffset = (x - externalPadding) / (getThumbnailAreaWidth() +
                externalPadding);
            int firstInRow = getFirstPaintThumbnialIndexAtHeight(y);
            return firstInRow + tnOffset;
        }
        return -1;
    }

    private int getFirstPaintThumbnialIndexAtHeight(int height) {
        int rowsToStart = getRowcountInHeight(height);
        return rowsToStart * thumbnailCountPerRow;
    }

    private int getLastPaintThumbnailIndexAtHeight(int height) {
        int rowsToEnd = getRowcountInHeight(height);
        return (rowsToEnd + 1) * thumbnailCountPerRow;
    }

    private int getRowcountInHeight(int height) {
        return (int) ((height - externalPadding) /
            (getThumbnailAreaHeight() + externalPadding) + 0.5);
    }

    private Image getCashedThumbnailAtIndex(int index) {
        Image thumbnail = null;
        if (thumbnailAtIndex.containsKey(index)) {
            thumbnail = thumbnailAtIndex.get(index);
        } else {
            thumbnail = getThumbnailAtIndex(index);
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
        return indicesSelectedThumbnails.contains(index);
    }

    /**
     * Liefert die Anzahl der selektierten Thumbnails.
     * 
     * @return Anzahl
     */
    public int getSelectionCount() {
        return indicesSelectedThumbnails.size();
    }

    private int getThumbnailAreaHeight() {
        return thumbnailHeight + 2 * internalPadding + 2 * thumbnailBorderWidth;
    }

    private int getThumbnailAreaWidth() {
        return thumbnailWidth + 2 * internalPadding + 2 * thumbnailBorderWidth;
    }

    private int getThumbnailLeftIndent(Image thumbnail) {
        int indentLeft = (int) ((double) ((getThumbnailAreaWidth() - thumbnail.getWidth(this))) / 2.0 + 0.5);
        if (indentLeft < 0) {
            indentLeft = 0;
        }
        return indentLeft;
    }

    private int getThumbnailTopIndent(Image thumbnail) {
        int indentTop = (int) ((double) ((getThumbnailAreaHeight() - thumbnail.getHeight(this))) / 2.0 + 0.5);
        if (indentTop < 0) {
            indentTop = 0;
        }
        return indentTop;
    }

    private void handleKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            selectNextThumbnail();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            selectPreviousThumbnail();
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            selectUpThumbnail();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            selectDownThumbnail();
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            invokeDoubleKlick();
        } else if ((e.getModifiers() & KeyEvent.CTRL_MASK) ==
            KeyEvent.CTRL_MASK && e.getKeyCode() == KeyEvent.VK_A) {
            selectAllThumbnails(true);
        }
    }

    private void handleMouseClicked(MouseEvent e) {
        boolean isLeftClick = isLeftClick(e);
        if (isLeftClick && !hasFocus()) {
            requestFocus();
        }
        if (isLeftClick) {
            int thumbnailIndex = getThumbnailIndexAtPoint(e.getX(), e.getY());
            if (thumbnailIndex >= 0) {
                if (isDoubleClick(e)) {
                    doubleClickAtIndex(thumbnailIndex);
                    selectThumbnail(thumbnailIndex);
                    notifyThumbnailSelected();
                } else if (e.isControlDown()) {
                    if (!isSelected(thumbnailIndex)) {
                        addToThumbnailSelection(thumbnailIndex);
                    } else {
                        removeThumbnailFromSelection(thumbnailIndex);
                    }
                    notifyThumbnailSelected();
                } else if (e.isShiftDown()) {
                    selectRange(thumbnailIndex);
                    notifyThumbnailSelected();
                } else {
                    selectThumbnail(thumbnailIndex);
                    notifyThumbnailSelected();
                }
            } else {
                selectAllThumbnails(false);
            }
        } else if (getSelectionCount() > 0 && (e.isPopupTrigger() || e.getModifiers() == 4)) {
            showPopupMenu(e);
        }
    }

    private boolean isDoubleClick(MouseEvent e) {
        return e.getClickCount() >= 2;
    }

    private boolean isLeftClick(MouseEvent e) {
        return e.getButton() == MouseEvent.BUTTON1;
    }

    private boolean isThumbnailIndex(int index) {
        return index >= 0 && index < thumbnailCount;
    }

    private void selectAllThumbnails(boolean select) {
        indicesSelectedThumbnails.clear();
        if (select) {
            for (int index = 0; index < thumbnailCount; index++) {
                indicesSelectedThumbnails.add(index);
            }
        } else {
            notifyAllThumbnailsDeselected();
        }
        repaint();
    }

    private boolean isThumbnailSelected(Integer index) {
        return indicesSelectedThumbnails.contains(index);
    }

    private int getFirstSelectedIndexBefore(int index) {
        int firstIndex = -1;
        if (getSelectionCount() > 0) {
            boolean found = false;
            for (int i = index - 1; !found && i >= 0; i--) {
                found = isThumbnailSelected(i);
                if (found) {
                    firstIndex = i;
                }
            }
        }
        return firstIndex;
    }

    private int getFirstSelectedIndexAfter(int index) {
        int firstIndex = -1;
        if (getSelectionCount() > 0) {
            boolean found = false;
            for (int i = index + 1; !found && i < thumbnailCount; i++) {
                found = isThumbnailSelected(i);
                if (found) {
                    firstIndex = i;
                }
            }
        }
        return firstIndex;
    }

    private void selectRange(int thumbnailIndex) {
        int index = getFirstSelectedIndexAfter(thumbnailIndex);
        if (index == -1) {
            index = getFirstSelectedIndexBefore(thumbnailIndex);
        }
        if (index == -1) {
            selectThumbnail(thumbnailIndex);
        } else {
            indicesSelectedThumbnails.clear();
            int startIndex = index > thumbnailIndex ? thumbnailIndex : index;
            int endIndex = index > thumbnailIndex ? index : thumbnailIndex;
            for (int i = startIndex; i <= endIndex; i++) {
                indicesSelectedThumbnails.add(i);
            }
            repaint();
        }
    }

    private void selectThumbnail(int index) {
        indicesSelectedThumbnails.clear();
        indicesSelectedThumbnails.add(index);
        repaint();
    }

    private void addToThumbnailSelection(int index) {
        if (!indicesSelectedThumbnails.contains(index)) {
            indicesSelectedThumbnails.add(index);
            repaint();
        }
    }

    private void removeThumbnailFromSelection(int index) {
        if (indicesSelectedThumbnails.contains(index)) {
            indicesSelectedThumbnails.remove(new Integer(index));
            repaint();
        }
    }

    /**
     * Setzt die Anzahl der Thumbnails, die nebeneinander angezeigt werden.
     * 
     * @param count Anzahl
     */
    protected void setThumbnailCountPerRow(int count) {
        thumbnailCountPerRow = count;
        repaint();
    }

    private int getColumnIndexAtThumbnailIndex(int thumbnailIndex) {
        return thumbnailIndex % thumbnailCountPerRow;
    }

    private int getRowIndexAtThumbnailIndex(int thumbnailIndex) {
        return thumbnailIndex / thumbnailCountPerRow;
    }

    private void invokeDoubleKlick() {
        int indexSelectedThumbnail = getIndexSelectedThumbnail();
        if (indexSelectedThumbnail >= 0) {
            doubleClickAtIndex(indexSelectedThumbnail);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        paintPanelBackground(g);
        if (thumbnailCount > 0) {
            Rectangle rectClip = g.getClipBounds();
            int firstIndex = getFirstPaintThumbnialIndexAtHeight(rectClip.y);
            int lastIndex = getLastPaintThumbnailIndexAtHeight(rectClip.y + rectClip.height);
            g.setFont(fontTitle);
            for (int index = firstIndex; index < lastIndex && index < thumbnailCount; index++) {
                paintThumbnailAtIndex(index, g);
            }
            paintPanelFocusBorder(g);
            setSize(calculatePreferredSize(getPreferredSize(), rectClip));
        }
    }

    private void paintPanelBackground(Graphics g) {
        Color oldColor = g.getColor();
        g.setColor(colorPanelBackground);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(oldColor);
    }

    private void paintThumbnailAtIndex(int index, Graphics g) {
        int rowIndex = getRowIndexAtThumbnailIndex(index);
        int columnIndex = getColumnIndexAtThumbnailIndex(index);
        int areaX = externalPadding + columnIndex * (getThumbnailAreaWidth() +
            externalPadding);
        int areaY = externalPadding + rowIndex * (getThumbnailAreaHeight() +
            externalPadding);
        Image thumbnail = getCashedThumbnailAtIndex(index);

        paintThumbnailBackground(g, areaX, areaY, isSelected(index));
        paintThumbnailFlag(index, g, areaX, areaY);
        if (thumbnail != null) {
            setMaxThumbnailDimensions(thumbnail);
            paintThumbnail(thumbnail, g, areaX, areaY);
            paintThumbnailTextAtIndex(g, index, areaX, areaY);
        }
    }

    private void paintThumbnailBackground(Graphics g, int thumbnailAreaX,
        int thumbnailAreaY, boolean isSelected) {
        Color backgroundColor = isSelected
            ? colorHighlightedBackground
            : colorThumbnailBackground;
        Color oldColor = g.getColor();
        g.setColor(backgroundColor);
        g.fillRect(
            thumbnailAreaX + 1,
            thumbnailAreaY + 1,
            thumbnailWidth + 2 * internalPadding,
            thumbnailHeight + 2 * internalPadding);
        g.setColor(colorThumbnailBorder);
        g.drawRect(
            thumbnailAreaX,
            thumbnailAreaY,
            thumbnailWidth + 2 * internalPadding + thumbnailBorderWidth,
            thumbnailHeight + 2 * internalPadding + 2);
        g.setColor(oldColor);
    }

    private void paintThumbnailFlag(int index, Graphics g, int thumbnailAreaX, int thumbnailAreaY) {
        ThumbnailFlag flag = getFlagOfThumbnail(index);
        if (flag != null) {
            Color oldColor = g.getColor();
            g.setColor(flag.getColor());
            g.fillRect(
                thumbnailAreaX + thumbnailWidth + 2 * internalPadding - lengthFlag,
                thumbnailAreaY + thumbnailHeight + 2 * internalPadding - heightFlag,
                lengthFlag,
                heightFlag);
            g.setColor(oldColor);
        }
    }

    private void paintThumbnail(Image thumbnail, Graphics g, int areaX,
        int areaY) {
        int indentTop = getThumbnailTopIndent(thumbnail);
        int indentLeft = getThumbnailLeftIndent(thumbnail);

        g.drawImage(thumbnail, areaX + indentLeft, areaY + indentTop, this);
    }

    private void paintThumbnailTextAtIndex(Graphics g, int index,
        int areaX, int areaY) {
        String text = getThumbnailTextAtThumbnailIndex(index);
        int width = getThumbnailAreaWidth();
        int lenTitle = g.getFontMetrics().stringWidth(text);
        int space = width - lenTitle;
        int xText =
            space > 0 ? areaX + (int) ((double) space / 2.0 + 0.5) : areaX;

        Color oldColor = g.getColor();
        g.setColor(isSelected(index) ? colorHighlightedText : colorText);
        g.drawString(text, xText, areaY + getThumbnailAreaHeight() +
            fontHeightThumbnailText + 4);
        g.setColor(oldColor);
    }

    private String getThumbnailTextAtThumbnailIndex(int index) {
        String text = getTextForThumbnailAtIndex(index);
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
        return (int) ((double) (thumbnailCount + thumbnailCountPerRow - 1) /
            (double) thumbnailCountPerRow + 0.5);
    }

    private void notifyAllThumbnailsDeselected() {
        for (ThumbnailsPanelListener listener : panelListener) {
            listener.thumbnailSelected(new ThumbnailsPanelAction(-1, -1, -1,
                this));
        }
    }

    private void notifyThumbnailSelected() {
        for (ThumbnailsPanelListener listener : panelListener) {
            listener.thumbnailSelected(new ThumbnailsPanelAction(
                getIndexSelectedThumbnail(),
                getXSelectedThumbnail(),
                getYSelectedThumbnail(),
                this));
        }
    }

    private void notifyThumbnailsCountChanged() {
        for (ThumbnailsPanelListener listener : panelListener) {
            listener.thumbnailCountChanged();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        int width = externalPadding + thumbnailCountPerRow *
            (getThumbnailAreaWidth() + externalPadding);
        int heigth = externalPadding + getRowCount() *
            (getThumbnailAreaHeight() + externalPadding);
        return new Dimension(width, heigth);
    }

    private Dimension calculatePreferredSize(
        Dimension preferredSize, Rectangle rectClip) {
        return new Dimension(
            rectClip.width > preferredSize.width ? rectClip.width : preferredSize.width,
            rectClip.height > preferredSize.height ? rectClip.height : preferredSize.height);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    private void selectUpThumbnail() {
        int indexSelectedThumbnail = getIndexSelectedThumbnail();
        int indexToSelect = indexSelectedThumbnail - thumbnailCountPerRow;
        if (indexSelectedThumbnail >= 0 && isThumbnailIndex(indexToSelect)) {
            selectThumbnail(indexToSelect);
            notifyThumbnailSelected();
        }
    }

    private void selectDownThumbnail() {
        int indexSelectedThumbnail = getIndexSelectedThumbnail();
        int indexToSelect = indexSelectedThumbnail + thumbnailCountPerRow;
        if (indexSelectedThumbnail >= 0 && isThumbnailIndex(indexToSelect)) {
            selectThumbnail(indexToSelect);
            notifyThumbnailSelected();
        }
    }

    private void selectNextThumbnail() {
        int indexSelectedThumbnail = getIndexSelectedThumbnail();
        int indexToSelect = (indexSelectedThumbnail + 1) % thumbnailCount;
        if (indexSelectedThumbnail >= 0 && isThumbnailIndex(indexToSelect)) {
            selectThumbnail(indexToSelect);
            notifyThumbnailSelected();
        }
    }

    private void selectPreviousThumbnail() {
        int indexSelectedThumbnail = getIndexSelectedThumbnail();
        int indexToSelect = (indexSelectedThumbnail - 1) % thumbnailCount;
        if (indexSelectedThumbnail >= 0 && isThumbnailIndex(indexToSelect)) {
            selectThumbnail(indexToSelect);
            notifyThumbnailSelected();
        }
    }

    private int getXSelectedThumbnail() {
        int indexSelectedThumbnail = getIndexSelectedThumbnail();
        if (indexSelectedThumbnail >= 0) {
            int tnWidth = getThumbnailAreaWidth() + externalPadding;
            int columnIndex = getColumnIndexAtThumbnailIndex(
                indexSelectedThumbnail);
            return tnWidth * columnIndex;
        }
        return -1;
    }

    private int getYSelectedThumbnail() {
        int indexSelectedThumbnail = getIndexSelectedThumbnail();
        if (indexSelectedThumbnail >= 0) {
            int tnHeight = getThumbnailAreaHeight() + externalPadding;
            int rowIndex = getRowIndexAtThumbnailIndex(indexSelectedThumbnail);
            return tnHeight * rowIndex;
        }
        return -1;
    }

    private void setMaxThumbnailDimensions(Image thumbnail) {
        int width = thumbnail.getWidth(this);
        int height = thumbnail.getHeight(this);
        boolean repaint = false;
        if (width > thumbnailWidth) {
            thumbnailWidth = width;
            repaint = true;
        }
        if (height > thumbnailHeight) {
            thumbnailHeight = height;
            repaint = true;
        }
        if (repaint) {
            repaint();
        }
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

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        handleMouseClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        checkPopupMenu(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        showToolTip(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
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
