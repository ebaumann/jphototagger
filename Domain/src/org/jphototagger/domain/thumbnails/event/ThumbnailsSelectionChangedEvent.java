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
    private final List<File> selectedFiles;
    private final List<Integer> selectedIndices;

    public ThumbnailsSelectionChangedEvent(Object source, List<? extends File> selectedFiles, List<Integer> selectedIndices) {
        if (selectedFiles == null) {
            throw new NullPointerException("selectedFiles == null");
        }

        if (selectedIndices == null) {
            throw new NullPointerException("selectedIndices == null");
        }

        this.source = source;
        this.selectedFiles = new ArrayList<File>(selectedFiles);
        this.selectedIndices = new ArrayList<>(selectedIndices);
    }

    public List<File> getSelectedFiles() {
        return Collections.unmodifiableList(selectedFiles);
    }

    public List<Integer> getSelectedIndices() {
        return Collections.unmodifiableList(selectedIndices);
    }

    public boolean isAFileSelected() {
        return !selectedFiles.isEmpty();
    }

    public boolean isExcactlyOneFileSelected() {
        return selectedFiles.size() == 1;
    }

    public int getSelectionCount() {
        return selectedFiles.size();
    }

    public Object getSource() {
        return source;
    }
}
