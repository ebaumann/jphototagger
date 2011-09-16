package org.jphototagger.repository.hsqldb;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.repository.RepositoryMaintainance;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryMaintainance.class)
public final class RepositoryMaintainanceImpl implements RepositoryMaintainance {

    private DatabaseMaintainance db = DatabaseMaintainance.INSTANCE;

    @Override
    public boolean compressRepository() {
        return db.compressDatabase();
    }

    @Override
    public int deleteNotReferenced1n() {
        return db.deleteNotReferenced1n();
    }

    @Override
    public void shutdownRepository() {
        db.shutdown();
    }
}
