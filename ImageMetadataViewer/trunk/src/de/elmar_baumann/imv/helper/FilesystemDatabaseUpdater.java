package de.elmar_baumann.imv.helper;

import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.FileSystemError;
import de.elmar_baumann.imv.event.FileSystemEvent;
import de.elmar_baumann.imv.event.listener.FileSystemActionListener;
import de.elmar_baumann.imv.io.ImageUtil;
import de.elmar_baumann.imv.tasks.UserTasks;
import java.io.File;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * Updates the database on file system events.
 *
 * <strong>Usage:</strong> Create an instance and register it as
 * {@link FileSystemActionListener} to a process copying/renaming/deleting
 * files. Example:
 *
 * @{@code
 * CopyToDirectoryDialog dialog = new CopyToDirectoryDialog();
 * dialog.setSourceFiles(files);
 * dialog.addFileSystemActionListener(new FilesystemDatabaseUpdater());
 * dialog.setVisible(true);
 * }
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-11
 */
public final class FilesystemDatabaseUpdater implements FileSystemActionListener {

    boolean wait;

    public FilesystemDatabaseUpdater() {
    }

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
    public void actionPerformed(FileSystemEvent action, File src, File target) {

        if (!ImageUtil.isImageFile(src)) return;
        if (action.equals(FileSystemEvent.COPY)) {
            insertFileIntoDatabase(target);
        } else if (action.equals(FileSystemEvent.DELETE)) {
            removeFileFromDatabase(src);
        } else if (action.equals(FileSystemEvent.MOVE) ||
                action.equals(FileSystemEvent.RENAME)) {
            DatabaseImageFiles.INSTANCE.updateRenameImageFilename(
                    src.getAbsolutePath(), target.getAbsolutePath());
        }
    }

    @Override
    public void actionFailed(
            FileSystemEvent action, FileSystemError error, File src, File target) {
        // ignore
    }

    private void insertFileIntoDatabase(File file) {
        InsertImageFilesIntoDatabase inserter =
                new InsertImageFilesIntoDatabase(
                Arrays.asList(file.getAbsolutePath()),
                EnumSet.of(InsertImageFilesIntoDatabase.Insert.OUT_OF_DATE),
                null);
        if (wait) {
            inserter.run();
        } else {
            UserTasks.INSTANCE.add(inserter);
        }
    }

    private void removeFileFromDatabase(File file) {
        DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
        String filename = file.getAbsolutePath();
        if (db.existsFilename(filename)) {
            db.deleteImageFiles(Arrays.asList(filename));
        }
    }
}
