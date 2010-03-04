package de.elmar_baumann.jpt.exporter;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.data.Favorite;
import de.elmar_baumann.jpt.database.DatabaseFavorites;
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
public final class FavoritesExporter implements Exporter {

    public static final FileFilter        FILE_FILTER = new FileNameExtensionFilter(JptBundle.INSTANCE.getString("FavoritesExporter.DisplayName.FileFilter"), "xml");
    public static final FavoritesExporter INSTANCE    = new FavoritesExporter();

    @Override
    public void exportFile(File file) {
        if (file == null) throw new NullPointerException("file == null");

        file = FileUtil.getWithSuffixIgnoreCase(file, ".xml");
        try {
            List<Favorite> templates = DatabaseFavorites.INSTANCE.getAll();
            XmlObjectExporter.export(new CollectionWrapper(templates), file);
        } catch (Exception ex) {
            AppLogger.logSevere(FavoritesExporter.class, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return JptBundle.INSTANCE.getString("FavoritesExporter.DisplayName");
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_export.png");
    }

    @Override
    public String getDefaultFilename() {
        return "JptFavorites.xml";
    }

    @XmlRootElement
    public static class CollectionWrapper {

        @XmlElementWrapper(name = "Favorites")
        @XmlElement(type = Favorite.class)
        private final ArrayList<Favorite> collection = new ArrayList<Favorite>();

        public CollectionWrapper() {
        }

        public CollectionWrapper(Collection<Favorite> collection) {
            this.collection.addAll(collection);
        }

        public List<Favorite> getCollection() {
            return new ArrayList<Favorite>(collection);
        }
    }

    private FavoritesExporter() {
    }
}
