package org.jphototagger.domain.thumbnails.event;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.ThumbnailsPanelSettings;

/**
 * @author Elmar Baumann
 */
public final class ThumbnailsPanelRefreshEvent {

    private final Object source;
    private final Point currentViewPosition;
    private final OriginOfDisplayedThumbnails typeOfDisplayedImages;
    private List<Integer> selectedThumbnailIndices;

    public ThumbnailsPanelRefreshEvent(Object source, OriginOfDisplayedThumbnails typeOfDisplayedImages, Point currentViewPosition) {
        if (typeOfDisplayedImages == null) {
            throw new NullPointerException("typeOfDisplayedImages == null");
        }

        if (currentViewPosition == null) {
            throw new NullPointerException("currentViewPosition == null");
        }

        this.source = source;
        this.typeOfDisplayedImages = typeOfDisplayedImages;
        this.currentViewPosition = currentViewPosition;
    }

    public Point getCurrentViewPosition() {
        return currentViewPosition;
    }

    public Object getSource() {
        return source;
    }

    public List<Integer> getSelectedThumbnailIndices() {
        return Collections.unmodifiableList(selectedThumbnailIndices);
    }

    public void setSelectedThumbnailIndices(List<Integer> selectedThumbnailIndices) {
        if (selectedThumbnailIndices == null) {
            throw new NullPointerException("selectedThumbnailIndices == null");
        }

        this.selectedThumbnailIndices = new ArrayList<Integer>(selectedThumbnailIndices);
    }

    public boolean hasSelectedThumbnails() {
        return selectedThumbnailIndices != null;
    }

    public ThumbnailsPanelSettings getThumbnailsPanelSettings() {
        return new ThumbnailsPanelSettings(currentViewPosition, selectedThumbnailIndices);
    }

    public OriginOfDisplayedThumbnails getTypeOfDisplayedImages() {
        return typeOfDisplayedImages;
    }
}
