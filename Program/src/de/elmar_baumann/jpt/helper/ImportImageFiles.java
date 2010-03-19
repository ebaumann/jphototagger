/*
 * @(#)ImportImageFiles.java    Created on 2010-01-24
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.controller.filesystem.ControllerImportImageFiles;
import de.elmar_baumann.jpt.database.DatabaseImageCollections;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.helper.InsertImageFilesIntoDatabase.Insert;
import de.elmar_baumann.jpt.io.ImageFilteredDirectory;
import de.elmar_baumann.jpt.model.ListModelImageCollections;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.tasks.UserTasks;
import de.elmar_baumann.jpt.view.dialogs.ImportImageFilesDialog;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.panels.ProgressBarUpdater;
import de.elmar_baumann.lib.generics.Pair;
import de.elmar_baumann.lib.io.FileUtil;

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
 * @author  Elmar Baumann
 */
public final class ImportImageFiles extends Thread implements ProgressListener {
    private final List<File>    copiedTargetFiles = new ArrayList<File>();
    private final List<File>    copiedSourceFiles = new ArrayList<File>();
    private static final String progressBarString =
        JptBundle.INSTANCE.getString("ImportImageFiles.Info.ProgressBar");
    private final ProgressBarUpdater progressBarUpdater =
        new ProgressBarUpdater(progressBarString);
    private final CopyFiles copier;
    private final boolean   deleteScrFilesAfterCopying;

    public static void importFrom(File sourceDirectory) {
        ImportImageFilesDialog dlg = new ImportImageFilesDialog();

        if (sourceDirectory != null) {
            dlg.setSourceDir(sourceDirectory);
        }

        dlg.setVisible(true);

        if (dlg.isAccepted()) {
            if (dlg.filesChoosed()) {
                copy(dlg.getSourceFiles(), dlg.getTargetDir(),
                     dlg.isDeleteSourceFilesAfterCopying());
            } else {
                List<File> sourceDirectories = new ArrayList<File>();
                File       srcDir            = dlg.getSourceDir();

                sourceDirectories.add(srcDir);
                sourceDirectories.addAll(
                    FileUtil.getSubdirectoriesRecursive(srcDir));

                List<File> sourceImageFiles =
                    ImageFilteredDirectory.getImageFilesOfDirectories(
                        sourceDirectories);

                copy(sourceImageFiles, dlg.getTargetDir(),
                     dlg.isDeleteSourceFilesAfterCopying());
            }
        }
    }

    private static void copy(List<File> sourceImageFiles, File targetDir,
                             boolean deleteScrFilesAfterCopying) {
        if (sourceImageFiles.size() > 0) {
            UserTasks.INSTANCE.add(
                new ImportImageFiles(
                    getSourceTargetFilePairs(sourceImageFiles, targetDir),
                    deleteScrFilesAfterCopying));
        }
    }

    private static List<Pair<File, File>> getSourceTargetFilePairs(
            Collection<? extends File> sourceFiles, File targetDirectory) {
        List<Pair<File, File>> pairs = new ArrayList<Pair<File,
                                           File>>(sourceFiles.size());
        String targetDir = targetDirectory.getAbsolutePath();

        for (File sourceFile : sourceFiles) {
            File targetFile = new File(targetDir + File.separator
                                       + sourceFile.getName());

            pairs.add(new Pair<File, File>(sourceFile, targetFile));
        }

        return pairs;
    }

    private ImportImageFiles(List<Pair<File, File>> sourceTargetFiles,
                             boolean deleteScrFilesAfterCopying) {
        this.deleteScrFilesAfterCopying = deleteScrFilesAfterCopying;
        copier                          = new CopyFiles(sourceTargetFiles,
                CopyFiles.Options.RENAME_SRC_FILE_IF_TARGET_FILE_EXISTS);
        setName("Importing image files @ "
                + ControllerImportImageFiles.class.getSimpleName());
    }

    @Override
    public void run() {
        copier.addProgressListener(this);
        copier.addProgressListener(progressBarUpdater);
        copier.run();    // No separate thread!
    }

    @Override
    public void progressStarted(ProgressEvent evt) {

        // ignore
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        Object o = evt.getInfo();

        if (o instanceof Pair<?, ?>) {
            Pair<?, ?> pair   = (Pair<?, ?>) o;
            Object     second = pair.getSecond();

            if (second instanceof File) {
                File   file     = (File) second;
                String filename = file.getName().toLowerCase();

                if (!filename.endsWith(".xmp")) {
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
            insertCopiedFilesIntoDb();    // Needs to be in the DB to be added to an image collection
        }

        insertCopiedFilesAsCollectionIntoDb();
        selectPrevImportCollection();
    }

    private void insertCopiedFilesIntoDb() {
        InsertImageFilesIntoDatabase dbInserter =
            new InsertImageFilesIntoDatabase(
                FileUtil.getAsFilenames(copiedTargetFiles), Insert.OUT_OF_DATE);

        dbInserter.addProgressListener(progressBarUpdater);
        dbInserter.run();    // No separate thread!
    }

    private void insertCopiedFilesAsCollectionIntoDb() {
        String collectionName =
            ListModelImageCollections.NAME_IMAGE_COLLECTION_PREV_IMPORT;
        DatabaseImageCollections db                  =
            DatabaseImageCollections.INSTANCE;
        List<String>             prevCollectionFiles =
            db.getFilenamesOf(collectionName);

        if (!prevCollectionFiles.isEmpty()) {
            int delCount = db.deleteImagesFrom(collectionName,
                                               prevCollectionFiles);

            if (delCount != prevCollectionFiles.size()) {
                AppLogger.logWarning(
                    getClass(),
                    "ImportImageFiles.Error.DeleteCollectionImages",
                    collectionName);

                return;
            }
        }

        db.insert(collectionName, FileUtil.getAsFilenames(copiedTargetFiles));
    }

    private void selectPrevImportCollection() {
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();

        appPanel.getTabbedPaneSelection().setSelectedComponent(
            appPanel.getTabSelectionImageCollections());
        GUI.INSTANCE.getAppPanel().getListImageCollections().setSelectedValue(
            ListModelImageCollections.NAME_IMAGE_COLLECTION_PREV_IMPORT, true);
    }

    private void deleteCopiedSourceFiles() {
        for (File file : copiedSourceFiles) {
            AppLogger.logInfo(ImportImageFiles.class,
                              "ImportImageFiles.Info.DeleteCopiedFile", file);

            if (!file.delete()) {
                AppLogger.logWarning(ImportImageFiles.class, progressBarString,
                                     "ImportImageFiles.Error.DeleteCopiedFile",
                                     file);
            }
        }
    }
}
