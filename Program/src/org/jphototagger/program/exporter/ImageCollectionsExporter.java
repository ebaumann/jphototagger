package org.jphototagger.program.exporter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.jphototagger.domain.imagecollections.ImageCollection;
import org.jphototagger.domain.repository.Exporter;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.xml.bind.XmlObjectExporter;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.database.DatabaseImageCollections;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = Exporter.class)
public final class ImageCollectionsExporter implements Exporter {

    public static final String DEFAULT_FILENAME = "JptImageCollections.xml";
    public static final String DISPLAY_NAME = Bundle.getString(ImageCollectionsExporter.class, "ExportImageCollections.DisplayName");
    public static final FileFilter FILE_FILTER = new FileNameExtensionFilter(Bundle.getString(ImageCollectionsExporter.class, "ImageCollectionsExporter.DisplayName.FileFilter"), "xml");
    public static final ImageIcon ICON = AppLookAndFeel.getIcon("icon_export.png");
    public static final int POSITION = 50;

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
        private final ArrayList<ImageCollection> collection = new ArrayList<ImageCollection>();

        public CollectionWrapper() {
        }

        public CollectionWrapper(Collection<ImageCollection> collection) {
            this.collection.addAll(collection);
        }

        public List<ImageCollection> getCollection() {
            return new ArrayList<ImageCollection>(collection);
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
