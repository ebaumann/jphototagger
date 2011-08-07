package org.jphototagger.domain.repository.event.thumbnails;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class ThumbnailsChangedEvent {

    private final Object source;
    private final List<File> files;

    public ThumbnailsChangedEvent(Object source, List<File> files) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }
        
        this.source = source;
        this.files = files;
    }

    public List<File> getFiles() {
        return Collections.unmodifiableList(files);
    }

    public int getFileCount() {
        return files.size();
    }
    
    public Object getSource() {
        return source;
    }
}
