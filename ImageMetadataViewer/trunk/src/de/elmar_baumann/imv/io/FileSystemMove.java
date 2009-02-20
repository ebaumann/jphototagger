package de.elmar_baumann.imv.io;

import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.FileSystemAction;
import de.elmar_baumann.imv.event.FileSystemError;
import de.elmar_baumann.lib.template.Pair;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Moves files to a target directory. The info object in
 * {@link de.elmar_baumann.imv.event.ProgressEvent#getInfo()} is a
 * {@link de.elmar_baumann.lib.template.Pair<File, File>} where
 * {@link de.elmar_baumann.lib.template.Pair#getFirst()} is the source file and
 * {@link de.elmar_baumann.lib.template.Pair#getSecond()} is the target file.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/20
 */
public final class FileSystemMove extends FileSystem implements Runnable {

    private List<File> sourceFiles = new ArrayList<File>();
    private List<File> targetFiles = new ArrayList<File>();
    private File targetDirectory;

    /**
     * Moves sources files to a target directory.
     * 
     * @param sourceFiles      source files
     * @param targetDirectory  target directory
     */
    public FileSystemMove(List<File> sourceFiles, File targetDirectory) {
        this.sourceFiles = sourceFiles;
        this.targetDirectory = targetDirectory;
        setTargetFiles();
    }

    /**
     * Moves source files to target files.
     * 
     * @param sourceFiles  source files
     * @param targetFiles  target files - size must be equals to sourceFiles
     */
    public FileSystemMove(List<File> sourceFiles, List<File> targetFiles) {
        this.sourceFiles = sourceFiles;
        this.targetFiles = targetFiles;
    }

    private void setTargetFiles() {
        targetFiles = new ArrayList<File>(sourceFiles.size());
        for (File sourceFile : sourceFiles) {
            targetFiles.add(new File(
                targetDirectory.getAbsolutePath() + File.separator + sourceFile.getName()));
        }
    }

    @Override
    public void run() {
        int size = sourceFiles.size();

        ProgressEvent progressEvent = new ProgressEvent(this, 0, size, 0, "");
        notifyProgressListenerStarted(progressEvent);
        boolean stop = progressEvent.isStop();

        for (int i = 0; !stop && i < size; i++) {
            File sourceFile = sourceFiles.get(i);
            File targetFile = targetFiles.get(i);
            if (checkExists(sourceFile, targetFile)) {
                boolean moved = sourceFile.renameTo(targetFile);
                checkMoved(moved, sourceFile, targetFile);
            }
            progressEvent.setValue(i + 1);
            progressEvent.setInfo(new Pair<File, File>(sourceFile, targetFile));
            notifyProgressListenerPerformed(progressEvent);
            stop = progressEvent.isStop();
        }
        notifyProgressListenerEnded(progressEvent);
    }

    private boolean checkExists(File sourceFile, File targetFile) {
        boolean exists = targetFile.exists();
        if (exists) {
            notifyError(FileSystemError.MOVE_RENAME_EXISTS, sourceFile, targetFile);
        }
        return !exists;
    }

    private void checkMoved(boolean moved, File sourceFile, File targetFile) {
        if (moved) {
            notifyActionListenersPerformed(FileSystemAction.MOVE, sourceFile, targetFile);
        } else {
            notifyError(FileSystemError.UNKNOWN, sourceFile, targetFile);
        }
    }

    private synchronized void notifyError(FileSystemError error, File sourceFile, File targetFile) {
        notifyActionListenersFailed(
            FileSystemAction.MOVE,
            error,
            sourceFile,
            targetFile);
        notifyActionListenersFailed(
            FileSystemAction.MOVE,
            error,
            sourceFile,
            targetFile);
    }
}
