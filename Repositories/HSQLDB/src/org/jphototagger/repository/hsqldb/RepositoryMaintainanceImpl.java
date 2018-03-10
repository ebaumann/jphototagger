package org.jphototagger.repository.hsqldb;

import org.jphototagger.api.function.Consumer;
import org.jphototagger.domain.repository.RepositoryMaintainance;
import org.jphototagger.domain.repository.browse.ResultSetBrowserEvent;
import org.openide.util.lookup.ServiceProvider;

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

    @Override
    public void browse(String sql, Consumer<ResultSetBrowserEvent> consumer) {
        ImageFilesDatabase.INSTANCE.browse(sql, consumer);
    }
}
