package org.jphototagger.repository.hsqldb;

import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.filetypes.UserDefinedFileType;
import org.jphototagger.domain.repository.UserDefinedFileTypesRepository;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = UserDefinedFileTypesRepository.class)
public final class UserDefinedFileTypeRepositoryImpl implements UserDefinedFileTypesRepository {

    private final UserDefinedFileTypesDatabase db = UserDefinedFileTypesDatabase.INSTANCE;

    @Override
    public void deleteUserDefinedFileType(UserDefinedFileType serDefinedFileType) {
        db.delete(serDefinedFileType);
    }

    @Override
    public List<UserDefinedFileType> findAllUserDefinedFileTypes() {
        return db.getAll();
    }

    @Override
    public boolean existsUserDefinedFileTypeWithSuffix(String suffix) {
        return db.existsSuffix(suffix);
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
        return db.insert(userDefinedFileType);
    }

    @Override
    public int updateUserDefinedFileType(UserDefinedFileType oldUserDefinedFileType, UserDefinedFileType newUserDefinedFileType) {
        return db.update(oldUserDefinedFileType, newUserDefinedFileType);
    }
}
