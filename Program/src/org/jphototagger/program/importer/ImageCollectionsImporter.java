package org.jphototagger.program.importer;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

import org.jphototagger.domain.database.InsertIntoDatabase;
import org.jphototagger.domain.imagecollections.ImageCollection;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.comparator.ComparatorStringAscending;
import org.jphototagger.program.database.DatabaseImageCollections;
import org.jphototagger.program.exporter.ImageCollectionsExporter;
import org.jphototagger.program.exporter.ImageCollectionsExporter.CollectionWrapper;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase;
import org.jphototagger.program.model.ListModelImageCollections;
import org.jphototagger.program.view.panels.ProgressBarUpdater;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ImageCollectionsImporter implements Importer {
    public static final ImageCollectionsImporter INSTANCE = new ImageCollectionsImporter();

    private ImageCollectionsImporter() {}

    @Override
    public void importFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            ImageCollectionsExporter.CollectionWrapper wrapper =
                (CollectionWrapper) XmlObjectImporter.importObject(file,
                    ImageCollectionsExporter.CollectionWrapper.class);

            new ImportThread(wrapper.getCollection()).start();
        } catch (Exception ex) {
            Logger.getLogger(ImageCollectionsImporter.class.getName()).log(Level.SEVERE, null, ex);
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

        ImportThread(List<ImageCollection> imageCollections) {
            super("JPhotoTagger: Importing image collections");

            if (imageCollections == null) {
                throw new NullPointerException("imageCollections == null");
            }

            this.imageCollections = imageCollections;
        }

        @Override
        public void run() {
            for (ImageCollection imageCollection : imageCollections) {
                if (!DatabaseImageCollections.INSTANCE.exists(imageCollection.getName())) {
                    insertIntoDbMissingFiles(imageCollection);

                    if (DatabaseImageCollections.INSTANCE.insert(imageCollection)) {
                        updateImageCollectionList(imageCollection);
                    }
                }
            }
        }

        private void updateImageCollectionList(final ImageCollection imageCollection) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {
                @Override
                public void run() {
                    ListModelImageCollections model = ModelFactory.INSTANCE.getModel(ListModelImageCollections.class);

                    ListUtil.insertSorted(model, imageCollection.getName(), ComparatorStringAscending.INSTANCE,
                                          ListModelImageCollections.getSpecialCollectionCount(), model.getSize() - 1);
                }
            });
        }

        private void insertIntoDbMissingFiles(ImageCollection imageCollection) {
            InsertImageFilesIntoDatabase inserter = new InsertImageFilesIntoDatabase(imageCollection.getFiles(),
                                                        InsertIntoDatabase.OUT_OF_DATE);

            inserter.addProgressListener(new ProgressBarUpdater(inserter,
                    Bundle.getString(ImportThread.class, "ImageCollectionsImporter.ProgressBar.String")));
            inserter.run();    // run in this thread!
        }
    }
}
