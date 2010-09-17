/*
 * @(#)FilesystemDatabaseUpdater.java    Created on 2009-08-11
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.helper;

import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.event.listener.FileSystemListener;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase.Insert;
import org.jphototagger.program.io.ImageFileFilterer;
import org.jphototagger.program.tasks.UserTasks;

import java.io.File;

import java.util.Arrays;

/**
 * Updates the database on file system events.
 *
 * <strong>Usage:</strong> Create an instance and register it as
 * {@link FileSystemListener} to a process copying/renaming/deleting
 * files. Example:
 *
 * {@code
 * CopyToDirectoryDialog dialog = new CopyToDirectoryDialog();
 * dialog.setSourceFiles(files);
 * dialog.addFileSystemListener(new FilesystemDatabaseUpdater());
 * dialog.setVisible(true);
 * }
 *
 * @author Elmar Baumann
 */
public final class FilesystemDatabaseUpdater implements FileSystemListener {
    private volatile boolean wait;

    /**
     * Creates a new instance.
     *
     * @param wait if true, wait until completion. If false, start a new
     *             thread. Default: false (new thread)
     */
    public FilesystemDatabaseUpdater(boolean wait) {
        this.wait = wait;
    }

    private void insertFileIntoDatabase(File file) {
        if (ImageFileFilterer.isImageFile(file)) {
            InsertImageFilesIntoDatabase inserter =
                new InsertImageFilesIntoDatabase(Arrays.asList(file),
                    Insert.OUT_OF_DATE);

            if (wait) {
                inserter.run();    // Do not start a thread!
            } else {
                UserTasks.INSTANCE.add(inserter);
            }
        }
    }

    private void removeFileFromDatabase(File file) {
        if (ImageFileFilterer.isImageFile(file)) {
            DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;

            if (db.exists(file)) {
                db.delete(Arrays.asList(file));
            }
        }
    }

    @Override
    public void fileCopied(File source, File target) {
        if (ImageFileFilterer.isImageFile(target)) {
            insertFileIntoDatabase(target);
        }
    }

    @Override
    public void fileDeleted(File file) {
        if (ImageFileFilterer.isImageFile(file)) {
            removeFileFromDatabase(file);
        }
    }

    @Override
    public void fileMoved(File source, File target) {
        if (ImageFileFilterer.isImageFile(source)
                && ImageFileFilterer.isImageFile(target)) {
            DatabaseImageFiles.INSTANCE.updateRename(source, target);
        }
    }

    @Override
    public void fileRenamed(File source, File target) {
        if (ImageFileFilterer.isImageFile(source)
                && ImageFileFilterer.isImageFile(target)) {
            DatabaseImageFiles.INSTANCE.updateRename(source, target);
        }
    }
}
