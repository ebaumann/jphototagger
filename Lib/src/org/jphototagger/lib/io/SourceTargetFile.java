package org.jphototagger.lib.io;

import java.io.File;

import org.jphototagger.lib.util.ObjectUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class SourceTargetFile {

    private final File sourceFile;
    private final File targetFile;

    public SourceTargetFile(File sourceFile, File targetFile) {
        this.sourceFile = sourceFile;
        this.targetFile = targetFile;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public File getTargetFile() {
        return targetFile;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof SourceTargetFile)) {
            return false;
        }

        SourceTargetFile other = (SourceTargetFile) obj;

        return ObjectUtil.equals(sourceFile, other.sourceFile) && ObjectUtil.equals(targetFile, other.targetFile);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.sourceFile != null ? this.sourceFile.hashCode() : 0);
        hash = 11 * hash + (this.targetFile != null ? this.targetFile.hashCode() : 0);
        return hash;
    }
}
