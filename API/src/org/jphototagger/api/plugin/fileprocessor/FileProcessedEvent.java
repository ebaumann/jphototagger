package org.jphototagger.api.plugin.fileprocessor;

import java.io.File;

/**
 * @author Elmar Baumann
 */
public final class FileProcessedEvent {

    private final Object source;
    private final File file;
    private final boolean fileChanged;

    public FileProcessedEvent(Object source, File file, boolean fileChanged) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        this.source = source;
        this.file = file;
        this.fileChanged = fileChanged;
    }

    public boolean isFileChanged() {
        return fileChanged;
    }

    public File getFile() {
        return file;
    }

    public Object getSource() {
        return source;
    }
}
