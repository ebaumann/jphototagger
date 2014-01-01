package org.jphototagger.eximport.jpt.importer;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import org.jphototagger.domain.repository.RenameTemplatesRepository;
import org.jphototagger.domain.repository.RepositoryDataImporter;
import org.jphototagger.domain.templates.RenameTemplate;
import org.jphototagger.eximport.jpt.exporter.RenameTemplatesExporter;
import org.jphototagger.eximport.jpt.exporter.RenameTemplatesExporter.CollectionWrapper;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataImporter.class)
public final class RenameTemplatesImporter implements RepositoryDataImporter {

    private final RenameTemplatesRepository repo = Lookup.getDefault().lookup(RenameTemplatesRepository.class);

    @Override
    public void importFromFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            RenameTemplatesExporter.CollectionWrapper wrapper =
                    (CollectionWrapper) XmlObjectImporter.importObject(file,
                    RenameTemplatesExporter.CollectionWrapper.class);

            for (RenameTemplate template : wrapper.getCollection()) {
                if (!repo.existsRenameTemplate(template.getName())) {
                    repo.saveRenameTemplate(template);
                }
            }
        } catch (Throwable t) {
            Logger.getLogger(RenameTemplatesImporter.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return RenameTemplatesExporter.FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return RenameTemplatesExporter.DISPLAY_NAME;
    }

    @Override
    public Icon getIcon() {
        return ImportPreferences.ICON;
    }

    @Override
    public String getDefaultFilename() {
        return RenameTemplatesExporter.DEFAULT_FILENAME;
    }

    @Override
    public int getPosition() {
        return RenameTemplatesExporter.POSITION;
    }

    @Override
    public boolean isJPhotoTaggerData() {
        return true;
    }
}
