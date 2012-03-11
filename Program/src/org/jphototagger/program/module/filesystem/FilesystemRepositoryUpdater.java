package org.jphototagger.program.module.filesystem;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.file.event.FileCopiedEvent;
import org.jphototagger.api.file.event.FileDeletedEvent;
import org.jphototagger.api.file.event.FileMovedEvent;
import org.jphototagger.api.file.event.FileRenamedEvent;
import org.jphototagger.domain.filefilter.FileFilterUtil;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.repository.RepositoryUtil;
import org.jphototagger.domain.repository.SaveOrUpdate;
import org.jphototagger.program.misc.SaveToOrUpdateFilesInRepositoryImpl;
import org.jphototagger.program.module.thumbnails.cache.RenderedThumbnailCache;
import org.jphototagger.program.module.thumbnails.cache.ThumbnailCache;
import org.jphototagger.program.module.thumbnails.cache.XmpCache;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public final class FilesystemRepositoryUpdater {

    public static final FilesystemRepositoryUpdater INSTANCE = new FilesystemRepositoryUpdater();
    private static final Logger LOGGER = Logger.getLogger(FilesystemRepositoryUpdater.class.getName());
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    private FilesystemRepositoryUpdater() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = FileCopiedEvent.class)
    public void fileCopied(FileCopiedEvent evt) {
        File targetFile = evt.getTargetFile();
        if (RepositoryUtil.isSaveOrUpdate(evt)
                && FileFilterUtil.isImageFile(targetFile)) {
            insertFileIntoRepository(targetFile);
        }
    }

    private void insertFileIntoRepository(File targetFile) {
        SaveToOrUpdateFilesInRepositoryImpl inserter = new SaveToOrUpdateFilesInRepositoryImpl(
                Arrays.asList(targetFile), SaveOrUpdate.OUT_OF_DATE);
        inserter.start();
    }

    @EventSubscriber(eventClass = FileDeletedEvent.class)
    public void fileDeleted(FileDeletedEvent evt) {
        File file = evt.getFile();
        if (FileFilterUtil.isImageFile(file)) {
            new DeleteFileThread(file).start();
        }
    }

    @EventSubscriber(eventClass = FileMovedEvent.class)
    public void fileMoved(FileMovedEvent evt) {
        File sourceFile = evt.getSourceFile();
        File targetFile = evt.getTargetFile();
        if (RepositoryUtil.isSaveOrUpdate(evt)
                && FileFilterUtil.isImageFile(sourceFile)
                && FileFilterUtil.isImageFile(targetFile)) {
            new MoveFileThread(sourceFile, targetFile).start();
        }
    }

    @EventSubscriber(eventClass = FileRenamedEvent.class)
    public void fileRenamed(final FileRenamedEvent evt) {
        File sourceFile = evt.getSourceFile();
        File targetFile = evt.getTargetFile();
        if (RepositoryUtil.isSaveOrUpdate(evt)
                && FileFilterUtil.isImageFile(sourceFile)
                && FileFilterUtil.isImageFile(targetFile)) {
            new MoveFileThread(sourceFile, targetFile).start();
        }
    }

    private void updateCaches(final File sourceFile, final File targetFile) {
        ThumbnailCache.INSTANCE.updateFiles(sourceFile, targetFile);
        XmpCache.INSTANCE.updateFiles(sourceFile, targetFile);
        RenderedThumbnailCache.INSTANCE.updateFiles(sourceFile, targetFile);
        GUI.getThumbnailsPanel().renameFile(sourceFile, targetFile);
    }

    private class DeleteFileThread extends Thread {

        private final File file;

        private DeleteFileThread(File file) {
            super("JPhotoTagger: Deleting file from Repository");
            this.file = file;
        }

        @Override
        public void run() {
            if (repo.existsImageFile(file)) {
                repo.deleteImageFiles(Arrays.asList(file));
            }
        }
    }

    private class MoveFileThread extends Thread {

        private final File sourceFile;
        private final File targetFile;

        private MoveFileThread(File sourceFile, File targetFile) {
            super("JPhotoTagger: Renaming file in the Repository");
            this.sourceFile = sourceFile;
            this.targetFile = targetFile;
        }

        @Override
        public void run() {
            LOGGER.log(Level.INFO, "Rename in the repository file ''{0}'' to ''{1}'' and updating caches",
                    new Object[]{sourceFile, targetFile});
            repo.updateRenameImageFile(sourceFile, targetFile);
            updateCaches(sourceFile, targetFile);
        }
    }
}
