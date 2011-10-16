package org.jphototagger.importfiles;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;

import org.jphototagger.api.concurrent.SerialTaskExecutor;
import org.jphototagger.api.file.CopyMoveFilesOptions;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.FileCopyService;
import org.jphototagger.domain.FileImportService;
import org.jphototagger.domain.filefilter.FileFilterUtil;
import org.jphototagger.domain.imagecollections.ImageCollection;
import org.jphototagger.domain.imagecollections.ImageCollectionService;
import org.jphototagger.domain.repository.ImageCollectionsRepository;
import org.jphototagger.domain.repository.SaveOrUpdate;
import org.jphototagger.domain.repository.SaveToOrUpdateFilesInRepository;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.SourceTargetFile;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.ProgressBarUpdater;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileImportService.class)
public final class ImportImageFiles extends Thread implements FileImportService, ProgressListener {

    private static final String progressBarString = Bundle.getString(ImportImageFiles.class, "ImportImageFiles.Info.ProgressBar");
    private final List<File> copiedTargetFiles = new ArrayList<File>();
    private final List<File> copiedSourceFiles = new ArrayList<File>();
    private final List<SourceTargetFile> sourceTargetFiles;
    private final boolean deleteScrFilesAfterCopying;
    private static final Logger LOGGER = Logger.getLogger(ImportImageFiles.class.getName());

    public ImportImageFiles() {
        super("JPhotoTagger: Importing image files");
        sourceTargetFiles = Collections.emptyList();
        deleteScrFilesAfterCopying = false;
    }

    private ImportImageFiles(List<SourceTargetFile> sourceTargetFiles, boolean deleteScrFilesAfterCopying) {
        super("JPhotoTagger: Importing image files");
        this.sourceTargetFiles = new ArrayList<SourceTargetFile>(sourceTargetFiles);
        this.deleteScrFilesAfterCopying = deleteScrFilesAfterCopying;
    }

    public static void importFrom(File sourceDirectory) {
        ImportImageFilesDialog dlg = new ImportImageFilesDialog();

        if (sourceDirectory != null) {
            dlg.setSourceDir(sourceDirectory);
        }

        dlg.setVisible(true);

        if (dlg.isAccepted()) {
            if (dlg.filesChoosed()) {
                copy(dlg.getSourceFiles(), dlg.getTargetDir(), dlg.isDeleteSourceFilesAfterCopying());
            } else {
                List<File> sourceDirectories = new ArrayList<File>();
                File srcDir = dlg.getSourceDir();

                sourceDirectories.add(srcDir);
                sourceDirectories.addAll(FileUtil.getSubDirectoriesRecursive(srcDir, null));

                List<File> sourceImageFiles = FileFilterUtil.getImageFilesOfDirectories(sourceDirectories);

                copy(sourceImageFiles, dlg.getTargetDir(), dlg.isDeleteSourceFilesAfterCopying());
            }
        }
    }

    private static void copy(List<File> sourceImageFiles, File targetDir, boolean deleteScrFilesAfterCopying) {
        if (sourceImageFiles.size() > 0) {
            SerialTaskExecutor executor = Lookup.getDefault().lookup(SerialTaskExecutor.class);
            ImportImageFiles importImageFiles =
                    new ImportImageFiles(getSourceTargetFiles(sourceImageFiles, targetDir), deleteScrFilesAfterCopying);

            executor.addTask(importImageFiles);
        }
    }

    private static List<SourceTargetFile> getSourceTargetFiles(Collection<? extends File> sourceFiles, File targetDirectory) {
        List<SourceTargetFile> sourceTargetFiles = new ArrayList<SourceTargetFile>(sourceFiles.size());
        String targetDir = targetDirectory.getAbsolutePath();

        for (File sourceFile : sourceFiles) {
            File targetFile = new File(targetDir + File.separator + sourceFile.getName());

            sourceTargetFiles.add(new SourceTargetFile(sourceFile, targetFile));
        }

        return sourceTargetFiles;
    }

    @Override
    public void run() {
        FileCopyService copyService = Lookup.getDefault().lookup(FileCopyService.class).createInstance(sourceTargetFiles, CopyMoveFilesOptions.RENAME_SOURCE_FILE_IF_TARGET_FILE_EXISTS);
        ProgressBarUpdater pBarUpdater = new ProgressBarUpdater(copyService, progressBarString);

        copyService.addProgressListener(this);
        copyService.addProgressListener(pBarUpdater);
        copyService.copyWaitForTermination();
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        // ignore
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        Object o = evt.getInfo();

        if (o instanceof SourceTargetFile) {
            SourceTargetFile sourceTargetFile = (SourceTargetFile) o;
            File targetFile = sourceTargetFile.getTargetFile();

            if (!targetFile.getName().toLowerCase().endsWith(".xmp")) {
                copiedTargetFiles.add(targetFile);
                copiedSourceFiles.add(sourceTargetFile.getSourceFile());
            }
        }
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        addFilesToCollection();

        if (deleteScrFilesAfterCopying) {
            deleteCopiedSourceFiles();
        }
    }

    private void addFilesToCollection() {
        if (!copiedTargetFiles.isEmpty()) {

            // Needs to be in the DB to be added to an image collection
            insertCopiedFilesIntoDb();
        }

        insertCopiedFilesAsCollectionIntoDb();
        selectPrevImportCollection();
    }

    private void insertCopiedFilesIntoDb() {
        SaveToOrUpdateFilesInRepository inserter = Lookup.getDefault().lookup(SaveToOrUpdateFilesInRepository.class).createInstance(copiedTargetFiles, SaveOrUpdate.OUT_OF_DATE);
        ProgressBarUpdater pBarUpdater = new ProgressBarUpdater(inserter, progressBarString);

        inserter.addProgressListener(pBarUpdater);
        inserter.saveOrUpdateWaitForTermination();
    }

    private void insertCopiedFilesAsCollectionIntoDb() {
        String collectionName = ImageCollection.PREVIOUS_IMPORT_NAME;
        ImageCollectionsRepository repo = Lookup.getDefault().lookup(ImageCollectionsRepository.class);
        List<File> prevCollectionFiles = repo.findImageFilesOfImageCollection(collectionName);

        if (!prevCollectionFiles.isEmpty()) {
            int delCount = repo.deleteImagesFromImageCollection(collectionName, prevCollectionFiles);

            if (delCount != prevCollectionFiles.size()) {
                LOGGER.log(Level.WARNING, "Could not delete all images from ''{0}''!", collectionName);

                return;
            }
        }

        repo.saveImageCollection(collectionName, copiedTargetFiles);
    }

    private void selectPrevImportCollection() {
        ImageCollectionService service = Lookup.getDefault().lookup(ImageCollectionService.class);
        if (service != null) {
            service.selectPreviousImportedFiles();
        }
    }

    private void deleteCopiedSourceFiles() {
        for (File file : copiedSourceFiles) {
            LOGGER.log(Level.INFO, "Deleting after import file ''{0}''", file);

            if (!file.delete()) {
                LOGGER.log(Level.WARNING, "Error while deleting file ''{0}''!", file);
            }
        }
    }

    @Override
    public void importFilesFromDirectory(File directory) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        importFrom(directory);
    }
}
