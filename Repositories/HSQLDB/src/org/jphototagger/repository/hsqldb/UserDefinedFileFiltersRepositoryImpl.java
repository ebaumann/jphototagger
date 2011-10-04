package org.jphototagger.repository.hsqldb;

import java.util.Set;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.filefilter.UserDefinedFileFilter;
import org.jphototagger.domain.repository.UserDefinedFileFiltersRepository;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = UserDefinedFileFiltersRepository.class)
public final class UserDefinedFileFiltersRepositoryImpl implements UserDefinedFileFiltersRepository {

    @Override
    public boolean deleteUserDefinedFileFilter(UserDefinedFileFilter filter) {
        return UserDefinedFileFiltersDatabase.INSTANCE.deleteUserDefinedFileFilter(filter);
    }

    @Override
    public boolean existsUserDefinedFileFilter(String name) {
        return UserDefinedFileFiltersDatabase.INSTANCE.existsUserDefinedFileFilter(name);
    }

    @Override
    public Set<UserDefinedFileFilter> findAllUserDefinedFileFilters() {
        return UserDefinedFileFiltersDatabase.INSTANCE.getAllUserDefinedFileFilters();
    }

    @Override
    public boolean saveUserDefinedFileFilter(UserDefinedFileFilter filter) {
        return UserDefinedFileFiltersDatabase.INSTANCE.insertUserDefinedFileFilter(filter);
    }

    @Override
    public boolean updateUserDefinedFileFilter(UserDefinedFileFilter filter) {
        return UserDefinedFileFiltersDatabase.INSTANCE.updateUserDefinedFileFilter(filter);
    }
}
