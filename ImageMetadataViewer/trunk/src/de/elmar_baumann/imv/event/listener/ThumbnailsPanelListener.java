package de.elmar_baumann.imv.event.listener;

import de.elmar_baumann.imv.event.ThumbnailsPanelEvent;

/**
 * Listens to {@link de.elmar_baumann.imv.view.panels.ThumbnailsPanel}.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public interface ThumbnailsPanelListener {

    /**
     * Thumbnail were selected or deselected.
     * 
     * @param action  action
     */
    public void selectionChanged(ThumbnailsPanelEvent action);

    /**
     * Count and/or order of the thumbnails changed.
     */
    public void thumbnailsChanged();
}
