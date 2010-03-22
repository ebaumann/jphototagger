/*
 * @(#)FileSystemMove.java    Created on 2008-10-20
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.io;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.lib.generics.Pair;
import org.jphototagger.lib.io.FileUtil;

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
 * @author  Elmar Baumann
 */
public final class FileSystemMove extends FileSystem implements Runnable {
    private final List<File> sourceFiles = new ArrayList<File>();
    private final List<File> targetFiles = new ArrayList<File>();
    private final boolean    renameIfTargetFileExists;
    private final File       targetDirectory;

    /**
     * Moves sources files to a target directory.
     *
     * @param sourceFiles              source files
     * @param targetDirectory          target directory
     * @param renameIfTargetFileExists renaming automatically the file if the
     *                                 target file exists
     */
    public FileSystemMove(List<File> sourceFiles, File targetDirectory,
                          boolean renameIfTargetFileExists) {
        this.sourceFiles.clear();
        this.sourceFiles.addAll(sourceFiles);
        this.targetDirectory          = targetDirectory;
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
    public FileSystemMove(List<File> sourceFiles, List<File> targetFiles,
                          boolean renameIfTargetFileExists) {
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
            targetFiles.add(new File(targetDirectory.getAbsolutePath()
                                     + File.separator + sourceFile.getName()));
        }
    }

    @Override
    public void run() {
        int           size          = sourceFiles.size();
        ProgressEvent progressEvent = new ProgressEvent(this, 0, size, 0, "");

        notifyProgressListenerStarted(progressEvent);

        boolean stop = progressEvent.isStop();

        for (int i = 0; !stop && (i < size); i++) {
            File sourceFile = sourceFiles.get(i);
            File targetFile = getTargetFile(targetFiles.get(i));

            if (checkExists(sourceFile, targetFile)) {
                boolean moved = sourceFile.renameTo(targetFile);

                notifyMoved(moved, sourceFile, targetFile);
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
            MessageDisplayer.error(null, "FileSystemMove.Error.TargetExists",
                                   sourceFile, targetFile);
        }

        return !exists;
    }

    private void notifyMoved(boolean moved, File sourceFile, File targetFile) {
        if (moved) {
            notifyFileSystemListenersMoved(sourceFile, targetFile);
        } else {
            MessageDisplayer.error(null, "FileSystemMove.Error", sourceFile,
                                   targetFile);
        }
    }
}
