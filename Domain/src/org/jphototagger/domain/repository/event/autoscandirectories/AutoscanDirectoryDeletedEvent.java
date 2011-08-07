package org.jphototagger.domain.repository.event.autoscandirectories;

import java.io.File;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class AutoscanDirectoryDeletedEvent {

    private final Object source;
    private final File directory;

    public AutoscanDirectoryDeletedEvent(Object source, File directory) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        this.source = source;
        this.directory = directory;
    }

    public File getDirectory() {
        return directory;
    }

    public Object getSource() {
        return source;
    }
}
