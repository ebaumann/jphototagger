package org.jphototagger.program.exporter;

import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.resource.JptBundle;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.Icon;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ProgramsExporter implements Exporter {
    public static final FileFilter FILE_FILTER =
        new FileNameExtensionFilter(JptBundle.INSTANCE.getString("ProgramsExporter.DisplayName.FileFilter"), "xml");
    public static final ProgramsExporter INSTANCE = new ProgramsExporter();

    @Override
    public void exportFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        File xmlFile = FileUtil.ensureSuffix(file, ".xml");

        try {
            List<Program> programs = DatabasePrograms.INSTANCE.getAll(DatabasePrograms.Type.ACTION);

            programs.addAll(DatabasePrograms.INSTANCE.getAll(DatabasePrograms.Type.PROGRAM));
            XmlObjectExporter.export(new CollectionWrapper(programs), xmlFile);
        } catch (Exception ex) {
            AppLogger.logSevere(ProgramsExporter.class, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return JptBundle.INSTANCE.getString("ProgramsExporter.DisplayName");
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_export.png");
    }

    @Override
    public String getDefaultFilename() {
        return "JptPrograms.xml";
    }

    @XmlRootElement
    public static class CollectionWrapper {
        @XmlElementWrapper(name = "Programs")
        @XmlElement(type = Program.class)
        private final ArrayList<Program> collection = new ArrayList<Program>();

        public CollectionWrapper() {}

        public CollectionWrapper(Collection<Program> collection) {
            this.collection.addAll(collection);
        }

        public List<Program> getCollection() {
            return new ArrayList<Program>(collection);
        }
    }


    private ProgramsExporter() {}
}
