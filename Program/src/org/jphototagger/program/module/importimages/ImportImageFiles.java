package org.jphototagger.program.module.importimages;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;

import org.jphototagger.api.concurrent.SerialTaskExecutor;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.imagecollections.ImageCollection;
import org.jphototagger.domain.repository.ImageCollectionsRepository;
import org.jphototagger.domain.repository.InsertIntoRepository;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.SourceTargetFile;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.misc.InsertImageFilesIntoRepository;
import org.jphototagger.program.module.filesystem.ImageFileFilterer;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.app.ui.AppPanel;
import org.jphototagger.program.app.ui.ProgressBarUpdater;
import org.jphototagger.program.module.filesystem.CopyFiles;

/**
 * Imports image files from a source directory to a target directory.
 *
 * @author Elmar Baumann
 */
public final class ImportImageFiles extends Thread implements ProgressListener {

    private static final String progressBarString = Bundle.getString(ImportImageFiles.class, "ImportImageFiles.Info.ProgressBar");
    private final List<File> copiedTargetFiles = new ArrayList<File>();
    private final List<File> copiedSourceFiles = new ArrayList<File>();
    private final List<SourceTargetFile> sourceTargetFiles;
    private final boolean deleteScrFilesAfterCopying;
    private static final Logger LOGGER = Logger.getLogger(ImportImageFiles.class.getName());

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

                List<File> sourceImageFiles = ImageFileFilterer.getImageFilesOfDirectories(sourceDirectories);

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
        CopyFiles copyFiles = new CopyFiles(sourceTargetFiles, CopyFiles.Options.RENAME_SRC_FILE_IF_TARGET_FILE_EXISTS);
        ProgressBarUpdater pBarUpdater = new ProgressBarUpdater(copyFiles, progressBarString);

        copyFiles.addProgressListener(this);
        copyFiles.addProgressListener(pBarUpdater);
        copyFiles.run();    // Has to run in this thread!
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
        InsertImageFilesIntoRepository inserter = new InsertImageFilesIntoRepository(copiedTargetFiles, InsertIntoRepository.OUT_OF_DATE);
        ProgressBarUpdater pBarUpdater = new ProgressBarUpdater(inserter, progressBarString);

        inserter.addProgressListener(pBarUpdater);
        inserter.run();    // Has to run in this thread!
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
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                AppPanel appPanel = GUI.getAppPanel();

                appPanel.getTabbedPaneSelection().setSelectedComponent(appPanel.getTabSelectionImageCollections());
                GUI.getAppPanel().getListImageCollections().setSelectedValue(
                        ImageCollection.PREVIOUS_IMPORT_NAME, true);
            }
        });
    }

    private void deleteCopiedSourceFiles() {
        for (File file : copiedSourceFiles) {
            LOGGER.log(Level.INFO, "Deleting after import file ''{0}''", file);

            if (!file.delete()) {
                LOGGER.log(Level.WARNING, "Error while deleting file ''{0}''!", file);
            }
        }
    }
}
