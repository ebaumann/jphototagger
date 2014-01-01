package org.jphototagger.repository.hsqldb;

import java.util.List;
import org.jphototagger.domain.filetypes.UserDefinedFileType;
import org.jphototagger.domain.repository.UserDefinedFileTypesRepository;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = UserDefinedFileTypesRepository.class)
public final class UserDefinedFileTypesRepositoryImpl implements UserDefinedFileTypesRepository {

    @Override
    public void deleteUserDefinedFileType(UserDefinedFileType serDefinedFileType) {
        UserDefinedFileTypesDatabase.INSTANCE.delete(serDefinedFileType);
    }

    @Override
    public List<UserDefinedFileType> findAllUserDefinedFileTypes() {
        return UserDefinedFileTypesDatabase.INSTANCE.getAll();
    }

    @Override
    public boolean existsUserDefinedFileTypeWithSuffix(String suffix) {
        return UserDefinedFileTypesDatabase.INSTANCE.existsSuffix(suffix);
    }

    @Override
    public UserDefinedFileType findUserDefinedFileTypeBySuffix(String suffix) {
        return UserDefinedFileTypesDatabase.INSTANCE.findBySuffix(suffix);
    }

    @Override
    public int getMaxLengthSuffix() {
        return UserDefinedFileTypesDatabase.getMaxLengthSuffix();
    }

    @Override
    public int saveUserDefinedFileType(UserDefinedFileType userDefinedFileType) {
        return UserDefinedFileTypesDatabase.INSTANCE.insert(userDefinedFileType);
    }

    @Override
    public int updateUserDefinedFileType(UserDefinedFileType oldUserDefinedFileType, UserDefinedFileType newUserDefinedFileType) {
        return UserDefinedFileTypesDatabase.INSTANCE.update(oldUserDefinedFileType, newUserDefinedFileType);
    }
}
