package org.jphototagger.eximport.jpt.importer;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import org.jphototagger.domain.filefilter.UserDefinedFileFilter;
import org.jphototagger.domain.repository.RepositoryDataImporter;
import org.jphototagger.domain.repository.UserDefinedFileFiltersRepository;
import org.jphototagger.eximport.jpt.exporter.UserDefinedFileFilterExporter;
import org.jphototagger.eximport.jpt.exporter.UserDefinedFileFilterExporter.CollectionWrapper;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataImporter.class)
public final class UserDefinedFileFilterImporter implements RepositoryDataImporter {

    private final UserDefinedFileFiltersRepository repo = Lookup.getDefault().lookup(UserDefinedFileFiltersRepository.class);

    @Override
    public void importFromFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        try {
            CollectionWrapper wrapper = (CollectionWrapper) XmlObjectImporter.importObject(file,
                    UserDefinedFileFilterExporter.CollectionWrapper.class);

            for (UserDefinedFileFilter filter : wrapper.getCollection()) {
                if (!repo.existsUserDefinedFileFilter(filter.getName())) {
                    repo.saveUserDefinedFileFilter(filter);
                }
            }
        } catch (Throwable t) {
            Logger.getLogger(UserDefinedFileFilterImporter.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return UserDefinedFileFilterExporter.FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return UserDefinedFileFilterExporter.DISPLAY_NAME;
    }

    @Override
    public Icon getIcon() {
        return ImportPreferences.ICON;
    }

    @Override
    public String getDefaultFilename() {
        return UserDefinedFileFilterExporter.DEFAULT_FILENAME;
    }

    @Override
    public int getPosition() {
        return UserDefinedFileFilterExporter.POSITION;
    }

    @Override
    public boolean isJPhotoTaggerData() {
        return true;
    }
}
