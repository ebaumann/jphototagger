package org.jphototagger.domain.event;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ImageSelectionChangeEvent {

    private final Object source;
    private final List<File> currentSelectedImageFiles;

    public ImageSelectionChangeEvent(Object source, List<File> currentSelectedImageFiles) {
        if (currentSelectedImageFiles == null) {
            throw new NullPointerException("currentSelectedImageFiles == null");
        }

        this.source = source;
        this.currentSelectedImageFiles = new ArrayList<File>(currentSelectedImageFiles);
    }

    public List<File> getCurrentSelectedImageFiles() {
        return Collections.unmodifiableList(currentSelectedImageFiles);
    }

    public Object getSource() {
        return source;
    }
}
