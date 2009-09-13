/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.imv.io;

import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.FileSystemEvent;
import de.elmar_baumann.imv.event.FileSystemError;
import de.elmar_baumann.lib.generics.Pair;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Moves files to a target directory. The info object in
 * {@link de.elmar_baumann.imv.event.ProgressEvent#getInfo()} is a
 * {@link de.elmar_baumann.lib.generics.Pair} where
 * {@link de.elmar_baumann.lib.generics.Pair#getFirst()} is the source file and
 * {@link de.elmar_baumann.lib.generics.Pair#getSecond()} is the target file.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-20
 */
public final class FileSystemMove extends FileSystem implements Runnable {

    private List<File> sourceFiles = new ArrayList<File>();
    private List<File> targetFiles = new ArrayList<File>();
    private final boolean renameIfTargetFileExists;
    private File targetDirectory;

    /**
     * Moves sources files to a target directory.
     * 
     * @param sourceFiles              source files
     * @param targetDirectory          target directory
     * @param renameIfTargetFileExists renaming automatically the file if the
     *                                 target file exists
     */
    public FileSystemMove(
            List<File> sourceFiles,
            File targetDirectory,
            boolean renameIfTargetFileExists) {
        this.sourceFiles = new ArrayList<File>(sourceFiles);
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
    public FileSystemMove(
            List<File> sourceFiles,
            List<File> targetFiles,
            boolean renameIfTargetFileExists) {
        this.sourceFiles = new ArrayList<File>(sourceFiles);
        this.targetFiles = new ArrayList<File>(targetFiles);
        this.renameIfTargetFileExists = renameIfTargetFileExists;
    }

    private void setTargetFiles() {
        targetFiles = new ArrayList<File>(sourceFiles.size());
        for (File sourceFile : sourceFiles) {
            targetFiles.add(new File(
                    targetDirectory.getAbsolutePath() + File.separator +
                    sourceFile.getName()));
        }
    }

    @Override
    public void run() {
        int size = sourceFiles.size();

        ProgressEvent progressEvent = new ProgressEvent(this, 0, size, 0, ""); // NOI18N
        notifyProgressListenerStarted(progressEvent);
        boolean stop = progressEvent.isStop();

        for (int i = 0; !stop && i < size; i++) {
            File sourceFile = sourceFiles.get(i);
            File targetFile = getTargetFile(targetFiles.get(i));
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
            notifyError(FileSystemError.MOVE_RENAME_EXISTS, sourceFile,
                    targetFile);
        }
        return !exists;
    }

    private void checkMoved(boolean moved, File sourceFile, File targetFile) {
        if (moved) {
            notifyActionListenersPerformed(FileSystemEvent.MOVE, sourceFile,
                    targetFile);
        } else {
            notifyError(FileSystemError.UNKNOWN, sourceFile, targetFile);
        }
    }

    private synchronized void notifyError(FileSystemError error, File sourceFile,
            File targetFile) {
        notifyActionListenersFailed(
                FileSystemEvent.MOVE,
                error,
                sourceFile,
                targetFile);
        notifyActionListenersFailed(
                FileSystemEvent.MOVE,
                error,
                sourceFile,
                targetFile);
    }
}
