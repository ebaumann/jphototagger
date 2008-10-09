package de.elmar_baumann.imv.event;

/**
 * Beobachtet Ereignisse am Thumbnailspanel.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/20
 * @see     de.elmar_baumann.imagemetadataviewer.view.panels.ThumbnailsPanel
 */
public interface ThumbnailsPanelListener {

    /**
     * Das Thumbnail wurde selektiert.
     * 
     * @param action Aktion
     */
    public void thumbnailSelected(ThumbnailsPanelAction action);

    /**
     * Alle Thumbnails wurden deselektiert.
     * 
     * @param action Aktion
     */
    public void allThumbnailsDeselected(ThumbnailsPanelAction action);

    /**
     * Die Anzahl der anzuzeigenden Thumbnails hat sich verändert.
     */
    public void thumbnailCountChanged();
}
