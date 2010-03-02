package de.elmar_baumann.jpt.exporter;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.data.SavedSearch;
import de.elmar_baumann.jpt.database.DatabaseSavedSearches;
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
public final class SavedSearchesExporter implements Exporter {

    public static final FileFilter            FILE_FILTER = new FileNameExtensionFilter(JptBundle.INSTANCE.getString("SavedSearchesExporter.DisplayName.FileFilter"), "xml");
    public static final SavedSearchesExporter INSTANCE    = new SavedSearchesExporter();

    @Override
    public void exportFile(File file) {
        if (file == null) throw new NullPointerException("file == null");

        file = FileUtil.getWithSuffixIgnoreCase(file, ".xml");
        try {
            List<SavedSearch> savedSearches = DatabaseSavedSearches.INSTANCE.getAll();
            XmlObjectExporter.export(new CollectionWrapper(savedSearches), file);
        } catch (Exception ex) {
            AppLogger.logSevere(SavedSearchesExporter.class, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return JptBundle.INSTANCE.getString("SavedSearchesExporter.DisplayName");
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_export.png");
    }

    @XmlRootElement
    public static class CollectionWrapper {

        @XmlElementWrapper(name = "SavedSearches")
        @XmlElement(type = SavedSearch.class)
        private final ArrayList<SavedSearch> collection = new ArrayList<SavedSearch>();

        public CollectionWrapper() {
        }

        public CollectionWrapper(Collection<SavedSearch> collection) {
            this.collection.addAll(collection);
        }

        public List<SavedSearch> getCollection() {
            return new ArrayList<SavedSearch>(collection);
        }
    }

    private SavedSearchesExporter() {
    }
}
