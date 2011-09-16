package org.jphototagger.repository.hsqldb;

import java.util.Set;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.filefilter.UserDefinedFileFilter;
import org.jphototagger.domain.repository.UserDefinedFileFiltersRepository;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = UserDefinedFileFiltersRepository.class)
public final class UserDefinedFileFiltersRepositoryImpl implements UserDefinedFileFiltersRepository {

    private final UserDefinedFileFiltersDatabase db = UserDefinedFileFiltersDatabase.INSTANCE;

    @Override
    public boolean deleteUserDefinedFileFilter(UserDefinedFileFilter filter) {
        return db.deleteUserDefinedFileFilter(filter);
    }

    @Override
    public boolean existsUserDefinedFileFilter(String name) {
        return db.existsUserDefinedFileFilter(name);
    }

    @Override
    public Set<UserDefinedFileFilter> findAllUserDefinedFileFilters() {
        return db.getAllUserDefinedFileFilters();
    }

    @Override
    public boolean saveUserDefinedFileFilter(UserDefinedFileFilter filter) {
        return db.insertUserDefinedFileFilter(filter);
    }

    @Override
    public boolean updateUserDefinedFileFilter(UserDefinedFileFilter filter) {
        return db.updateUserDefinedFileFilter(filter);
    }
}
