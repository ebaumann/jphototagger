package de.elmar_baumann.jpt.importer;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.RenameTemplate;
import de.elmar_baumann.jpt.database.DatabaseRenameTemplates;
import de.elmar_baumann.jpt.exporter.RenameTemplatesExporter;
import de.elmar_baumann.jpt.exporter.RenameTemplatesExporter.CollectionWrapper;
import de.elmar_baumann.jpt.view.ViewUtil;
import java.awt.Component;
import java.io.File;
import java.util.List;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-03-02
 */
public final class RenameTemplatesImporter {

    public static void importFile(Component parent, boolean dlgMessages) {
        File file = ViewUtil.chooseFile(RenameTemplatesExporter.KEY_START_DIR, RenameTemplatesExporter.FILE_FILTER, null);

        if (file != null) {
            importFile(file, dlgMessages);
        }
    }

    public static void importFile(File file, boolean dlgMessages) {
        try {
            RenameTemplatesExporter.CollectionWrapper wrapper = (CollectionWrapper)
                    XmlObjectImporter.importObject(
                          file, RenameTemplatesExporter.CollectionWrapper.class);
            int                  insertCount = 0;
            List<RenameTemplate> templates   = wrapper.getCollection();

            for (RenameTemplate template : templates) {
                if (!DatabaseRenameTemplates.INSTANCE.exists(template.getName())) {
                    if (DatabaseRenameTemplates.INSTANCE.insert(template)) {
                        insertCount++;
                    }
                }
            }
            if (dlgMessages) MessageDisplayer.information(null, "RenameTemplatesImporter.Info.ImportSuccess", insertCount, templates.size(), file);
        } catch (Exception ex) {
            AppLogger.logSevere(RenameTemplatesImporter.class, ex);
            if (dlgMessages) MessageDisplayer.error(null, "RenameTemplatesImporter.Error.Import", file);
        }
    }

    private RenameTemplatesImporter() {
    }
}
