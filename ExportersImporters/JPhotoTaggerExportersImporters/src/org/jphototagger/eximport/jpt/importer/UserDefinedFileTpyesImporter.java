package org.jphototagger.eximport.jpt.importer;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import org.jphototagger.domain.filetypes.UserDefinedFileType;
import org.jphototagger.domain.repository.RepositoryDataImporter;
import org.jphototagger.domain.repository.UserDefinedFileTypesRepository;
import org.jphototagger.eximport.jpt.exporter.UserDefinedFileTypesExporter;
import org.jphototagger.eximport.jpt.exporter.UserDefinedFileTypesExporter.CollectionWrapper;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataImporter.class)
public final class UserDefinedFileTpyesImporter implements RepositoryDataImporter {

    private final UserDefinedFileTypesRepository repo = Lookup.getDefault().lookup(UserDefinedFileTypesRepository.class);

    @Override
    public void importFromFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        try {
            CollectionWrapper wrapper = (CollectionWrapper) XmlObjectImporter.importObject(file,
                    UserDefinedFileTypesExporter.CollectionWrapper.class);

            for (UserDefinedFileType fileType : wrapper.getCollection()) {
                if (!repo.existsUserDefinedFileTypeWithSuffix(fileType.getSuffix())) {
                    repo.saveUserDefinedFileType(fileType);
                }
            }
        } catch (Throwable t) {
            Logger.getLogger(UserDefinedFileTpyesImporter.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return UserDefinedFileTypesExporter.FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return UserDefinedFileTypesExporter.DISPLAY_NAME;
    }

    @Override
    public Icon getIcon() {
        return ImportPreferences.ICON;
    }

    @Override
    public String getDefaultFilename() {
        return UserDefinedFileTypesExporter.DEFAULT_FILENAME;
    }

    @Override
    public int getPosition() {
        return UserDefinedFileTypesExporter.POSITION;
    }

    @Override
    public boolean isJPhotoTaggerData() {
        return true;
    }
}
