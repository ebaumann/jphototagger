package org.jphototagger.program.importer;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

import org.jphototagger.lib.xml.bind.XmlObjectImporter;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.database.DatabaseAutoscanDirectories;
import org.jphototagger.program.exporter.AutoscanDirectoriesExporter;
import org.jphototagger.program.exporter.AutoscanDirectoriesExporter.CollectionWrapper;
import org.jphototagger.program.exporter.StringWrapper;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class AutoscanDirectoriesImporter implements Importer {
    public static final AutoscanDirectoriesImporter INSTANCE = new AutoscanDirectoriesImporter();

    private AutoscanDirectoriesImporter() {}

    @Override
    public void importFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            AutoscanDirectoriesExporter.CollectionWrapper wrapper =
                (CollectionWrapper) XmlObjectImporter.importObject(file,
                    AutoscanDirectoriesExporter.CollectionWrapper.class);

            for (StringWrapper stringWrapper : wrapper.getCollection()) {
                if (!DatabaseAutoscanDirectories.INSTANCE.exists(new File(stringWrapper.getString()))) {
                    DatabaseAutoscanDirectories.INSTANCE.insert(new File(stringWrapper.getString()));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(AutoscanDirectoriesImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return AutoscanDirectoriesExporter.INSTANCE.getFileFilter();
    }

    @Override
    public String getDisplayName() {
        return AutoscanDirectoriesExporter.INSTANCE.getDisplayName();
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_import.png");
    }

    @Override
    public String getDefaultFilename() {
        return AutoscanDirectoriesExporter.INSTANCE.getDefaultFilename();
    }
}
