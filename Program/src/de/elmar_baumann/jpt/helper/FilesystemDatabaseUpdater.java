/*
 * JPhotoTagger tags and finds images fast
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
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.FileSystemEvent;
import de.elmar_baumann.jpt.event.listener.FileSystemListener;
import de.elmar_baumann.jpt.helper.InsertImageFilesIntoDatabase.Insert;
import de.elmar_baumann.jpt.io.ImageUtil;
import de.elmar_baumann.jpt.tasks.UserTasks;
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
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-11
 */
public final class FilesystemDatabaseUpdater implements FileSystemListener {

    volatile boolean wait;

    /**
     * Creates a new instance.
     *
     * @param wait if true, wait until completion. If false, start a new
     *             thread. Default: false (new thread)
     */
    public FilesystemDatabaseUpdater(boolean wait) {
        this.wait = wait;
    }

    @Override
    public void actionPerformed(FileSystemEvent event) {

        if (event.isError()) return;

        File                 src    = event.getSource();
        File                 target = event.getTarget();
        FileSystemEvent.Type type   = event.getType();

        if (!ImageUtil.isImageFile(src)) return;

        if (type.equals(FileSystemEvent.Type.COPY)) {

            insertFileIntoDatabase(target);

        } else if (type.equals(FileSystemEvent.Type.DELETE)) {

            removeFileFromDatabase(src);

        } else if (type.equals(FileSystemEvent.Type.MOVE) || type.equals(FileSystemEvent.Type.RENAME)) {

            DatabaseImageFiles.INSTANCE.updateRename(src.getAbsolutePath(), target.getAbsolutePath());
        }
    }

    private void insertFileIntoDatabase(File file) {
        InsertImageFilesIntoDatabase inserter = new InsertImageFilesIntoDatabase(
                       Arrays.asList(file.getAbsolutePath()), Insert.OUT_OF_DATE);
        if (wait) {
            inserter.run(); // Do not start a thread!
        } else {
            UserTasks.INSTANCE.add(inserter);
        }
    }

    private void removeFileFromDatabase(File file) {
        DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
        String filename = file.getAbsolutePath();
        if (db.exists(filename)) {
            db.delete(Arrays.asList(filename));
        }
    }
}
