package org.jphototagger.repository.hsqldb;

import org.jphototagger.api.file.FilenameTokens;
import org.jphototagger.domain.repository.FileRepositoryProvider;
import org.jphototagger.domain.repository.Repository;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = Repository.class)
public final class RepositoryImpl implements Repository {

    /**
     * JPhotoTagger's only app database instance
     */
    public static final HsqlDbConnectionPool INSTANCE;

    static {
        INSTANCE = new HsqlDbConnectionPool();
        INSTANCE.setUrl(createUrl());
    }

    private static String createUrl() {
        FileRepositoryProvider provider = Lookup.getDefault().lookup(FileRepositoryProvider.class);
        String file = provider.getFileRepositoryFileName(FilenameTokens.FULL_PATH_NO_SUFFIX);
        return HsqlDbConnectionPool.createFileUrl(file);
    }

    @Override
    public void init() {
        AppDatabase.init();
    }

    @Override
    public boolean isInit() {
        return INSTANCE.isInit();
    }

    @Override
    public void shutdown() {
        DatabaseMaintainance.INSTANCE.shutdown();
    }
}
