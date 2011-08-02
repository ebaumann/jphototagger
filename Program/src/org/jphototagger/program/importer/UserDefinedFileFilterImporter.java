package org.jphototagger.program.importer;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

import org.jphototagger.domain.filefilter.UserDefinedFileFilter;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.database.DatabaseUserDefinedFileFilters;
import org.jphototagger.program.exporter.UserDefinedFileFilterExporter;
import org.jphototagger.program.exporter.UserDefinedFileFilterExporter.CollectionWrapper;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class UserDefinedFileFilterImporter implements Importer {
    public static final UserDefinedFileFilterImporter INSTANCE = new UserDefinedFileFilterImporter();

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
        return UserDefinedFileFilterExporter.INSTANCE.getFileFilter();
    }

    @Override
    public String getDisplayName() {
        return UserDefinedFileFilterExporter.INSTANCE.getDisplayName();
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_import.png");
    }

    @Override
    public String getDefaultFilename() {
        return UserDefinedFileFilterExporter.INSTANCE.getDefaultFilename();
    }

    private UserDefinedFileFilterImporter() {}
}
