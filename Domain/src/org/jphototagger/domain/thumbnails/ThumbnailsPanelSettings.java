package org.jphototagger.domain.thumbnails;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Elmar Baumann
 */
public class ThumbnailsPanelSettings {

    private final List<Integer> selectedIndices;
    private final Point viewPosition;
    private final List<File> selectedFiles = new ArrayList<>();

    public ThumbnailsPanelSettings(Point viewPosition, List<Integer> selectedIndices) {
        if (viewPosition == null) {
            throw new NullPointerException("viewPosition == null");
        }
        if (selectedIndices == null) {
            throw new NullPointerException("selectedIndices == null");
        }
        this.viewPosition = viewPosition;
        this.selectedIndices = new ArrayList<>(selectedIndices);
    }

    public Point getViewPosition() {
        return viewPosition;
    }

    public List<File> getSelectedFiles() {
        return Collections.unmodifiableList(selectedFiles);
    }

    public void setSelectedFiles(List<File> files) {
        if (files == null) {
            throw new NullPointerException("files == null");
    }
        this.selectedFiles.clear();
        this.selectedFiles.addAll(files);
}

    /**
     * Higher priority than {@link #hasSelectedIndices()}
     *
     * @return
     */
    public boolean hasSelectedFiles() {
        return !selectedFiles.isEmpty();
    }

    public List<Integer> getSelectedIndices() {
        return Collections.unmodifiableList(selectedIndices);
    }

    public boolean hasSelectedIndices() {
        return !selectedIndices.isEmpty();
    }
}
