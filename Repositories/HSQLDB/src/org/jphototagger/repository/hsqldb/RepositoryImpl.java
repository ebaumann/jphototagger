package org.jphototagger.repository.hsqldb;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.repository.Repository;

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
