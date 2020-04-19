package org.jphototagger.importfiles.subdircreators.templates;

import java.io.File;
import java.util.Objects;
import org.jphototagger.api.storage.PreferencesDirectoryProvider;
import org.jphototagger.lib.xml.bind.XmlObjectExporter;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;
import org.openide.util.Lookup;

/**
 * Loads a {@link SubdirectoryTemplates} instance from a file and persists it to
 * that file.
 *
 * @author Elmar Baumann
 */
public final class SubdirectoryTemplatesRepository {

    private final File repositoryFile;

    /**
     * Creates an instance and uses a file with a default name located in the
     * user's preferences directory.
     */
    public SubdirectoryTemplatesRepository() {
        this(getDefaultRepositoryFile());
    }

    public SubdirectoryTemplatesRepository(File repositoryFile) {
        this.repositoryFile = Objects.requireNonNull(repositoryFile, "repositoryFile == null");
    }

    private static File getDefaultRepositoryFile() {
        PreferencesDirectoryProvider pp = Lookup.getDefault().lookup(PreferencesDirectoryProvider.class);
        File dir = pp.getUserPreferencesDirectory();
        return new File(dir, "UserDefinedSubdirectoryCreateStrategies.xml");
    }

    /**
     * @return {@code SubdirectoryTemplates} instance or null, if the repository
     *         file does not exist
     *
     * @throws Exception when the repository file exists but could not converted
     *                   into an {@code SubdirectoryTemplates} instance
     */
    public SubdirectoryTemplates load() throws Exception {
        if (!repositoryFile.isFile()) {
            return null;
        }

        return (SubdirectoryTemplates) XmlObjectImporter.importObject(repositoryFile, SubdirectoryTemplates.class);
    }

    public void save(SubdirectoryTemplates templates) throws Exception {
        XmlObjectExporter.export(templates, repositoryFile);
    }
}
