/*
 * @(#)ImageCollectionsImporter.java    Created on 2010-03-02
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

package org.jphototagger.program.importer;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.comparator.ComparatorStringAscending;
import org.jphototagger.program.data.ImageCollection;
import org.jphototagger.program.database.DatabaseImageCollections;
import org.jphototagger.program.exporter.ImageCollectionsExporter;
import org.jphototagger.program.exporter.ImageCollectionsExporter.CollectionWrapper;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase.Insert;
import org.jphototagger.program.model.ListModelImageCollections;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.panels.ProgressBarUpdater;
import org.jphototagger.lib.componentutil.ListUtil;

import java.io.File;

import java.util.List;

import javax.swing.filechooser.FileFilter;
import javax.swing.Icon;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class ImageCollectionsImporter implements Importer {
    public static final ImageCollectionsImporter INSTANCE =
        new ImageCollectionsImporter();

    private ImageCollectionsImporter() {}

    @Override
    public void importFile(File file) {
        try {
            ImageCollectionsExporter.CollectionWrapper wrapper =
                (CollectionWrapper) XmlObjectImporter.importObject(file,
                    ImageCollectionsExporter.CollectionWrapper.class);

            new ImportThread(wrapper.getCollection()).start();
        } catch (Exception ex) {
            AppLogger.logSevere(ImageCollectionsImporter.class, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return ImageCollectionsExporter.INSTANCE.getFileFilter();
    }

    @Override
    public String getDisplayName() {
        return ImageCollectionsExporter.INSTANCE.getDisplayName();
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_import.png");
    }

    @Override
    public String getDefaultFilename() {
        return ImageCollectionsExporter.INSTANCE.getDefaultFilename();
    }

    private static class ImportThread extends Thread {
        private final List<ImageCollection> imageCollections;

        public ImportThread(List<ImageCollection> imageCollections) {
            this.imageCollections = imageCollections;
            super.setName("Importing image collections @ "
                          + getClass().getSimpleName());
        }

        @Override
        public void run() {
            for (ImageCollection imageCollection : imageCollections) {
                if (!DatabaseImageCollections.INSTANCE.exists(
                        imageCollection.getName())) {
                    insertIntoDbMissingFiles(imageCollection);

                    if (DatabaseImageCollections.INSTANCE.insert(
                            imageCollection)) {
                        ListModelImageCollections model =
                            ModelFactory.INSTANCE.getModel(
                                ListModelImageCollections.class);

                        ListUtil
                            .insertSorted(model, imageCollection
                                .getName(), ComparatorStringAscending
                                .INSTANCE, ListModelImageCollections
                                .getSpecialCollectionCount(), model.getSize()
                                    - 1);
                    }
                }
            }
        }

        private void insertIntoDbMissingFiles(ImageCollection imageCollection) {
            InsertImageFilesIntoDatabase inserter =
                new InsertImageFilesIntoDatabase(imageCollection.getFiles(),
                    Insert.OUT_OF_DATE);

            inserter.addProgressListener(
                new ProgressBarUpdater(
                    JptBundle.INSTANCE.getString(
                        "ImageCollectionsImporter.ProgressBar.String")));
            inserter.run();    // Not as thread!
        }
    }
}
