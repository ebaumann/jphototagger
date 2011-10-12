package org.jphototagger.api.file.event;

import java.io.File;

/**
 * @author Elmar Baumann
 */
public final class FileDeletedEvent {

    private final Object source;
    private final File file;

    public FileDeletedEvent(Object source, File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        this.source = source;
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public Object getSource() {
        return source;
    }
}
