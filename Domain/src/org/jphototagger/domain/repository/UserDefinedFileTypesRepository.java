package org.jphototagger.domain.repository;

import java.util.List;
import org.jphototagger.domain.filetypes.UserDefinedFileType;

/**
 * @author Elmar Baumann
 */
public interface UserDefinedFileTypesRepository {

    void deleteUserDefinedFileType(UserDefinedFileType serDefinedFileType);

    boolean existsUserDefinedFileTypeWithSuffix(String suffix);

    List<UserDefinedFileType> findAllUserDefinedFileTypes();

    UserDefinedFileType findUserDefinedFileTypeBySuffix(String suffix);

    int getMaxLengthSuffix();

    int saveUserDefinedFileType(UserDefinedFileType userDefinedFileType);

    int updateUserDefinedFileType(UserDefinedFileType oldUserDefinedFileType, UserDefinedFileType newUserDefinedFileType);
}
