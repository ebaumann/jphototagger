package org.jphototagger.importfiles.subdircreators.templates;

import java.io.File;
import org.jphototagger.api.storage.PreferencesDirectoryProvider;
import org.jphototagger.lib.xml.bind.XmlObjectExporter;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;
import org.openide.util.Lookup;

/**
 * Loads a {@link SubdirectoryTemplates} instance from a file and persists it to
 * a file. The filename is fix and located in the user's preferences directory.
 *
 * @author Elmar Baumann
 */
public final class SubdirectoryTemplatesRepository {

    private static final String FILENAME = "UserDefinedSubdirectoryCreateStrategies.xml";

    /**
     * @return {@code SubdirectoryTemplates} instance or null, if the repository
     *         file does not exist
     *
     * @throws Exception when the repository file exists but could not converted
     *                   into an {@code SubdirectoryTemplates} instance
     */
    public SubdirectoryTemplates load() throws Exception {
        File file = getRepositoryFile();

        if (!file.isFile()) {
            return null;
        }

        return (SubdirectoryTemplates) XmlObjectImporter.importObject(file, SubdirectoryTemplates.class);
    }

    public void save(SubdirectoryTemplates templates) throws Exception {
        XmlObjectExporter.export(templates, getRepositoryFile());
    }

    private File getRepositoryFile() {
        PreferencesDirectoryProvider pp = Lookup.getDefault().lookup(PreferencesDirectoryProvider.class);
        File dir = pp.getUserPreferencesDirectory();
        return new File(dir, FILENAME);
    }
}
