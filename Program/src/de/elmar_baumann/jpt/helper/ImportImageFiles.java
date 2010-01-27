/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.controller.filesystem.ControllerImportImageFiles;
import de.elmar_baumann.jpt.database.DatabaseImageCollections;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.helper.InsertImageFilesIntoDatabase.Insert;
import de.elmar_baumann.jpt.io.ImageFilteredDirectory;
import de.elmar_baumann.jpt.model.ListModelImageCollections;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
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
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-24
 */
public final class ImportImageFiles extends Thread implements ProgressListener {

    private final List<File>         copiedFiles        = new ArrayList<File>();
    private final ProgressBarUpdater progressBarUpdater = new ProgressBarUpdater(Bundle.getString("ControllerImportImageFiles.Info.ProgressBar"));
    private final CopyFiles          copier;

    public static void importFrom(File sourceDirectory) {
        ImportImageFilesDialog dlg = new ImportImageFilesDialog();

        if (sourceDirectory != null) {
            dlg.setSourceDir(sourceDirectory);
        }

        dlg.setVisible(true);
        if (dlg.isAccepted()) {
            copy(dlg.getSourceDir(), dlg.getTargetDir());
        }

    }

    private static void copy(File srcDir, File targetDir) {
        List<File> sourceDirectories = new ArrayList<File>();

        sourceDirectories.add(srcDir);
        sourceDirectories.addAll(FileUtil.getSubdirectoriesRecursive(srcDir));

        List<File> sourceImageFiles = ImageFilteredDirectory.getImageFilesOfDirectories(sourceDirectories);

        if (sourceImageFiles.size() > 0) {
            UserTasks.INSTANCE.add(
                    new ImportImageFiles(getSourceTargetFilePairs(sourceImageFiles, targetDir)));
        }
    }

    private static List<Pair<File, File>> getSourceTargetFilePairs(Collection<? extends File> sourceFiles, File targetDirectory) {
        List<Pair<File, File>> pairs     = new ArrayList<Pair<File, File>>(sourceFiles.size());
        String                 targetDir = targetDirectory.getAbsolutePath();

        for (File sourceFile : sourceFiles) {
            File targetFile = new File(targetDir + File.separator + sourceFile.getName());

            pairs.add(new Pair<File, File>(sourceFile, targetFile));
        }

        return pairs;
    }

    public ImportImageFiles(List<Pair<File, File>> sourceTargetFiles) {
        copier = new CopyFiles(sourceTargetFiles, CopyFiles.Options.RENAME_SRC_FILE_IF_TARGET_FILE_EXISTS);
        setName("Importing image files @ " + ControllerImportImageFiles.class.getSimpleName());
    }

    @Override
    public void run() {
        copier.addProgressListener(this);
        copier.addProgressListener(progressBarUpdater);
        copier.run(); // No separate thread!
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
                String filename = file.getName().toLowerCase();
                if (!filename.endsWith(".xmp")) {
                    copiedFiles.add(file);
                }
            }
        }
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        addFilesToCollection();
    }

    private void addFilesToCollection() {
        if (copiedFiles.isEmpty()) return;
        insertCopiedFilesIntoDb();
        if (insertCopiedFilesAsCollectionIntoDb()) {
            selectPrevImportCollection();
        }
    }

    private void insertCopiedFilesIntoDb() {
        InsertImageFilesIntoDatabase dbInserter = new InsertImageFilesIntoDatabase(
                        FileUtil.getAsFilenames(copiedFiles), Insert.OUT_OF_DATE);

        dbInserter.run(); // No separate thread!
    }

    private boolean insertCopiedFilesAsCollectionIntoDb() {
        String                   collectionName      = ListModelImageCollections.NAME_IMAGE_COLLECTION_PREV_IMPORT;
        DatabaseImageCollections db                  = DatabaseImageCollections.INSTANCE;
        List<String>             prevCollectionFiles = db.getFilenamesOf(collectionName);

        if (!prevCollectionFiles.isEmpty()) {
            db.deleteImagesFrom(collectionName, prevCollectionFiles);
        }
        return db.insert(collectionName, FileUtil.getAsFilenames(copiedFiles));
    }

    private void selectPrevImportCollection() {
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();
        appPanel.getTabbedPaneSelection().setSelectedComponent(appPanel.getTabSelectionImageCollections());
        GUI.INSTANCE.getAppPanel().getListImageCollections().setSelectedValue(
                ListModelImageCollections.NAME_IMAGE_COLLECTION_PREV_IMPORT,
                true);
    }
}
