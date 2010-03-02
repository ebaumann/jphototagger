package de.elmar_baumann.jpt.exporter;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.RenameTemplate;
import de.elmar_baumann.jpt.database.DatabaseRenameTemplates;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.view.ViewUtil;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-03-02
 */
public final class RenameTemplatesExporter {

    public static final String     KEY_START_DIR = "RenameTemplatesExporter.StartDir";
    public static final FileFilter FILE_FILTER   = new FileNameExtensionFilter(JptBundle.INSTANCE.getString("RenameTemplatesExporter.DisplayName.FileFilter"), "xml");

    public static void export(Component parent, boolean dlgMessages) {
        File file = ViewUtil.chooseFile(KEY_START_DIR, FILE_FILTER, parent);

        if (file != null) {
            export(file, dlgMessages);
        }
    }

    public static void export(File file, boolean dlgMessages) {
        if (file == null) throw new NullPointerException("file == null");

        file = FileUtil.getWithSuffixIgnoreCase(file, ".xml");
        try {
            AppLogger.logInfo(RenameTemplatesExporter.class, "RenameTemplatesExporter.Info.Export", file);
            Set<RenameTemplate> templates = DatabaseRenameTemplates.INSTANCE.getAll();
            XmlObjectExporter.export(new CollectionWrapper(templates), file);
            if (dlgMessages) MessageDisplayer.information(null, "RenameTemplatesExporter.Info.ExportSuccess", templates.size(), file);
        } catch (Exception ex) {
            AppLogger.logSevere(RenameTemplatesExporter.class, ex);
            if (dlgMessages) MessageDisplayer.error(null, "RenameTemplatesExporter.Error.Export", file);
        }
    }

    @XmlRootElement
    public static class CollectionWrapper {

        @XmlElementWrapper(name = "RenameTemplates")
        @XmlElement(type = RenameTemplate.class)
        private final ArrayList<RenameTemplate> collection = new ArrayList<RenameTemplate>();

        public CollectionWrapper() {
        }

        public CollectionWrapper(Collection<RenameTemplate> collection) {
            this.collection.addAll(collection);
        }

        public List<RenameTemplate> getCollection() {
            return new ArrayList<RenameTemplate>(collection);
        }
    }

    private RenameTemplatesExporter() {
    }
}
