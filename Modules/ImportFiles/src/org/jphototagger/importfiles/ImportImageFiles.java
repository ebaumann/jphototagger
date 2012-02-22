package org.jphototagger.importfiles;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.file.CopyMoveFilesOptions;
import org.jphototagger.api.progress.MainWindowProgressBarProvider;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.DirectorySelectService;
import org.jphototagger.domain.FileCopyService;
import org.jphototagger.domain.FileImportService;
import org.jphototagger.domain.imagecollections.ImageCollection;
import org.jphototagger.domain.imagecollections.ImageCollectionService;
import org.jphototagger.domain.repository.ImageCollectionsRepository;
import org.jphototagger.domain.repository.SaveOrUpdate;
import org.jphototagger.domain.repository.SaveToOrUpdateFilesInRepository;
import org.jphototagger.domain.thumbnails.ThumbnailsDisplayer;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.SourceTargetFile;
import org.jphototagger.lib.runtime.External;
import org.jphototagger.lib.runtime.ExternalOutput;
import org.jphototagger.lib.runtime.RuntimeUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.ProgressBarUpdater;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileImportService.class)
public final class ImportImageFiles implements FileImportService {

    @Override
    public void importFilesFromDirectory(File directory) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }
        importFrom(directory);
    }

    public static void importFrom(File sourceDirectory) {
        ImportThread importThread = new ImportThread(sourceDirectory);
        importThread.start();
    }

    private static class ImportThread extends Thread implements ProgressListener {

        private static final String PROGRESS_BAR_STRING = Bundle.getString(ImportThread.class, "ImportImageFiles.Info.ProgressBar");
        private static final Logger LOGGER = Logger.getLogger(ImportThread.class.getName());
        private static final int MAX_WAIT_FOR_SCRIPT_EXEC_IN_MILLIS = 240000;
        private final MainWindowProgressBarProvider progressBarProvider = Lookup.getDefault().lookup(MainWindowProgressBarProvider.class);
        private final List<File> copiedTargetFiles = new ArrayList<File>();
        private final List<File> copiedSourceFiles = new ArrayList<File>();
        private final File sourceDirectory;
        private File targetDirectory;
        private List<SourceTargetFile> sourceTargetFiles;
        private boolean deleteSourceFilesAfterCopying;
        private File scriptFile;
        private String scriptForRuntime;
        private FileCopyService copyService;
        private ProgressBarUpdater copyServiceProgressBarUpdater;

        private ImportThread(File sourceDirectory) {
            super("JPhotoTagger: Importing Image Files");
            this.sourceDirectory = sourceDirectory;
        }

        @Override
        public void run() {
            ImportImageFilesDialog importDialog = new ImportImageFilesDialog();
            if (sourceDirectory != null) {
                importDialog.setSourceDirectory(sourceDirectory);
            }
            importDialog.setVisible(true);
            if (importDialog.isAccepted()) {
                ImportData importData = importDialog.createImportData();
                if (importData.hasSourceFiles()) {
                    prepareCopy(importData);
                    copy();
                } else {
                    MessageDisplayer.information(null,
                            Bundle.getString(ImportThread.class, "ImportImageFiles.Info.NoSourceFiles"));
                }
            }
        }

        private void prepareCopy(ImportData importData) {
            initFileRenameStrategy(importData);
            createSourceTargetFiles(importData);
            targetDirectory = importData.getTargetDirectory();
            deleteSourceFilesAfterCopying = importData.isDeleteSourceFilesAfterCopying();
            scriptFile = importData.getScriptFile();
            scriptForRuntime = scriptFile == null
                    ? ""
                    : RuntimeUtil.quoteForCommandLine(scriptFile);
        }

        private void initFileRenameStrategy(ImportData importData) {
            if (importData.hasFileRenameStrategy()) {
                importData.getFileRenameStrategy().init();
            }
        }

        private void createSourceTargetFiles(ImportData importData) {
            progressBarProvider.progressStarted(createIndeterminateProgressEvent());
            try {
                sourceTargetFiles = new ArrayList<SourceTargetFile>(importData.getSourceFileCount());
                List<File> sourceFiles = importData.getSourceFiles();
                Collections.sort(sourceFiles, ExifDateTimeOriginalAscendingComparator.INSTANCE);
                for (File sourceFile : sourceFiles) {
                    File targetFile = createTargetFile(sourceFile, importData);
                    ensureTargetDirExists(targetFile);
                    SourceTargetFile sourceTargetFile = new SourceTargetFile(sourceFile, targetFile);
                    sourceTargetFile.setUserObject(importData.getXmp());
                    sourceTargetFiles.add(sourceTargetFile);
                }
            } finally {
                progressBarProvider.progressEnded(this);
            }
        }

        private File createTargetFile(File sourceFile, ImportData importData) {
            String targetSubdirPathname = importData.hasSubdirectoryCreateStrategy()
                    ? importData.getSubdirectoryCreateStrategy().suggestSubdirectoryName(sourceFile)
                    : "";
            String targetDirPathname = importData.getTargetDirectory().getAbsolutePath();
            targetDirPathname = targetSubdirPathname.isEmpty()
                    ? targetDirPathname
                    : targetDirPathname + File.separator + targetSubdirPathname;
            if (importData.hasFileRenameStrategy()) {
                return importData.getFileRenameStrategy().suggestNewFile(sourceFile, targetDirPathname);
            } else {
                String sourceFilename = sourceFile.getName();
                return new File(targetDirPathname + File.separator + sourceFilename);
            }
        }

        private void ensureTargetDirExists(File targetFile) {
            File parentFile = targetFile.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                try {
                    FileUtil.ensureDirectoryExists(parentFile);
                } catch (Throwable t) {
                    Logger.getLogger(ImportImageFiles.class.getName()).log(Level.SEVERE, null, t);
                }
            }
        }

        private ProgressEvent createIndeterminateProgressEvent() {
            return new ProgressEvent.Builder()
                    .source(this)
                    .indeterminate(true)
                    .stringPainted(true)
                    .stringToPaint(PROGRESS_BAR_STRING)
                    .build();
        }

        private void copy() {
            copyService = Lookup.getDefault().lookup(FileCopyService.class).createInstance(
                    sourceTargetFiles, CopyMoveFilesOptions.RENAME_SOURCE_FILE_IF_TARGET_FILE_EXISTS);
            copyServiceProgressBarUpdater = new ProgressBarUpdater(copyService, PROGRESS_BAR_STRING);
            copyService.addProgressListener(this);
            copyService.addProgressListener(copyServiceProgressBarUpdater);
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
                boolean isXmpFile = targetFile.getName().toLowerCase().endsWith(".xmp");
                if (!isXmpFile) {
                    copiedTargetFiles.add(targetFile);
                    copiedSourceFiles.add(sourceTargetFile.getSourceFile());
                    executeScriptFileForCopiedFile(targetFile);
                }
            }
        }

        @Override
        public void progressEnded(ProgressEvent evt) {
            if (copyServiceProgressBarUpdater != null) {
                copyServiceProgressBarUpdater.progressEnded(null);
            }
            try {
                progressBarProvider.progressStarted(createIndeterminateProgressEvent());
                if (scriptFile != null) {
                    selectTargetDirectory();
                } else {
                    addFilesToCollection();
                }
                if (deleteSourceFilesAfterCopying) {
                    deleteCopiedSourceFiles();
                }
            } finally {
                progressBarProvider.progressEnded(this  );
            }
        }

        private void selectTargetDirectory() {
            DirectorySelectService service = Lookup.getDefault().lookup(DirectorySelectService.class);
            if (service != null) {
                service.selectDirectory(targetDirectory); // Will be invoked into EDT
            }
        }

        private void executeScriptFileForCopiedFile(File copiedFileInTarget) {
            if (scriptFile == null) {
                return;
            }
            String fileAsScriptArgument = RuntimeUtil.quoteForCommandLine(copiedFileInTarget);
            String command = scriptForRuntime + " " + fileAsScriptArgument;
            logScriptCommand(copiedFileInTarget, command);
            ExternalOutput execResult = External.executeGetOutput(command, MAX_WAIT_FOR_SCRIPT_EXEC_IN_MILLIS);
            logScriptErrors(execResult);
        }

        private void logScriptCommand(File copiedFileInTarget, String command) {
            LOGGER.log(Level.INFO, "Executing script file ''{0}'' for copied file ''{1}'' with command ''{2}'' and waiting for a maximum of {3} milliseconds",
                    new Object[]{scriptFile, copiedFileInTarget, command, MAX_WAIT_FOR_SCRIPT_EXEC_IN_MILLIS});
        }

        private void logScriptErrors(ExternalOutput execResult) {
            byte[] errorStream = execResult.getErrorStream();
            if (errorStream != null && errorStream.length > 0) {
                LOGGER.log(Level.WARNING, new String(errorStream));
            }
        }

        private void addFilesToCollection() {
            if (!copiedTargetFiles.isEmpty()) {
                // Needs to be present in the DB for adding to an image collection
                insertCopiedFilesIntoDb();
            }
            insertCopiedFilesAsCollectionIntoDb();
            selectPrevImportCollection();
        }

        private void insertCopiedFilesIntoDb() {
            SaveToOrUpdateFilesInRepository inserter = Lookup.getDefault().lookup(SaveToOrUpdateFilesInRepository.class)
                    .createInstance(copiedTargetFiles, SaveOrUpdate.OUT_OF_DATE);
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
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    ImageCollectionService imageCollectionService = Lookup.getDefault().lookup(ImageCollectionService.class);
                    if (imageCollectionService != null) {
                        imageCollectionService.selectPreviousImportedFiles();
                        ThumbnailsDisplayer thumbnailsDisplayer = Lookup.getDefault().lookup(ThumbnailsDisplayer.class);
                        if (thumbnailsDisplayer != null) {
                            thumbnailsDisplayer.refresh();
                        }
                    }
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
}
