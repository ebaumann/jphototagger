package org.jphototagger.domain.thumbnails;

import java.awt.Point;
import java.util.Collections;
import java.util.List;

/**
 * @author Elmar Baumann
 */
public class ThumbnailsPanelSettings {

    private final List<Integer> selectedThumbnailIndices;
    private final Point viewPosition;

    public ThumbnailsPanelSettings(Point viewPosition, List<Integer> selectedThumbnailIndices) {
        if (viewPosition == null) {
            throw new NullPointerException("viewPosition == null");
        }
        if (selectedThumbnailIndices == null) {
            throw new NullPointerException("selectedThumbnailIndices == null");
        }
        this.viewPosition = viewPosition;
        this.selectedThumbnailIndices = selectedThumbnailIndices;
    }

    public List<Integer> getSelThumbnails() {
        return Collections.unmodifiableList(selectedThumbnailIndices);
    }

    public Point getViewPosition() {
        return viewPosition;
    }

    public boolean hasSelThumbnails() {
        return selectedThumbnailIndices != null;
    }

    public boolean hasViewPosition() {
        return viewPosition != null;
    }
}
