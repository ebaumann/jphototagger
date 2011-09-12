package org.jphototagger.repository.hsqldb;

import java.util.List;

import org.jphototagger.domain.filetypes.UserDefinedFileType;
import org.jphototagger.domain.repository.UserDefinedFileTypesRepository;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = UserDefinedFileTypesRepository.class)
public final class UserDefinedFileTypeRepositoryImpl implements UserDefinedFileTypesRepository {

    private final DatabaseUserDefinedFileTypes db = DatabaseUserDefinedFileTypes.INSTANCE;

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
        return DatabaseUserDefinedFileTypes.INSTANCE.findBySuffix(suffix);
    }

    @Override
    public int getMaxLengthSuffix() {
        return DatabaseUserDefinedFileTypes.getMaxLengthSuffix();
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
