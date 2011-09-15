package org.jphototagger.program.repository.importer;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

import org.jphototagger.domain.repository.FileExcludePatternsRepository;
import org.jphototagger.domain.repository.RepositoryDataImporter;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.repository.exporter.FileExcludePatternsExporter;
import org.jphototagger.program.repository.exporter.FileExcludePatternsExporter.CollectionWrapper;
import org.jphototagger.program.repository.exporter.StringWrapper;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataImporter.class)
public final class FileExcludePatternsImporter implements RepositoryDataImporter {

    private final FileExcludePatternsRepository repo = Lookup.getDefault().lookup(FileExcludePatternsRepository.class);

    @Override
    public void importFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            FileExcludePatternsExporter.CollectionWrapper wrapper =
                    (CollectionWrapper) XmlObjectImporter.importObject(file,
                    FileExcludePatternsExporter.CollectionWrapper.class);

            for (StringWrapper stringWrapper : wrapper.getCollection()) {
                if (!repo.existsFileExcludePattern(stringWrapper.getString())) {
                    repo.saveFileExcludePattern(stringWrapper.getString());
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(FileExcludePatternsImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return FileExcludePatternsExporter.FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return FileExcludePatternsExporter.DISPLAY_NAME;
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_import.png");
    }

    @Override
    public String getDefaultFilename() {
        return FileExcludePatternsExporter.DEFAULT_FILENAME;
    }

    @Override
    public int getPosition() {
        return FileExcludePatternsExporter.POSITION;
    }

    @Override
    public boolean isJPhotoTaggerData() {
        return true;
    }
}
