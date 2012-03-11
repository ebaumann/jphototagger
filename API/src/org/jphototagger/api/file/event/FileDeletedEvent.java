package org.jphototagger.api.file.event;

import java.io.File;

import org.jphototagger.api.event.PropertyEvent;

/**
 * @author Elmar Baumann
 */
public final class FileDeletedEvent extends PropertyEvent {

    private final File file;

    public FileDeletedEvent(Object source, File file) {
        super(source);
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
