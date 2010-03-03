package de.elmar_baumann.jpt.exporter;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.data.ImageCollection;
import de.elmar_baumann.jpt.database.DatabaseImageCollections;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-03-02
 */
public final class ImageCollectionsExporter implements Exporter {

    public static final FileFilter              FILE_FILTER   = new FileNameExtensionFilter(JptBundle.INSTANCE.getString("ImageCollectionsExporter.DisplayName.FileFilter"), "xml");
    public static final ImageCollectionsExporter INSTANCE      = new ImageCollectionsExporter();

    @Override
    public void exportFile(File file) {
        if (file == null) throw new NullPointerException("file == null");

        file = FileUtil.getWithSuffixIgnoreCase(file, ".xml");
        try {
            List<ImageCollection> templates = DatabaseImageCollections.INSTANCE.getAll2();
            XmlObjectExporter.export(new CollectionWrapper(templates), file);
        } catch (Exception ex) {
            AppLogger.logSevere(ImageCollectionsExporter.class, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return JptBundle.INSTANCE.getString("ExportImageCollections.DisplayName");
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

        public CollectionWrapper() {
        }

        public CollectionWrapper(Collection<ImageCollection> collection) {
            this.collection.addAll(collection);
        }

        public List<ImageCollection> getCollection() {
            return new ArrayList<ImageCollection>(collection);
        }
    }

    private ImageCollectionsExporter() {
    }
}
