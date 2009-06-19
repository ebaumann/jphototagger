package de.elmar_baumann.imv.event;

import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;

/**
 * Event in a thumbnails panel.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/20
 * @see     de.elmar_baumann.imv.view.panels.ThumbnailsPanel
 */
public final class ThumbnailsPanelEvent {

    private int thumbnailIndex = -1;

    /**
     * Constructor.
     * 
     * @param thumbnailIndex Index of the affected thumbnail
     */
    public ThumbnailsPanelEvent(int thumbnailIndex) {
        this.thumbnailIndex = thumbnailIndex;
    }

    /**
     * Returns the index of the affected thumbnail.
     * 
     * @return index
     */
    public int getThumbnailIndex() {
        return thumbnailIndex;
    }
}
