package org.jphototagger.program.io;

import org.jphototagger.lib.generics.Pair;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.event.ProgressEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Moves files to a target directory. The info object in
 * {@link org.jphototagger.program.event.ProgressEvent#getInfo()} is a
 * {@link org.jphototagger.lib.generics.Pair} where
 * {@link org.jphototagger.lib.generics.Pair#getFirst()} is the source file and
 * {@link org.jphototagger.lib.generics.Pair#getSecond()} is the target file.
 *
 * @author Elmar Baumann
 */
public final class FileSystemMove extends FileSystem implements Runnable {
    private final List<File> sourceFiles = new ArrayList<File>();
    private final List<File> targetFiles = new ArrayList<File>();
    private final boolean renameIfTargetFileExists;
    private final File targetDirectory;

    /**
     * Moves sources files to a target directory.
     *
     * @param sourceFiles              source files
     * @param targetDirectory          target directory
     * @param renameIfTargetFileExists renaming automatically the file if the
     *                                 target file exists
     */
    public FileSystemMove(List<File> sourceFiles, File targetDirectory, boolean renameIfTargetFileExists) {
        if (sourceFiles == null) {
            throw new NullPointerException("sourceFiles == null");
        }

        if (targetDirectory == null) {
            throw new NullPointerException("targetDirectory == null");
        }

        this.sourceFiles.clear();
        this.sourceFiles.addAll(sourceFiles);
        this.targetDirectory = targetDirectory;
        this.renameIfTargetFileExists = renameIfTargetFileExists;
        setTargetFiles();
    }

    /**
     * Moves source files to target files.
     *
     * @param sourceFiles              source files
     * @param targetFiles              target files - size must be equals to
     *                                 sourceFiles
     * @param renameIfTargetFileExists renaming automatically the file if the
     *                                 target file exists
     */
    public FileSystemMove(List<File> sourceFiles, List<File> targetFiles, boolean renameIfTargetFileExists) {
        if (sourceFiles == null) {
            throw new NullPointerException("sourceFiles == null");
        }

        if (targetFiles == null) {
            throw new NullPointerException("targetFiles == null");
        }

        this.targetDirectory = new File("");
        this.sourceFiles.clear();
        this.targetFiles.clear();
        this.sourceFiles.addAll(sourceFiles);
        this.targetFiles.addAll(targetFiles);
        this.renameIfTargetFileExists = renameIfTargetFileExists;
    }

    private void setTargetFiles() {
        targetFiles.clear();

        for (File sourceFile : sourceFiles) {
            targetFiles.add(new File(targetDirectory.getAbsolutePath() + File.separator + sourceFile.getName()));
        }
    }

    @Override
    public void run() {
        int size = sourceFiles.size();
        ProgressEvent progressEvent = new ProgressEvent(this, 0, size, 0, "");

        notifyProgressListenerStarted(progressEvent);

        boolean cancel = progressEvent.isCancel();

        for (int i = 0; !cancel && (i < size); i++) {
            File sourceFile = sourceFiles.get(i);
            File targetFile = getTargetFile(targetFiles.get(i));

            if (checkExists(sourceFile, targetFile)) {
                boolean moved = sourceFile.renameTo(targetFile);

                notifyMoved(moved, sourceFile, targetFile);
            }

            progressEvent.setValue(i + 1);
            progressEvent.setInfo(new Pair<File, File>(sourceFile, targetFile));
            notifyProgressListenerPerformed(progressEvent);
            cancel = progressEvent.isCancel();
        }

        notifyProgressListenerEnded(progressEvent);
    }

    private File getTargetFile(File file) {
        File targetFile = file;

        if (renameIfTargetFileExists && targetFile.exists()) {
            targetFile = FileUtil.getNotExistingFile(targetFile);
        }

        return targetFile;
    }

    private boolean checkExists(File sourceFile, File targetFile) {
        boolean exists = targetFile.exists();

        if (exists) {
            MessageDisplayer.error(null, "FileSystemMove.Error.TargetExists", sourceFile, targetFile);
        }

        return !exists;
    }

    private void notifyMoved(boolean moved, File sourceFile, File targetFile) {
        if (moved) {
            notifyFileSystemListenersMoved(sourceFile, targetFile);
        } else {
            MessageDisplayer.error(null, "FileSystemMove.Error", sourceFile, targetFile);
        }
    }
}
