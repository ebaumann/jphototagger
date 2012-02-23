package org.jphototagger.api.file.event;

import java.io.File;

/**
 * @author Elmar Baumann
 */
public final class FileCopiedEvent {

    private final Object source;
    private final File sourceFile;
    private final File targetFile;
    private final boolean copyListenerShallUpdateRepository;

    public FileCopiedEvent(Object source, File sourceFile, File targetFile, boolean copyListenerShallUpdateRepository) {
        if (sourceFile == null) {
            throw new NullPointerException("sourceFile == null");
        }
        if (targetFile == null) {
            throw new NullPointerException("targetFile == null");
        }
        this.source = source;
        this.sourceFile = sourceFile;
        this.targetFile = targetFile;
        this.copyListenerShallUpdateRepository = copyListenerShallUpdateRepository;
    }

    public Object getSource() {
        return source;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public File getTargetFile() {
        return targetFile;
    }

    public boolean getCopyListenerShallUpdateRepository() {
        return copyListenerShallUpdateRepository;
    }
}
