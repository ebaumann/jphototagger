package org.jphototagger.eximport.jpt.importer;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.jphototagger.domain.repository.RepositoryDataImporter;
import org.jphototagger.eximport.jpt.exporter.ProgramsExporter;
import org.jphototagger.eximport.jpt.exporter.ProgramsExporter.CollectionWrapper;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataImporter.class)
public final class ProgramsImporter implements RepositoryDataImporter {

    private final ProgramsRepository repo = Lookup.getDefault().lookup(ProgramsRepository.class);
    private static final ImageIcon ICON = IconUtil.getImageIcon("/org/jphototagger/eximport/jpt/icons/icon_import.png");

    @Override
    public void importFile(File file) {
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
        return ICON;
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
