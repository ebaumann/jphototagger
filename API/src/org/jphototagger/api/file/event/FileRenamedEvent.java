package org.jphototagger.api.file.event;

import java.io.File;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class FileRenamedEvent {

    private final Object source;
    private final File sourceFile;
    private final File targetFile;

    public FileRenamedEvent(Object source, File sourceFile, File targetFile) {
        if (sourceFile == null) {
            throw new NullPointerException("sourceFile == null");
        }

        if (targetFile == null) {
            throw new NullPointerException("targetFile == null");
        }

        this.source = source;
        this.sourceFile = sourceFile;
        this.targetFile = targetFile;
    }

    public Object getSource() {
        return source;
    }

    public File getTargetFile() {
        return targetFile;
    }

    public File getSourceFile() {
        return sourceFile;
    }
}
