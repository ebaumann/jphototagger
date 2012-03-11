package org.jphototagger.api.file.event;

import java.io.File;

import org.jphototagger.api.event.PropertyEvent;

/**
 * @author Elmar Baumann
 */
public final class FileMovedEvent extends PropertyEvent {

    private final File sourceFile;
    private final File targetFile;

    public FileMovedEvent(Object source, File sourceFile, File targetFile) {
        super(source);
        if (sourceFile == null) {
            throw new NullPointerException("sourceFile == null");
        }
        if (targetFile == null) {
            throw new NullPointerException("targetFile == null");
        }
        this.sourceFile = sourceFile;
        this.targetFile = targetFile;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public File getTargetFile() {
        return targetFile;
    }
}
