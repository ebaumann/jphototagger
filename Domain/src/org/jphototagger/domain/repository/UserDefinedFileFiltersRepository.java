package org.jphototagger.domain.repository;

import java.util.Set;

import org.jphototagger.domain.filefilter.UserDefinedFileFilter;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface UserDefinedFileFiltersRepository {

    boolean deleteUserDefinedFileFilter(UserDefinedFileFilter filter);

    boolean existsUserDefinedFileFilter(String name);

    Set<UserDefinedFileFilter> findAllUserDefinedFileFilters();

    boolean saveUserDefinedFileFilter(UserDefinedFileFilter filter);

    boolean updateUserDefinedFileFilter(UserDefinedFileFilter filter);
}
