package org.jphototagger.program.importer;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.data.UserDefinedFileFilter;
import org.jphototagger.program.database.DatabaseUserDefinedFileFilters;
import org.jphototagger.program.exporter.UserDefinedFileFilterExporter;
import org.jphototagger.program.exporter.UserDefinedFileFilterExporter
    .CollectionWrapper;

import java.io.File;

import javax.swing.filechooser.FileFilter;
import javax.swing.Icon;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class UserDefinedFileFilterImporter implements Importer {
    public static final UserDefinedFileFilterImporter INSTANCE =
        new UserDefinedFileFilterImporter();

    @Override
    public void importFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            CollectionWrapper wrapper =
                (CollectionWrapper) XmlObjectImporter.importObject(file,
                    UserDefinedFileFilterExporter.CollectionWrapper.class);

            for (UserDefinedFileFilter filter : wrapper.getCollection()) {
                if (!DatabaseUserDefinedFileFilters.INSTANCE.exists(
                        filter.getName())) {
                    DatabaseUserDefinedFileFilters.INSTANCE.insert(filter);
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(UserDefinedFileFilterImporter.class, ex);
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
