package org.jphototagger.domain.repository.event.thumbnails;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ThumbnailSelectionChangeEvent {

    private final Object source;
    private final List<File> selectedFiles;

    public ThumbnailSelectionChangeEvent(Object source, List<File> selectedFiles) {
        if (selectedFiles == null) {
            throw new NullPointerException("selectedFiles == null");
        }

        this.source = source;
        this.selectedFiles = new ArrayList<File>(selectedFiles);
    }

    public Object getSource() {
        return source;
    }

    public List<File> getSelectedFiles() {
        return Collections.unmodifiableList(selectedFiles);
    }
    
    public int getSelectionCount() {
        return selectedFiles.size();
    }
    
    public boolean isAFileSelected() {
        return selectedFiles.size() > 0;
    }
}
