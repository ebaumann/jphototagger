package org.jphototagger.domain.thumbnails.event;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Elmar Baumann
 */
public final class ThumbnailsSelectionChangedEvent {

    private final Object source;
    private final List<File> selectedImageFiles;
    private final List<Integer> selectedIndices;

    public ThumbnailsSelectionChangedEvent(Object source, List<File> selectedImageFiles, List<Integer> selectedIndices) {
        if (selectedImageFiles == null) {
            throw new NullPointerException("selectedImageFiles == null");
        }

        if (selectedIndices == null) {
            throw new NullPointerException("selectedIndices == null");
        }

        this.source = source;
        this.selectedImageFiles = new ArrayList<File>(selectedImageFiles);
        this.selectedIndices = new ArrayList<Integer>(selectedIndices);
    }

    public List<File> getSelectedImageFiles() {
        return Collections.unmodifiableList(selectedImageFiles);
    }

    public List<Integer> getSelectedIndices() {
        return Collections.unmodifiableList(selectedIndices);
    }

    public boolean isAFileSelected() {
        return !selectedImageFiles.isEmpty();
    }

    public int getSelectionCount() {
        return selectedImageFiles.size();
    }

    public Object getSource() {
        return source;
    }
}
