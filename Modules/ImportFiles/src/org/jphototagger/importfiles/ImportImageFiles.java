package org.jphototagger.importfiles;

import java.io.File;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.concurrent.SerialTaskExecutor;
import org.jphototagger.api.file.CopyMoveFilesOptions;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.DirectorySelectService;
import org.jphototagger.domain.FileCopyService;
import org.jphototagger.domain.FileImportService;
import org.jphototagger.domain.filefilter.FileFilterUtil;
import org.jphototagger.domain.imagecollections.ImageCollection;
import org.jphototagger.domain.imagecollections.ImageCollectionService;
import org.jphototagger.domain.metadata.exif.ExifInfo;
import org.jphototagger.domain.repository.ImageCollectionsRepository;
import org.jphototagger.domain.repository.SaveOrUpdate;
import org.jphototagger.domain.repository.SaveToOrUpdateFilesInRepository;
import org.jphototagger.domain.thumbnails.ThumbnailsDisplayer;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.SourceTargetFile;
import org.jphototagger.lib.runtime.External;
import org.jphototagger.lib.runtime.ExternalOutput;
import org.jphototagger.lib.runtime.RuntimeUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.ProgressBarUpdater;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileImportService.class)
public final class ImportImageFiles extends Thread implements FileImportService, ProgressListener {

    private static final String progressBarString = Bundle.getString(ImportImageFiles.class, "ImportImageFiles.Info.ProgressBar");
    private static final DecimalFormat SUBDIR_DAY_MONTH_FORMAT = new DecimalFormat();
    private final List<File> copiedTargetFiles = new ArrayList<File>();
    private final List<File> copiedSourceFiles = new ArrayList<File>();
    private final List<SourceTargetFile> sourceTargetFiles;
    private final File targetDirectory;
    private final boolean deleteScrFilesAfterCopying;
    private final File scriptFile;
    private static final int MAX_WAIT_FOR_SCRIPT_EXEC_IN_MILLIS = 240000;
    private final String scriptForRuntime;
    private static final Logger LOGGER = Logger.getLogger(ImportImageFiles.class.getName());

    static {
        SUBDIR_DAY_MONTH_FORMAT.setMinimumIntegerDigits(2);
    }

    public ImportImageFiles() {
        this(Collections.<SourceTargetFile>emptyList(), null, false, null);
    }

    private ImportImageFiles(List<SourceTargetFile> sourceTargetFiles, File targetDirectory,
            boolean deleteScrFilesAfterCopying, File scriptFile) {
        super("JPhotoTagger: Importing image files");
        this.sourceTargetFiles = new ArrayList<SourceTargetFile>(sourceTargetFiles);
        this.deleteScrFilesAfterCopying = deleteScrFilesAfterCopying;
        this.scriptFile = scriptFile;
        this.targetDirectory = targetDirectory;
        scriptForRuntime = scriptFile == null ? "" : RuntimeUtil.quoteForCommandLine(scriptFile);
    }

    public static void importFrom(File sourceDirectory) {
        ImportImageFilesDialog dlg = new ImportImageFilesDialog();

        if (sourceDirectory != null) {
            dlg.setSourceDir(sourceDirectory);
        }

        dlg.setVisible(true);

        if (dlg.isAccepted()) {
            File targetDir = dlg.getTargetDir();
            boolean createSubdirs = dlg.isAutocreateSubdirs();
            boolean deleteSourceFilesAfterCopying = dlg.isDeleteSourceFilesAfterCopying();
            File script = dlg.getScriptFile();
            if (dlg.filesChoosed()) {
                copy(dlg.getSourceFiles(), targetDir, createSubdirs, deleteSourceFilesAfterCopying, script);
            } else {
                List<File> sourceDirectories = new ArrayList<File>();
                File srcDir = dlg.getSourceDir();

                sourceDirectories.add(srcDir);
                sourceDirectories.addAll(FileUtil.getSubDirectoriesRecursive(srcDir, null));

                List<File> sourceImageFiles = FileFilterUtil.getImageFilesOfDirectories(sourceDirectories);

                copy(sourceImageFiles, targetDir, createSubdirs, deleteSourceFilesAfterCopying, script);
            }
        }
    }

    private static void copy(List<File> sourceImageFiles, File targetDir, boolean autoCreateSubdirs,
            boolean deleteScrFilesAfterCopying, File scriptFile) {
        if (sourceImageFiles.size() > 0) {
            SerialTaskExecutor executor = Lookup.getDefault().lookup(SerialTaskExecutor.class);
            List<SourceTargetFile> scrTgtFiles = createSourceTargetFiles(sourceImageFiles, targetDir, autoCreateSubdirs);
            ImportImageFiles importImageFiles = new ImportImageFiles(scrTgtFiles, targetDir, deleteScrFilesAfterCopying, scriptFile);
            executor.addTask(importImageFiles);
        }
    }

    private static List<SourceTargetFile> createSourceTargetFiles(
            Collection<? extends File> sourceFiles, File targetDirectory, boolean autoCreateSubdirs) {
        List<SourceTargetFile> sourceTargetFiles = new ArrayList<SourceTargetFile>(sourceFiles.size());
        String targetDir = targetDirectory.getAbsolutePath();
        for (File sourceFile : sourceFiles) {
            File targetFile = createTargetFile(sourceFile, targetDir, autoCreateSubdirs);
            ensureTargetDirExists(targetFile);
            sourceTargetFiles.add(new SourceTargetFile(sourceFile, targetFile));
        }
        return sourceTargetFiles;
    }

    private static void ensureTargetDirExists(File targetFile) {
        File parentFile = targetFile.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            try {
                FileUtil.ensureDirectoryExists(parentFile);
            } catch (Throwable t) {
                Logger.getLogger(ImportImageFiles.class.getName()).log(Level.SEVERE, null, t);
            }
        }
    }

    private static File createTargetFile(File sourceFile, String targetDir, boolean autoCreateSubdirs) {
        String subdir = autoCreateSubdirs
                ? getDateDirString(sourceFile) + File.separator
                : "";
        return new File(targetDir + File.separator + subdir + sourceFile.getName());
    }

    private static String getDateDirString(File file) {
        ExifInfo exifInfo = Lookup.getDefault().lookup(ExifInfo.class);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(exifInfo.getTimeTakenInMillis(file));
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        String delimiter = "-";
        return String.valueOf(year)
                + delimiter + SUBDIR_DAY_MONTH_FORMAT.format(month)
                + delimiter + SUBDIR_DAY_MONTH_FORMAT.format(day);
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
                executeScriptFileForCopiedFile(targetFile);
            }
        }
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        if (scriptFile != null) {
            selectTargetDirectory();
        } else {
            addFilesToCollection();
        }
        if (deleteScrFilesAfterCopying) {
            deleteCopiedSourceFiles();
        }
    }

    private void selectTargetDirectory() {
        DirectorySelectService service = Lookup.getDefault().lookup(DirectorySelectService.class);

        if (service != null) {
            service.selectDirectory(targetDirectory);
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
            // Needs to be in the DB for adding to an image collection
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
        ImageCollectionService imageCollectionService = Lookup.getDefault().lookup(ImageCollectionService.class);
        if (imageCollectionService != null) {
            imageCollectionService.selectPreviousImportedFiles();
            ThumbnailsDisplayer thumbnailsDisplayer = Lookup.getDefault().lookup(ThumbnailsDisplayer.class);
            if (thumbnailsDisplayer != null) {
                thumbnailsDisplayer.refresh();
            }
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
