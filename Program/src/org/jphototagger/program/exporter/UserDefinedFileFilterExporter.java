package org.jphototagger.program.exporter;

import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.data.UserDefinedFileFilter;
import org.jphototagger.program.database.DatabaseUserDefinedFileFilters;
import org.jphototagger.program.resource.JptBundle;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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
public final class UserDefinedFileFilterExporter implements Exporter {
    public static final FileFilter FILE_FILTER =
        new FileNameExtensionFilter(
            JptBundle.INSTANCE.getString("UserDefinedFileFilterExporter.DisplayName.FileFilter"), "xml");
    public static final UserDefinedFileFilterExporter INSTANCE = new UserDefinedFileFilterExporter();

    @Override
    public void exportFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        File xmpFile = FileUtil.ensureSuffix(file, ".xml");

        try {
            Set<UserDefinedFileFilter> filter = DatabaseUserDefinedFileFilters.INSTANCE.getAll();

            XmlObjectExporter.export(new CollectionWrapper(filter), xmpFile);
        } catch (Exception ex) {
            AppLogger.logSevere(UserDefinedFileFilterExporter.class, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return JptBundle.INSTANCE.getString("UserDefinedFileFilter.DisplayName");
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_export.png");
    }

    @Override
    public String getDefaultFilename() {
        return "JptFileFilters.xml";
    }

    @XmlRootElement
    public static class CollectionWrapper {
        @XmlElementWrapper(name = "FileFilter")
        @XmlElement(type = UserDefinedFileFilter.class)
        private final ArrayList<UserDefinedFileFilter> collection = new ArrayList<UserDefinedFileFilter>();

        public CollectionWrapper() {}

        public CollectionWrapper(Collection<UserDefinedFileFilter> collection) {
            this.collection.addAll(collection);
        }

        public List<UserDefinedFileFilter> getCollection() {
            return new ArrayList<UserDefinedFileFilter>(collection);
        }
    }


    private UserDefinedFileFilterExporter() {}
}
