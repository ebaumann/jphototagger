package de.elmar_baumann.imv.event.listener;

/**
 * Listens to {@link de.elmar_baumann.imv.view.panels.ThumbnailsPanel}.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public interface ThumbnailsPanelListener {

    /**
     * Thumbnails were selected or deselected.
     */
    public void thumbnailsSelectionChanged();

    /**
     * Count and/or order of the thumbnails changed.
     */
    public void thumbnailsChanged();
}
