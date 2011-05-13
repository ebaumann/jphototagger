package org.jphototagger.program.importer;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.database.DatabaseFileExcludePatterns;
import org.jphototagger.program.exporter.FileExcludePatternsExporter;
import org.jphototagger.program.exporter.FileExcludePatternsExporter.CollectionWrapper;
import org.jphototagger.program.exporter.StringWrapper;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import javax.swing.Icon;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class FileExcludePatternsImporter implements Importer {
    public static final FileExcludePatternsImporter INSTANCE = new FileExcludePatternsImporter();

    @Override
    public void importFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            FileExcludePatternsExporter.CollectionWrapper wrapper =
                (CollectionWrapper) XmlObjectImporter.importObject(file,
                    FileExcludePatternsExporter.CollectionWrapper.class);

            for (StringWrapper stringWrapper : wrapper.getCollection()) {
                if (!DatabaseFileExcludePatterns.INSTANCE.exists(stringWrapper.getString())) {
                    DatabaseFileExcludePatterns.INSTANCE.insert(stringWrapper.getString());
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(FileExcludePatternsImporter.class, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return FileExcludePatternsExporter.INSTANCE.getFileFilter();
    }

    @Override
    public String getDisplayName() {
        return FileExcludePatternsExporter.INSTANCE.getDisplayName();
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_import.png");
    }

    @Override
    public String getDefaultFilename() {
        return FileExcludePatternsExporter.INSTANCE.getDefaultFilename();
    }

    private FileExcludePatternsImporter() {}
}
