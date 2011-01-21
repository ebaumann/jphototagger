package org.jphototagger.program.event.listener;

/**
 * Listens to {@link org.jphototagger.program.view.panels.ThumbnailsPanel}.
 *
 * @author Elmar Baumann
 */
public interface ThumbnailsPanelListener {

    /**
     * Thumbnails were selected or deselected.
     */
    void thumbnailsSelectionChanged();

    /**
     * Count and/or order of the thumbnails changed.
     */
    void thumbnailsChanged();
}
