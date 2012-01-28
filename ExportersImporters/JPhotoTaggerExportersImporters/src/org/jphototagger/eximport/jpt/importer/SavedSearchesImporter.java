package org.jphototagger.eximport.jpt.importer;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.metadata.search.SavedSearch;
import org.jphototagger.domain.repository.RepositoryDataImporter;
import org.jphototagger.domain.repository.SavedSearchesRepository;
import org.jphototagger.eximport.jpt.exporter.SavedSearchesExporter;
import org.jphototagger.eximport.jpt.exporter.SavedSearchesExporter.CollectionWrapper;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataImporter.class)
public final class SavedSearchesImporter implements RepositoryDataImporter {

    private final SavedSearchesRepository repo = Lookup.getDefault().lookup(SavedSearchesRepository.class);
    private static final ImageIcon ICON = IconUtil.getImageIcon("/org/jphototagger/eximport/jpt/icons/icon_import.png");

    @Override
    public void importFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            SavedSearchesExporter.CollectionWrapper wrapper = (CollectionWrapper) XmlObjectImporter.importObject(file,
                    SavedSearchesExporter.CollectionWrapper.class);

            for (SavedSearch savedSearch : wrapper.getCollection()) {
                if (savedSearch.isValid() && !repo.existsSavedSearch(savedSearch.getName())) {
                    repo.saveSavedSearch(savedSearch);
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
        return ICON;
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
