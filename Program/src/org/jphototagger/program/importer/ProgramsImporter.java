package org.jphototagger.program.importer;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.exporter.ProgramsExporter;
import org.jphototagger.program.exporter.ProgramsExporter.CollectionWrapper;

import java.io.File;

import javax.swing.filechooser.FileFilter;
import javax.swing.Icon;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ProgramsImporter implements Importer {
    public static final ProgramsImporter INSTANCE = new ProgramsImporter();

    @Override
    public void importFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            ProgramsExporter.CollectionWrapper wrapper =
                (CollectionWrapper) XmlObjectImporter.importObject(file,
                    ProgramsExporter.CollectionWrapper.class);

            for (Program program : wrapper.getCollection()) {
                if (!DatabasePrograms.INSTANCE.exists(program)) {
                    DatabasePrograms.INSTANCE.insert(program);
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(ProgramsImporter.class, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return ProgramsExporter.INSTANCE.getFileFilter();
    }

    @Override
    public String getDisplayName() {
        return ProgramsExporter.INSTANCE.getDisplayName();
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_import.png");
    }

    @Override
    public String getDefaultFilename() {
        return ProgramsExporter.INSTANCE.getDefaultFilename();
    }

    private ProgramsImporter() {}
}
