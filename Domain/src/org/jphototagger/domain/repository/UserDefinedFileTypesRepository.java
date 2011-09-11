package org.jphototagger.domain.repository;

import java.util.List;
import org.jphototagger.domain.filetypes.UserDefinedFileType;

/**
 *
 * @author Elmar Baumann
 */
public interface UserDefinedFileTypesRepository {

    void saveUserDefinedFileType(UserDefinedFileType userDefinedFileType);

    void updateUserDefinedFileType(UserDefinedFileType userDefinedFileType);

    void saveOrUpdateUserDefinedFileType(UserDefinedFileType userDefinedFileType);

    void deleteUserDefinedFileType(UserDefinedFileType serDefinedFileType);

    boolean existsUserDefinedFileTypeWithSuffix(String suffix);

    List<UserDefinedFileType> findAllUserDefinedFileTypes();

    UserDefinedFileType findUserDefinedFileTypeBySuffix(String suffix);
}
