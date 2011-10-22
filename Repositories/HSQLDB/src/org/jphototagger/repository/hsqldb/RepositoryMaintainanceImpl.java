package org.jphototagger.repository.hsqldb;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.repository.RepositoryMaintainance;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryMaintainance.class)
public final class RepositoryMaintainanceImpl implements RepositoryMaintainance {

    @Override
    public boolean compressRepository() {
        return DatabaseMaintainance.INSTANCE.compressDatabase();
    }

    @Override
    public int deleteNotReferenced1n() {
        return DatabaseMaintainance.INSTANCE.deleteNotReferenced1n();
    }
}
