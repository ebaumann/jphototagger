package de.elmar_baumann.jpt.exporter;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.data.RenameTemplate;
import de.elmar_baumann.jpt.database.DatabaseRenameTemplates;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.swing.Icon;
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
public final class RenameTemplatesExporter implements Exporter {

    public static final FileFilter              FILE_FILTER   = new FileNameExtensionFilter(JptBundle.INSTANCE.getString("RenameTemplatesExporter.DisplayName.FileFilter"), "xml");
    public static final RenameTemplatesExporter INSTANCE      = new RenameTemplatesExporter();

    @Override
    public void exportFile(File file) {
        if (file == null) throw new NullPointerException("file == null");

        file = FileUtil.getWithSuffixIgnoreCase(file, ".xml");
        try {
            Set<RenameTemplate> templates = DatabaseRenameTemplates.INSTANCE.getAll();
            XmlObjectExporter.export(new CollectionWrapper(templates), file);
        } catch (Exception ex) {
            AppLogger.logSevere(RenameTemplatesExporter.class, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return JptBundle.INSTANCE.getString("RenameTemplatesExporter.DisplayName");
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_export.png");
    }

    @Override
    public String getDefaultFilename() {
        return "JptRenameTemplates.xml";
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
