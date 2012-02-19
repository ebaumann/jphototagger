package org.jphototagger.importfiles;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.jphototagger.api.file.FileRenameStrategy;
import org.jphototagger.api.file.SubdirectoryCreateStrategy;

/**
 * @author Elmar Baumann
 */
public final class ImportData {

    private final List<File> sourceFiles;
    private final File targetDirectory;
    private final SubdirectoryCreateStrategy subdirectoryCreateStrategy;
    private final FileRenameStrategy fileRenameStrategy;
    private final File scriptFile;
    private final boolean deleteSourceFilesAfterCopying;

    private ImportData(Builder builder) {
        this.sourceFiles = builder.sourceFiles;
        this.targetDirectory = builder.targetDirectory;
        this.subdirectoryCreateStrategy = builder.subdirectoryCreateStrategy;
        this.fileRenameStrategy = builder.fileRenameStrategy;
        this.scriptFile = builder.scriptFile;
        this.deleteSourceFilesAfterCopying = builder.deleteSourceFilesAfterCopying;
    }

    public List<File> getSourceFiles() {
        return new LinkedList<File>(sourceFiles);
    }

    public File getTargetDirectory() {
        return targetDirectory;
    }

    public boolean isDeleteSourceFilesAfterCopying() {
        return deleteSourceFilesAfterCopying;
    }

    public SubdirectoryCreateStrategy getSubdirectoryCreateStrategy() {
        return subdirectoryCreateStrategy;
    }

    public FileRenameStrategy getFileRenameStrategy() {
        return fileRenameStrategy;
    }

    public File getScriptFile() {
        return scriptFile;
    }

    public boolean hasSubdirectoryCreateStrategy() {
        return subdirectoryCreateStrategy != null;
    }

    public boolean hasFileRenameStrategy() {
        return fileRenameStrategy != null;
    }

    public boolean hasScriptFile() {
        return scriptFile != null;
    }

    public int getSourceFileCount() {
        return sourceFiles.size();
    }

    public boolean hasSourceFiles() {
        return !sourceFiles.isEmpty();
    }

    public static class Builder {

        private final List<File> sourceFiles;
        private final File targetDirectory;
        private SubdirectoryCreateStrategy subdirectoryCreateStrategy;
        private FileRenameStrategy fileRenameStrategy;
        private File scriptFile;
        ;
        private boolean deleteSourceFilesAfterCopying;

        public Builder(List<File> sourceFiles, File targetDirectory) {
            if (sourceFiles == null) {
                throw new NullPointerException("sourceFiles == null");
            }
            if (targetDirectory == null) {
                throw new NullPointerException("targetDirectory == null");
            }
            this.sourceFiles = sourceFiles;
            this.targetDirectory = targetDirectory;
        }

        public Builder deleteSourceFilesAfterCopying(boolean deleteSourceFilesAfterCopying) {
            this.deleteSourceFilesAfterCopying = deleteSourceFilesAfterCopying;
            return this;
        }

        public Builder fileRenameStrategy(FileRenameStrategy fileRenameStrategy) {
            this.fileRenameStrategy = fileRenameStrategy;
            return this;
        }

        public Builder subdirectoryCreateStrategy(SubdirectoryCreateStrategy subdirectoryCreateStrategy) {
            this.subdirectoryCreateStrategy = subdirectoryCreateStrategy;
            return this;
        }

        public Builder scriptFile(File scriptFile) {
            this.scriptFile = scriptFile;
            return this;
        }

        public ImportData build() {
            return new ImportData(this);
        }
    }
}
