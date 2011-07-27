package org.jphototagger.services.repository;

import java.util.List;
import org.jphototagger.domain.UserDefinedFileType;

/**
 *
 * @author Elmar Baumann
 */
public interface UserDefinedFileTypesRepository {

    void save(UserDefinedFileType userDefinedFileType);

    void update(UserDefinedFileType userDefinedFileType);

    void saveOrUpdate(UserDefinedFileType userDefinedFileType);

    void remove(UserDefinedFileType serDefinedFileType);

    boolean existsFileTypeWithSuffix(String suffix);

    List<UserDefinedFileType> findAll();
}
