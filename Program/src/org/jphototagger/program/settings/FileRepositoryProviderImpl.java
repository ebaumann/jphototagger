package org.jphototagger.program.settings;

import java.io.File;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.file.FilenameTokens;
import org.jphototagger.domain.repository.FileRepositoryProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileRepositoryProvider.class)
public final class FileRepositoryProviderImpl implements FileRepositoryProvider {

    @Override
    public File getFileRepositoryDirectory() {
        String databaseDirectoryName = UserPreferences.INSTANCE.getDatabaseDirectoryName();

        return new File(databaseDirectoryName);
    }

    @Override
    public File getDefaultFileRepositoryDirectory() {
        String defaultDatabaseDirectoryName = UserPreferences.INSTANCE.getDefaultDatabaseDirectoryName();

        return new File(defaultDatabaseDirectoryName);
    }

    @Override
    public String getFileRepositoryFileName(FilenameTokens name) {
        return UserPreferences.INSTANCE.getDatabaseFileName(name);
    }

    @Override
    public String getFileRepositoryFileBasename() {
        return UserPreferences.getDatabaseBasename();
    }
}
