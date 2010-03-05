/*
 * JPhotoTagger tags and finds images fast.
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
package de.elmar_baumann.jpt.importer;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.comparator.ComparatorStringAscending;
import de.elmar_baumann.jpt.data.ImageCollection;
import de.elmar_baumann.jpt.database.DatabaseImageCollections;
import de.elmar_baumann.jpt.exporter.ImageCollectionsExporter;
import de.elmar_baumann.jpt.exporter.ImageCollectionsExporter.CollectionWrapper;
import de.elmar_baumann.jpt.factory.ModelFactory;
import de.elmar_baumann.jpt.helper.InsertImageFilesIntoDatabase;
import de.elmar_baumann.jpt.helper.InsertImageFilesIntoDatabase.Insert;
import de.elmar_baumann.jpt.model.ListModelImageCollections;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.view.panels.ProgressBarUpdater;
import de.elmar_baumann.lib.componentutil.ListUtil;
import java.io.File;
import java.util.List;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2010-03-02
 */
public final class ImageCollectionsImporter implements Importer {

    public static final ImageCollectionsImporter INSTANCE = new ImageCollectionsImporter();

    @Override
    public void importFile(File file) {
        try {
            ImageCollectionsExporter.CollectionWrapper wrapper = (CollectionWrapper)
                    XmlObjectImporter.importObject(
                          file, ImageCollectionsExporter.CollectionWrapper.class);

            new ImportThread(wrapper.getCollection()).start();
        } catch (Exception ex) {
            AppLogger.logSevere(ImageCollectionsImporter.class, ex);
        }
    }

    private static class ImportThread extends Thread {
        private final List<ImageCollection> imageCollections;

        public ImportThread(List<ImageCollection> imageCollections) {
            this.imageCollections = imageCollections;
            super.setName("Importing image collections @ " + getClass().getSimpleName());
        }

        @Override
        public void run() {
            for (ImageCollection imageCollection : imageCollections) {
                if (!DatabaseImageCollections.INSTANCE.exists(imageCollection.getName())) {
                    insertIntoDbMissingFiles(imageCollection);
                    if (DatabaseImageCollections.INSTANCE.insert(imageCollection)) {
                        ListModelImageCollections model = ModelFactory.INSTANCE.getModel(ListModelImageCollections.class);
                        ListUtil.insertSorted(model,
                                              imageCollection.getName(),
                                              ComparatorStringAscending.INSTANCE,
                                              ListModelImageCollections.getSpecialCollectionCount(),
                                              model.getSize() - 1);
                    }
                }
            }
        }


        private void insertIntoDbMissingFiles(ImageCollection imageCollection) {
            InsertImageFilesIntoDatabase inserter =
                    new InsertImageFilesIntoDatabase(imageCollection.getFilenames(),
                                                     Insert.OUT_OF_DATE);
            inserter.addProgressListener(new ProgressBarUpdater(JptBundle.INSTANCE.getString("ImageCollectionsImporter.ProgressBar.String")));
            inserter.run(); // Not as thread!
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

    private ImageCollectionsImporter() {
    }
}
