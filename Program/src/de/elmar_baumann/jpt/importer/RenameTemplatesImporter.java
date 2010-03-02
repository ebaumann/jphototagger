package de.elmar_baumann.jpt.importer;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.data.RenameTemplate;
import de.elmar_baumann.jpt.database.DatabaseRenameTemplates;
import de.elmar_baumann.jpt.exporter.RenameTemplatesExporter;
import de.elmar_baumann.jpt.exporter.RenameTemplatesExporter.CollectionWrapper;
import de.elmar_baumann.jpt.resource.JptBundle;
import java.io.File;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-03-02
 */
public final class RenameTemplatesImporter implements Importer {

    public static final RenameTemplatesImporter INSTANCE = new RenameTemplatesImporter();

    @Override
    public void importFile(File file) {
        try {
            RenameTemplatesExporter.CollectionWrapper wrapper = (CollectionWrapper)
                    XmlObjectImporter.importObject(
                          file, RenameTemplatesExporter.CollectionWrapper.class);

            for (RenameTemplate template : wrapper.getCollection()) {
                if (!DatabaseRenameTemplates.INSTANCE.exists(template.getName())) {
                    DatabaseRenameTemplates.INSTANCE.insert(template);
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(RenameTemplatesImporter.class, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return RenameTemplatesExporter.FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return JptBundle.INSTANCE.getString("RenameTemplatesImporter.DisplayName");
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_import.png");
    }

    private RenameTemplatesImporter() {
    }
}
