package org.jphototagger.program.settings;

import java.io.File;
import org.jphototagger.api.file.FilenameTokens;
import org.jphototagger.domain.repository.FileRepositoryProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileRepositoryProvider.class)
public final class FileRepositoryProviderImpl implements FileRepositoryProvider {

    @Override
    public File getFileRepositoryDirectory() {
        String repositoryDirectoryName = UserPreferences.INSTANCE.getRepositoryDirectoryName();

        return new File(repositoryDirectoryName);
    }

    @Override
    public File getDefaultFileRepositoryDirectory() {
        String defaultRepositoryDirectoryName = UserPreferences.INSTANCE.getDefaultRepositoryDirectoryName();

        return new File(defaultRepositoryDirectoryName);
    }

    @Override
    public String getFileRepositoryFileName(FilenameTokens filenameTokens) {
        return UserPreferences.INSTANCE.getRepositoryFileName(filenameTokens);
    }
}
