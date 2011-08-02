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

import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.xml.bind.XmlObjectExporter;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.database.DatabaseSavedSearches;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class SavedSearchesExporter implements Exporter {
    public static final FileFilter FILE_FILTER = new FileNameExtensionFilter(Bundle.getString(SavedSearchesExporter.class, "SavedSearchesExporter.DisplayName.FileFilter"), "xml");
    public static final SavedSearchesExporter INSTANCE = new SavedSearchesExporter();

    @Override
    public void exportFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        File xmlFile = FileUtil.ensureSuffix(file, ".xml");

        try {
            List<SavedSearch> savedSearches = DatabaseSavedSearches.INSTANCE.getAll();

            XmlObjectExporter.export(new CollectionWrapper(savedSearches), xmlFile);
        } catch (Exception ex) {
            Logger.getLogger(SavedSearchesExporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return Bundle.getString(SavedSearchesExporter.class, "SavedSearchesExporter.DisplayName");
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_export.png");
    }

    @Override
    public String getDefaultFilename() {
        return "JptSavedSearches.xml";
    }

    @XmlRootElement
    public static class CollectionWrapper {
        @XmlElementWrapper(name = "SavedSearches")
        @XmlElement(type = SavedSearch.class)
        private final ArrayList<SavedSearch> collection = new ArrayList<SavedSearch>();

        public CollectionWrapper() {}

        public CollectionWrapper(Collection<SavedSearch> collection) {
            this.collection.addAll(collection);
        }

        public List<SavedSearch> getCollection() {
            return new ArrayList<SavedSearch>(collection);
        }
    }


    private SavedSearchesExporter() {}
}
