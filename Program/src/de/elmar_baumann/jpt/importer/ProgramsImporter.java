package de.elmar_baumann.jpt.importer;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.data.Program;
import de.elmar_baumann.jpt.database.DatabasePrograms;
import de.elmar_baumann.jpt.exporter.ProgramsExporter;
import de.elmar_baumann.jpt.exporter.ProgramsExporter.CollectionWrapper;
import java.io.File;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-03-02
 */
public final class ProgramsImporter implements Importer {

    public static final ProgramsImporter INSTANCE = new ProgramsImporter();

    @Override
    public void importFile(File file) {
        try {
            ProgramsExporter.CollectionWrapper wrapper = (CollectionWrapper)
                    XmlObjectImporter.importObject(
                          file, ProgramsExporter.CollectionWrapper.class);

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

    private ProgramsImporter() {
    }
}
