package org.jphototagger.program.module.exportimport.importer;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import org.jphototagger.domain.imagecollections.ImageCollection;
import org.jphototagger.domain.repository.ImageCollectionsRepository;
import org.jphototagger.domain.repository.RepositoryDataImporter;
import org.jphototagger.domain.repository.SaveOrUpdate;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.ProgressBarUpdater;
import org.jphototagger.lib.util.ThreadUtil;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.program.misc.SaveToOrUpdateFilesInRepositoryImpl;
import org.jphototagger.program.module.exportimport.exporter.ImageCollectionsExporter;
import org.jphototagger.program.module.exportimport.exporter.ImageCollectionsExporter.CollectionWrapper;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataImporter.class)
public final class ImageCollectionsImporter implements RepositoryDataImporter {

    @Override
    public void importFromFile(File file) {
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
        return ImageCollectionsExporter.FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return ImageCollectionsExporter.DISPLAY_NAME;
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_import.png");
    }

    @Override
    public String getDefaultFilename() {
        return ImageCollectionsExporter.DEFAULT_FILENAME;
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
            ImageCollectionsRepository repo = Lookup.getDefault().lookup(ImageCollectionsRepository.class);

            for (ImageCollection imageCollection : imageCollections) {
                if (!repo.existsImageCollection(imageCollection.getName())) {
                    insertIntoDbMissingFiles(imageCollection);
                    repo.saveImageCollection(imageCollection);
                }
            }
        }

        private void insertIntoDbMissingFiles(ImageCollection imageCollection) {
            SaveToOrUpdateFilesInRepositoryImpl inserter = new SaveToOrUpdateFilesInRepositoryImpl(imageCollection.getFiles(),
                    SaveOrUpdate.OUT_OF_DATE);
            inserter.addProgressListener(new ProgressBarUpdater(inserter,
                    Bundle.getString(ImportThread.class, "ImageCollectionsImporter.ProgressBar.String")));
            ThreadUtil.runInThisThread(inserter);
        }
    }

    @Override
    public int getPosition() {
        return ImageCollectionsExporter.POSITION;
    }

    @Override
    public boolean isJPhotoTaggerData() {
        return true;
    }
}
