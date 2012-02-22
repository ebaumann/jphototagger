package org.jphototagger.eximport.jpt.importer;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.repository.AutoscanDirectoriesRepository;
import org.jphototagger.domain.repository.RepositoryDataImporter;
import org.jphototagger.eximport.jpt.exporter.AutoscanDirectoriesExporter;
import org.jphototagger.eximport.jpt.exporter.AutoscanDirectoriesExporter.CollectionWrapper;
import org.jphototagger.lib.xml.bind.StringWrapper;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataImporter.class)
public final class AutoscanDirectoriesImporter implements RepositoryDataImporter {

    private final AutoscanDirectoriesRepository repo = Lookup.getDefault().lookup(AutoscanDirectoriesRepository.class);

    @Override
    public void importFromFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            AutoscanDirectoriesExporter.CollectionWrapper wrapper =
                    (CollectionWrapper) XmlObjectImporter.importObject(file,
                    AutoscanDirectoriesExporter.CollectionWrapper.class);

            for (StringWrapper stringWrapper : wrapper.getCollection()) {
                if (!repo.existsAutoscanDirectory(new File(stringWrapper.getString()))) {
                    repo.saveAutoscanDirectory(new File(stringWrapper.getString()));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(AutoscanDirectoriesImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return AutoscanDirectoriesExporter.FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return AutoscanDirectoriesExporter.DISPLAY_NAME;
    }

    @Override
    public Icon getIcon() {
        return ImportPreferences.ICON;
    }

    @Override
    public String getDefaultFilename() {
        return AutoscanDirectoriesExporter.DEFAULT_FILENAME;
    }

    @Override
    public int getPosition() {
        return AutoscanDirectoriesExporter.POSITION;
    }

    @Override
    public boolean isJPhotoTaggerData() {
        return true;
    }
}
