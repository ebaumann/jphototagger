package org.jphototagger.repository.hsqldb;

import org.jphototagger.domain.repository.Repository;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = Repository.class)
public final class RepositoryImpl implements Repository {

    private final ConnectionPool pool = ConnectionPool.INSTANCE;

    @Override
    public void init() {
        AppDatabase.init();
    }

    @Override
    public boolean isInit() {
        return pool.isInit();
    }

    @Override
    public void shutdown() {
        DatabaseMaintainance.INSTANCE.shutdown();
    }
}
