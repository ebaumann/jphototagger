package org.jphototagger.program.module.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bushe.swing.event.EventBus;
import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.file.event.FileMovedEvent;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.domain.filefilter.FileFilterUtil;
import org.jphototagger.domain.repository.SaveOrUpdate;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.SourceTargetFile;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;

/**
 * Moves files to a target directory. The info object in
 * {@code org.jphototagger.program.event.ProgressEvent#getInfo()} is a
 * {@code SourceTargetFile}.
 *
 * @author Elmar Baumann
 */
public final class FileSystemMove extends FileSystem implements Runnable, Cancelable {

    private final List<File> sourceFiles = new ArrayList<>();
    private final List<File> targetFiles = new ArrayList<>();
    private final boolean renameIfTargetFileExists;
    private volatile boolean moveListenerShallUpdateRepository = true;
    private volatile boolean cancel;
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
        int fileCount = sourceFiles.size();
        ProgressEvent progressEvent = createProgressEvent(fileCount);
        notifyProgressListenerStarted(progressEvent);
        for (int i = 0; !cancel && !progressEvent.isCancel() && i < fileCount; i++) {
            File sourceFile = sourceFiles.get(i);
            File targetFile = getTargetFile(targetFiles.get(i));
            if (checkExists(sourceFile, targetFile)) {
                boolean moved = sourceFile.renameTo(targetFile);
                if (!moveListenerShallUpdateRepository && FileFilterUtil.isImageFile(sourceFile) && FileFilterUtil.isImageFile(targetFile)) {
                    FilesystemRepositoryUpdater.moveFile(sourceFile, targetFile);
                }
                notifyMoved(moved, sourceFile, targetFile);
            }
            progressEvent.setValue(i + 1);
            progressEvent.setInfo(new SourceTargetFile(sourceFile, targetFile));
            notifyProgressListenerPerformed(progressEvent);
        }
        notifyProgressListenerEnded(progressEvent);
    }

    private ProgressEvent createProgressEvent(int fileCount) {
        return new ProgressEvent.Builder()
                .source(this)
                .minimum(0)
                .maximum(fileCount)
                .value(0)
                .info("")
                .build();
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
            String message = Bundle.getString(FileSystemMove.class, "FileSystemMove.Error.TargetExists", sourceFile, targetFile);
            MessageDisplayer.error(null, message);
        }
        return !exists;
    }

    private void notifyMoved(boolean moved, File sourceFile, File targetFile) {
        if (moved) {
            FileMovedEvent evt = new FileMovedEvent(this, sourceFile, targetFile);
            evt.putProperty(SaveOrUpdate.class, moveListenerShallUpdateRepository
                    ? SaveOrUpdate.OUT_OF_DATE
                    : SaveOrUpdate.NONE);
            EventBus.publish(evt);
        } else {
            String message = Bundle.getString(FileSystemMove.class, "FileSystemMove.Error", sourceFile, targetFile);
            MessageDisplayer.error(null, message);
        }
    }

    /**
     * @param update Default: true
     */
    public void setMoveListenerShallUpdateRepository(boolean update) {
        moveListenerShallUpdateRepository = update;
    }

    public boolean getMoveListenerShallUpdateRepository() {
        return moveListenerShallUpdateRepository;
    }

    @Override
    public void cancel() {
        cancel = true;
    }
}
