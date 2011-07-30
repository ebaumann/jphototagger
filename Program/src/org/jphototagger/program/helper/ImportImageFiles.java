package org.jphototagger.program.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jphototagger.domain.database.InsertIntoDatabase;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.event.ProgressEvent;
import org.jphototagger.lib.event.listener.ProgressListener;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.SourceTargetFile;
import org.jphototagger.program.app.logging.AppLogger;
import org.jphototagger.program.database.DatabaseImageCollections;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.io.ImageFileFilterer;
import org.jphototagger.program.model.ListModelImageCollections;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.tasks.UserTasks;
import org.jphototagger.program.view.dialogs.ImportImageFilesDialog;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.ProgressBarUpdater;

/**
 * Imports image files from a source directory to a target directory.
 *
 * Does not import XMP sidecar files. After import the images will be inserted
 * into the {@link DatabaseImageFiles} and set as image collection
 * {@link ListModelImageCollections#NAME_IMAGE_COLLECTION_PREV_IMPORT}.
 *
 * @author Elmar Baumann
 */
public final class ImportImageFiles extends Thread implements ProgressListener {

    private static final String progressBarString = JptBundle.INSTANCE.getString("ImportImageFiles.Info.ProgressBar");
    private final List<File> copiedTargetFiles = new ArrayList<File>();
    private final List<File> copiedSourceFiles = new ArrayList<File>();
    private final List<SourceTargetFile> sourceTargetFiles;
    private final boolean deleteScrFilesAfterCopying;

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
            UserTasks.INSTANCE.add(new ImportImageFiles(getSourceTargetFiles(sourceImageFiles, targetDir),
                    deleteScrFilesAfterCopying));
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
        copyFiles.run();    // run in this thread!
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
        InsertImageFilesIntoDatabase inserter = new InsertImageFilesIntoDatabase(copiedTargetFiles, InsertIntoDatabase.OUT_OF_DATE);
        ProgressBarUpdater pBarUpdater = new ProgressBarUpdater(inserter, progressBarString);

        inserter.addProgressListener(pBarUpdater);
        inserter.run();    // run in this thread!
    }

    private void insertCopiedFilesAsCollectionIntoDb() {
        String collectionName = ListModelImageCollections.NAME_IMAGE_COLLECTION_PREV_IMPORT;
        DatabaseImageCollections db = DatabaseImageCollections.INSTANCE;
        List<File> prevCollectionFiles = db.getImageFilesOf(collectionName);

        if (!prevCollectionFiles.isEmpty()) {
            int delCount = db.deleteImagesFrom(collectionName, prevCollectionFiles);

            if (delCount != prevCollectionFiles.size()) {
                AppLogger.logWarning(getClass(), "ImportImageFiles.Error.DeleteCollectionImages", collectionName);

                return;
            }
        }

        db.insert(collectionName, copiedTargetFiles);
    }

    private void selectPrevImportCollection() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                AppPanel appPanel = GUI.getAppPanel();

                appPanel.getTabbedPaneSelection().setSelectedComponent(appPanel.getTabSelectionImageCollections());
                GUI.getAppPanel().getListImageCollections().setSelectedValue(
                        ListModelImageCollections.NAME_IMAGE_COLLECTION_PREV_IMPORT, true);
            }
        });
    }

    private void deleteCopiedSourceFiles() {
        for (File file : copiedSourceFiles) {
            AppLogger.logInfo(ImportImageFiles.class, "ImportImageFiles.Info.DeleteCopiedFile", file);

            if (!file.delete()) {
                AppLogger.logWarning(ImportImageFiles.class, progressBarString,
                        "ImportImageFiles.Error.DeleteCopiedFile", file);
            }
        }
    }
}
