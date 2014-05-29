package org.jphototagger.repository.hsqldb;

import java.io.File;
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

    @Override
    public void backupToDirectory(File directory) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
