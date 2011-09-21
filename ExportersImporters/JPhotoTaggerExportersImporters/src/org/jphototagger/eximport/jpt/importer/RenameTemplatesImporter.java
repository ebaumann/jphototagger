package org.jphototagger.eximport.jpt.importer;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.repository.RenameTemplatesRepository;
import org.jphototagger.domain.repository.RepositoryDataImporter;
import org.jphototagger.domain.templates.RenameTemplate;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;
import org.jphototagger.eximport.jpt.exporter.RenameTemplatesExporter;
import org.jphototagger.eximport.jpt.exporter.RenameTemplatesExporter.CollectionWrapper;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataImporter.class)
public final class RenameTemplatesImporter implements RepositoryDataImporter {

    private final RenameTemplatesRepository repo = Lookup.getDefault().lookup(RenameTemplatesRepository.class);
    private static final ImageIcon ICON = IconUtil.getImageIcon("/org/jphototagger/eximport/jpt/icons/icon_import.png");

    @Override
    public void importFile(File file) {
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
        } catch (Exception ex) {
            Logger.getLogger(RenameTemplatesImporter.class.getName()).log(Level.SEVERE, null, ex);
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
        return ICON;
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
