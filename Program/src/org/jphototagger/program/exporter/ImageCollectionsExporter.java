package org.jphototagger.program.exporter;

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
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.xml.bind.XmlObjectExporter;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.database.DatabaseImageCollections;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ImageCollectionsExporter implements Exporter {
    public static final FileFilter FILE_FILTER = new FileNameExtensionFilter(Bundle.getString(ImageCollectionsExporter.class, "ImageCollectionsExporter.DisplayName.FileFilter"), "xml");
    public static final ImageCollectionsExporter INSTANCE = new ImageCollectionsExporter();

    @Override
    public void exportFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        File xmlFile = FileUtil.ensureSuffix(file, ".xml");

        try {
            List<ImageCollection> templates = DatabaseImageCollections.INSTANCE.getAll2();

            XmlObjectExporter.export(new CollectionWrapper(templates), xmlFile);
        } catch (Exception ex) {
            Logger.getLogger(ImageCollectionsExporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return Bundle.getString(ImageCollectionsExporter.class, "ExportImageCollections.DisplayName");
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_export.png");
    }

    @Override
    public String getDefaultFilename() {
        return "JptImageCollections.xml";
    }

    @XmlRootElement
    public static class CollectionWrapper {
        @XmlElementWrapper(name = "ImageCollections")
        @XmlElement(type = ImageCollection.class)
        private final ArrayList<ImageCollection> collection = new ArrayList<ImageCollection>();

        public CollectionWrapper() {}

        public CollectionWrapper(Collection<ImageCollection> collection) {
            this.collection.addAll(collection);
        }

        public List<ImageCollection> getCollection() {
            return new ArrayList<ImageCollection>(collection);
        }
    }


    private ImageCollectionsExporter() {}
}
