package org.jphototagger.eximport.jpt.importer;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.jphototagger.domain.repository.RepositoryDataImporter;
import org.jphototagger.eximport.jpt.exporter.ProgramsExporter;
import org.jphototagger.eximport.jpt.exporter.ProgramsExporter.CollectionWrapper;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataImporter.class)
public final class ProgramsImporter implements RepositoryDataImporter {

    private final ProgramsRepository repo = Lookup.getDefault().lookup(ProgramsRepository.class);

    @Override
    public void importFromFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            ProgramsExporter.CollectionWrapper wrapper = (CollectionWrapper) XmlObjectImporter.importObject(file,
                    ProgramsExporter.CollectionWrapper.class);

            for (Program program : wrapper.getCollection()) {
                if (!repo.existsProgram(program)) {
                    repo.saveProgram(program);
                }
            }
        } catch (Throwable t) {
            Logger.getLogger(ProgramsImporter.class.getName()).log(Level.SEVERE, null, t);
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
        return ImportPreferences.ICON;
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
