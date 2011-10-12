package org.jphototagger.program.module.filesystem;

import java.io.File;
import java.util.Arrays;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.concurrent.SerialTaskExecutor;
import org.jphototagger.api.file.event.FileCopiedEvent;
import org.jphototagger.api.file.event.FileDeletedEvent;
import org.jphototagger.api.file.event.FileMovedEvent;
import org.jphototagger.api.file.event.FileRenamedEvent;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.repository.InsertIntoRepository;
import org.jphototagger.program.misc.InsertImageFilesIntoRepository;

/**
 * Updates the repository on file system events.
 *
 * @author Elmar Baumann
 */
public final class FilesystemRepositoryUpdater {

    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);
    private final SerialTaskExecutor executor = Lookup.getDefault().lookup(SerialTaskExecutor.class);
    private volatile boolean wait;

    /**
     * Creates a new instance.
     *
     * @param wait if true, wait until completion. If false, start a new
     *             thread. Default: false (new thread)
     */
    public FilesystemRepositoryUpdater(boolean wait) {
        this.wait = wait;
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    private void insertFileIntoRepository(File file) {
        if (ImageFileFilterer.isImageFile(file)) {
            InsertImageFilesIntoRepository inserter = new InsertImageFilesIntoRepository(Arrays.asList(file),
                    InsertIntoRepository.OUT_OF_DATE);

            if (wait) {
                inserter.run();    // Has to run in this thread!
            } else {
                executor.addTask(inserter);
            }
        }
    }

    private void removeFileFromRepository(File file) {
        if (ImageFileFilterer.isImageFile(file)) {

            if (repo.existsImageFile(file)) {
                repo.deleteImageFiles(Arrays.asList(file));
            }
        }
    }

    @EventSubscriber(eventClass = FileCopiedEvent.class)
    public void fileCopied(FileCopiedEvent evt) {
        File targetFile = evt.getTargetFile();

        if (ImageFileFilterer.isImageFile(targetFile)) {
            insertFileIntoRepository(targetFile);
        }
    }

    @EventSubscriber(eventClass = FileDeletedEvent.class)
    public void fileDeleted(FileDeletedEvent evt) {
        File file = evt.getFile();

        if (ImageFileFilterer.isImageFile(file)) {
            removeFileFromRepository(file);
        }
    }

    @EventSubscriber(eventClass = FileMovedEvent.class)
    public void fileMoved(FileMovedEvent evt) {
        File sourceFile = evt.getSourceFile();
        File targetFile = evt.getTargetFile();

        if (ImageFileFilterer.isImageFile(sourceFile) && ImageFileFilterer.isImageFile(targetFile)) {
            repo.updateRenameImageFile(sourceFile, targetFile);
        }
    }

    @EventSubscriber(eventClass = FileRenamedEvent.class)
    public void fileRenamed(final FileRenamedEvent evt) {
        File sourceFile = evt.getSourceFile();
        File targetFile = evt.getTargetFile();

        if (ImageFileFilterer.isImageFile(sourceFile) && ImageFileFilterer.isImageFile(targetFile)) {
            repo.updateRenameImageFile(sourceFile, targetFile);
        }
    }
}
