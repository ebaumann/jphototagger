package de.elmar_baumann.imagemetadataviewer.event;

import de.elmar_baumann.imagemetadataviewer.view.panels.ThumbnailsPanel;

/**
 * Aktion bei einem Thumbnailspanel.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/20
 * @see     de.elmar_baumann.imagemetadataviewer.view.panels.ThumbnailsPanel
 */
public class ThumbnailsPanelAction {

    private int thumbnailIndex = -1;
    private int x = -1;
    private int y = -1;
    private ThumbnailsPanel panel = null;

    /**
     * Konstruktor.
     * 
     * @param thumbnailIndex Index des Thumbnails, bei dem die Aktion stattfand
     * @param x              x-Koordinate des rechten oberen Thumbnailecks in Pixel
     * @param y              y-Koordinate des rechten oberen Thumbnailecks in Pixel
     * @param panel          Panel mit dem Thumbnail
     */
    public ThumbnailsPanelAction(int thumbnailIndex, int x, int y,
        ThumbnailsPanel panel) {
        this.x = x;
        this.y = y;
        this.thumbnailIndex = thumbnailIndex;
        this.panel = panel;
    }

    /**
     * Liefert den Index des Thumbnails, bei dem die Aktion stattfand.
     * 
     * @return Index
     */
    public int getThumbnailIndex() {
        return thumbnailIndex;
    }

    /**
     * Liefert die x-Koordinate des rechten oberen Thumbnailecks in Pixel.
     * 
     * @return x-Koordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Liefert die y-Koordinate des rechten oberen Thumbnailecks in Pixel.
     * 
     * @return y-Koordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Liefert das Panel.
     * 
     * @return Panel
     */
    public ThumbnailsPanel getPanel() {
        return this.panel;
    }
}
