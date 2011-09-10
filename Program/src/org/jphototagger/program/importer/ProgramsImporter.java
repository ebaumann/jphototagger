package org.jphototagger.program.importer;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.repository.Importer;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.exporter.ProgramsExporter;
import org.jphototagger.program.exporter.ProgramsExporter.CollectionWrapper;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = Importer.class)
public final class ProgramsImporter implements Importer {

    @Override
    public void importFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            ProgramsExporter.CollectionWrapper wrapper = (CollectionWrapper) XmlObjectImporter.importObject(file,
                    ProgramsExporter.CollectionWrapper.class);

            for (Program program : wrapper.getCollection()) {
                if (!DatabasePrograms.INSTANCE.exists(program)) {
                    DatabasePrograms.INSTANCE.insert(program);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ProgramsImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return ProgramsExporter.FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return ProgramsExporter.DISPLAY_NAME;
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_import.png");
    }

    @Override
    public String getDefaultFilename() {
        return ProgramsExporter.DEFAULT_FILENAME;
    }

    @Override
    public int getPosition() {
        return ProgramsExporter.POSITION;
    }

    @Override
    public boolean isJPhotoTaggerData() {
        return true;
    }
}
