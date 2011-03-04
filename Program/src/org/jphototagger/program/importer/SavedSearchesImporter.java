package org.jphototagger.program.importer;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.database.DatabaseSavedSearches;
import org.jphototagger.program.exporter.SavedSearchesExporter;
import org.jphototagger.program.exporter.SavedSearchesExporter.CollectionWrapper;

import java.io.File;

import javax.swing.filechooser.FileFilter;
import javax.swing.Icon;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class SavedSearchesImporter implements Importer {
    public static final SavedSearchesImporter INSTANCE = new SavedSearchesImporter();

    @Override
    public void importFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            SavedSearchesExporter.CollectionWrapper wrapper = (CollectionWrapper) XmlObjectImporter.importObject(file,
                                                                  SavedSearchesExporter.CollectionWrapper.class);

            for (SavedSearch savedSearch : wrapper.getCollection()) {
                if (savedSearch.isValid() &&!DatabaseSavedSearches.INSTANCE.exists(savedSearch.getName())) {
                    DatabaseSavedSearches.INSTANCE.insert(savedSearch);
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(SavedSearchesImporter.class, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return SavedSearchesExporter.INSTANCE.getFileFilter();
    }

    @Override
    public String getDisplayName() {
        return SavedSearchesExporter.INSTANCE.getDisplayName();
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_import.png");
    }

    @Override
    public String getDefaultFilename() {
        return SavedSearchesExporter.INSTANCE.getDefaultFilename();
    }

    private SavedSearchesImporter() {}
}
