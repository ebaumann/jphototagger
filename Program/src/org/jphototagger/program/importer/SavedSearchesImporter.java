package org.jphototagger.program.importer;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

import org.jphototagger.domain.repository.Importer;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.domain.metadata.search.SavedSearch;
import org.jphototagger.repository.hsqldb.DatabaseSavedSearches;
import org.jphototagger.program.exporter.SavedSearchesExporter;
import org.jphototagger.program.exporter.SavedSearchesExporter.CollectionWrapper;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = Importer.class)
public final class SavedSearchesImporter implements Importer {

    @Override
    public void importFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            SavedSearchesExporter.CollectionWrapper wrapper = (CollectionWrapper) XmlObjectImporter.importObject(file,
                    SavedSearchesExporter.CollectionWrapper.class);

            for (SavedSearch savedSearch : wrapper.getCollection()) {
                if (savedSearch.isValid() && !DatabaseSavedSearches.INSTANCE.exists(savedSearch.getName())) {
                    DatabaseSavedSearches.INSTANCE.insert(savedSearch);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(SavedSearchesImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return SavedSearchesExporter.FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return SavedSearchesExporter.DISPLAY_NAME;
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_import.png");
    }

    @Override
    public String getDefaultFilename() {
        return SavedSearchesExporter.DEFAULT_FILENAME;
    }

    @Override
    public int getPosition() {
        return SavedSearchesExporter.POSITION;
    }

    @Override
    public boolean isJPhotoTaggerData() {
        return true;
    }
}
