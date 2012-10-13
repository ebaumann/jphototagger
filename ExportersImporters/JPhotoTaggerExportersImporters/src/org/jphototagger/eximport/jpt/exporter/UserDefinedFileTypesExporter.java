package org.jphototagger.eximport.jpt.exporter;

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
import org.jphototagger.domain.filetypes.UserDefinedFileType;
import org.jphototagger.domain.repository.RepositoryDataExporter;
import org.jphototagger.domain.repository.UserDefinedFileTypesRepository;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.xml.bind.XmlObjectExporter;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataExporter.class)
public final class UserDefinedFileTypesExporter implements RepositoryDataExporter {

    public static final String DEFAULT_FILENAME = "JptUserDefinedFileTypes.xml";
    public static final String DISPLAY_NAME = Bundle.getString(UserDefinedFileTypesExporter.class, "UserDefinedFileTypesExporter.DisplayName");
    private static final String FILE_FILTER_DESCRIPTION = Bundle.getString(UserDefinedFileTypesExporter.class, "UserDefinedFileTypesExporter.FileFilterDescription");
    private static final String FILE_FILTER_SUFFIX = "xml";
    public static final FileFilter FILE_FILTER = new FileNameExtensionFilter(FILE_FILTER_DESCRIPTION, FILE_FILTER_SUFFIX);
    public static final int POSITION = 120;
    private final UserDefinedFileTypesRepository repo = Lookup.getDefault().lookup(UserDefinedFileTypesRepository.class);

    @Override
    public void exportToFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        File xmpFile = FileUtil.ensureSuffix(file, ".xml");
        try {
            List<UserDefinedFileType> filter = repo.findAllUserDefinedFileTypes();
            XmlObjectExporter.export(new CollectionWrapper(filter), xmpFile);
        } catch (Exception ex) {
            Logger.getLogger(UserDefinedFileTypesExporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public Icon getIcon() {
        return ExportPreferences.ICON;
    }

    @Override
    public String getDefaultFilename() {
        return DEFAULT_FILENAME;
    }

    @XmlRootElement
    public static class CollectionWrapper {

        @XmlElementWrapper(name = "FileType")
        @XmlElement(type = UserDefinedFileType.class)
        private final ArrayList<UserDefinedFileType> collection = new ArrayList<>();

        public CollectionWrapper() {
        }

        public CollectionWrapper(Collection<UserDefinedFileType> collection) {
            this.collection.addAll(collection);
        }

        public List<UserDefinedFileType> getCollection() {
            return new ArrayList<>(collection);
        }
    }

    @Override
    public boolean isJPhotoTaggerData() {
        return true;
    }

    @Override
    public int getPosition() {
        return POSITION;
    }
}
