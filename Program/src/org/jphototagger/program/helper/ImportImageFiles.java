package org.jphototagger.program.helper;

import org.jphototagger.lib.generics.Pair;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.database.DatabaseImageCollections;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase.Insert;
import org.jphototagger.program.io.ImageFileFilterer;
import org.jphototagger.program.model.ListModelImageCollections;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.tasks.UserTasks;
import org.jphototagger.program.view.dialogs.ImportImageFilesDialog;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.ProgressBarUpdater;

import java.awt.EventQueue;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    private final List<Pair<File, File>> sourceTargetFiles;
    private final boolean deleteScrFilesAfterCopying;

    private ImportImageFiles(List<Pair<File, File>> sourceTargetFiles, boolean deleteScrFilesAfterCopying) {
        super("JPhotoTagger: Importing image files");
        this.sourceTargetFiles = new ArrayList<Pair<File, File>>(sourceTargetFiles);
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
                sourceDirectories.addAll(FileUtil.getSubDirsRecursive(srcDir));

                List<File> sourceImageFiles = ImageFileFilterer.getImageFilesOfDirectories(sourceDirectories);

                copy(sourceImageFiles, dlg.getTargetDir(), dlg.isDeleteSourceFilesAfterCopying());
            }
        }
    }

    private static void copy(List<File> sourceImageFiles, File targetDir, boolean deleteScrFilesAfterCopying) {
        if (sourceImageFiles.size() > 0) {
            UserTasks.INSTANCE.add(new ImportImageFiles(getSourceTargetFilePairs(sourceImageFiles, targetDir),
                    deleteScrFilesAfterCopying));
        }
    }

    private static List<Pair<File, File>> getSourceTargetFilePairs(Collection<? extends File> sourceFiles,
            File targetDirectory) {
        List<Pair<File, File>> pairs = new ArrayList<Pair<File, File>>(sourceFiles.size());
        String targetDir = targetDirectory.getAbsolutePath();

        for (File sourceFile : sourceFiles) {
            File targetFile = new File(targetDir + File.separator + sourceFile.getName());

            pairs.add(new Pair<File, File>(sourceFile, targetFile));
        }

        return pairs;
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

        if (o instanceof Pair<?, ?>) {
            Pair<?, ?> pair = (Pair<?, ?>) o;
            Object second = pair.getSecond();

            if (second instanceof File) {
                File file = (File) second;

                if (!file.getName().toLowerCase().endsWith(".xmp")) {
                    copiedTargetFiles.add(file);
                    copiedSourceFiles.add((File) pair.getFirst());
                }
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
        InsertImageFilesIntoDatabase inserter = new InsertImageFilesIntoDatabase(copiedTargetFiles, Insert.OUT_OF_DATE);
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
        EventQueue.invokeLater(new Runnable() {
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
