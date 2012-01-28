package org.jphototagger.eximport.jpt.importer;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.filefilter.UserDefinedFileFilter;
import org.jphototagger.domain.repository.RepositoryDataImporter;
import org.jphototagger.domain.repository.UserDefinedFileFiltersRepository;
import org.jphototagger.eximport.jpt.exporter.UserDefinedFileFilterExporter;
import org.jphototagger.eximport.jpt.exporter.UserDefinedFileFilterExporter.CollectionWrapper;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataImporter.class)
public final class UserDefinedFileFilterImporter implements RepositoryDataImporter {

    private final UserDefinedFileFiltersRepository repo = Lookup.getDefault().lookup(UserDefinedFileFiltersRepository.class);
    private static final ImageIcon ICON = IconUtil.getImageIcon("/org/jphototagger/eximport/jpt/icons/icon_import.png");

    @Override
    public void importFile(File file) {
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
        } catch (Exception ex) {
            Logger.getLogger(UserDefinedFileFilterImporter.class.getName()).log(Level.SEVERE, null, ex);
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
        return ICON;
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
