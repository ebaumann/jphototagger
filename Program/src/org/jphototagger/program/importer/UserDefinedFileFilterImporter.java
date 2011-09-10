package org.jphototagger.program.importer;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

import org.jphototagger.domain.filefilter.UserDefinedFileFilter;
import org.jphototagger.domain.repository.Importer;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.database.DatabaseUserDefinedFileFilters;
import org.jphototagger.program.exporter.UserDefinedFileFilterExporter;
import org.jphototagger.program.exporter.UserDefinedFileFilterExporter.CollectionWrapper;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = Importer.class)
public final class UserDefinedFileFilterImporter implements Importer {

    @Override
    public void importFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            CollectionWrapper wrapper = (CollectionWrapper) XmlObjectImporter.importObject(file,
                    UserDefinedFileFilterExporter.CollectionWrapper.class);

            for (UserDefinedFileFilter filter : wrapper.getCollection()) {
                if (!DatabaseUserDefinedFileFilters.INSTANCE.exists(filter.getName())) {
                    DatabaseUserDefinedFileFilters.INSTANCE.insert(filter);
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
        return AppLookAndFeel.getIcon("icon_import.png");
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
