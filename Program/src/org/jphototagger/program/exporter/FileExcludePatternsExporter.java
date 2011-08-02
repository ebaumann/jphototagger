package org.jphototagger.program.exporter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.xml.bind.XmlObjectExporter;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.database.DatabaseFileExcludePatterns;
import org.jphototagger.program.resource.JptBundle;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class FileExcludePatternsExporter implements Exporter {
    public static final FileFilter FILE_FILTER =
        new FileNameExtensionFilter(JptBundle.INSTANCE.getString("FileExcludePatternsExporter.DisplayName.FileFilter"),
                                    "xml");
    public static final FileExcludePatternsExporter INSTANCE = new FileExcludePatternsExporter();

    @Override
    public void exportFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        File xmlFile = FileUtil.ensureSuffix(file, ".xml");

        try {
            List<String> patterns = DatabaseFileExcludePatterns.INSTANCE.getAll();

            XmlObjectExporter.export(new CollectionWrapper(StringWrapper.getWrappedStrings(patterns)), xmlFile);
        } catch (Exception ex) {
            Logger.getLogger(FileExcludePatternsExporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return JptBundle.INSTANCE.getString("FileExcludePatternsExporter.DisplayName");
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_export.png");
    }

    @Override
    public String getDefaultFilename() {
        return "JptFileExludePatterns.xml";
    }

    @XmlRootElement
    public static class CollectionWrapper {
        @XmlElementWrapper(name = "FileExludePatterns")
        @XmlElement(type = StringWrapper.class)
        private final ArrayList<StringWrapper> collection = new ArrayList<StringWrapper>();

        public CollectionWrapper() {}

        public CollectionWrapper(Collection<StringWrapper> collection) {
            this.collection.addAll(collection);
        }

        public List<StringWrapper> getCollection() {
            return new ArrayList<StringWrapper>(collection);
        }
    }


    private FileExcludePatternsExporter() {}
}
