package org.jphototagger.program.module.exportimport.exporter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.jphototagger.domain.imagecollections.ImageCollection;
import org.jphototagger.domain.repository.ImageCollectionsRepository;
import org.jphototagger.domain.repository.RepositoryDataExporter;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.xml.bind.XmlObjectExporter;
import org.jphototagger.resources.Icons;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataExporter.class)
public final class ImageCollectionsExporter implements RepositoryDataExporter {

    public static final String DEFAULT_FILENAME = "JptImageCollections.xml";
    public static final String DISPLAY_NAME = Bundle.getString(ImageCollectionsExporter.class, "ExportImageCollections.DisplayName");
    public static final String SUFFIX_XML = "xml";
    private static final String FILE_FILTER_DESCRIPTION = Bundle.getString(ImageCollectionsExporter.class, "ImageCollectionsExporter.FileFilterDescription");
    public static final FileFilter FILE_FILTER = new FileNameExtensionFilter(FILE_FILTER_DESCRIPTION, SUFFIX_XML);
    private static final Icon ICON = Icons.getIcon("icon_app_small.png");
    public static final int POSITION = 50;
    private final ImageCollectionsRepository repo = Lookup.getDefault().lookup(ImageCollectionsRepository.class);

    @Override
    public void exportToFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        File xmlFile = FileUtil.ensureSuffix(file, ".xml");

        try {
            List<ImageCollection> templates = repo.findAllImageCollections();

            XmlObjectExporter.export(new CollectionWrapper(templates), xmlFile);
        } catch (Throwable t) {
            Logger.getLogger(ImageCollectionsExporter.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public Icon getIcon() {
        return ICON;
    }

    @Override
    public String getDefaultFilename() {
        return DEFAULT_FILENAME;
    }

    @XmlRootElement
    public static class CollectionWrapper {

        @XmlElementWrapper(name = "ImageCollections")
        @XmlElement(type = ImageCollection.class)
        private final ArrayList<ImageCollection> collection = new ArrayList<>();

        public CollectionWrapper() {
        }

        public CollectionWrapper(Collection<ImageCollection> collection) {
            this.collection.addAll(collection);
        }

        public List<ImageCollection> getCollection() {
            return new ArrayList<>(collection);
        }
    }

    @Override
    public boolean isJPhotoTaggerData() {
        return true;
    }

    @Override
    public int getPosition() {
        return POSITION;
    }
}
