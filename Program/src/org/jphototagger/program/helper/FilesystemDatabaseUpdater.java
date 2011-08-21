package org.jphototagger.program.helper;

import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.domain.database.InsertIntoDatabase;
import org.jphototagger.program.io.ImageFileFilterer;
import org.jphototagger.program.tasks.UserTasks;
import java.io.File;
import java.util.Arrays;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.file.event.FileCopiedEvent;
import org.jphototagger.api.file.event.FileDeletedEvent;
import org.jphototagger.api.file.event.FileMovedEvent;
import org.jphototagger.api.file.event.FileRenamedEvent;

/**
 * Updates the database on file system events.
 *
 * @author Elmar Baumann
 */
public final class FilesystemDatabaseUpdater {

    private volatile boolean wait;

    /**
     * Creates a new instance.
     *
     * @param wait if true, wait until completion. If false, start a new
     *             thread. Default: false (new thread)
     */
    public FilesystemDatabaseUpdater(boolean wait) {
        this.wait = wait;
        AnnotationProcessor.process(this);
    }

    private void insertFileIntoDatabase(File file) {
        if (ImageFileFilterer.isImageFile(file)) {
            InsertImageFilesIntoDatabase inserter = new InsertImageFilesIntoDatabase(Arrays.asList(file),
                    InsertIntoDatabase.OUT_OF_DATE);

            if (wait) {
                inserter.run();    // run in this thread!
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

    @EventSubscriber(eventClass = FileCopiedEvent.class)
    public void fileCopied(FileCopiedEvent evt) {
        File targetFile = evt.getTargetFile();

        if (ImageFileFilterer.isImageFile(targetFile)) {
            insertFileIntoDatabase(targetFile);
        }
    }

    @EventSubscriber(eventClass = FileDeletedEvent.class)
    public void fileDeleted(FileDeletedEvent evt) {
        File file = evt.getFile();

        if (ImageFileFilterer.isImageFile(file)) {
            removeFileFromDatabase(file);
        }
    }

    @EventSubscriber(eventClass = FileMovedEvent.class)
    public void fileMoved(FileMovedEvent evt) {
        File sourceFile = evt.getSourceFile();
        File targetFile = evt.getTargetFile();

        if (ImageFileFilterer.isImageFile(sourceFile) && ImageFileFilterer.isImageFile(targetFile)) {
            DatabaseImageFiles.INSTANCE.updateRename(sourceFile, targetFile);
        }
    }

    @EventSubscriber(eventClass = FileRenamedEvent.class)
    public void fileRenamed(final FileRenamedEvent evt) {
        File sourceFile = evt.getSourceFile();
        File targetFile = evt.getTargetFile();

        if (ImageFileFilterer.isImageFile(sourceFile) && ImageFileFilterer.isImageFile(targetFile)) {
            DatabaseImageFiles.INSTANCE.updateRename(sourceFile, targetFile);
        }
    }
}
