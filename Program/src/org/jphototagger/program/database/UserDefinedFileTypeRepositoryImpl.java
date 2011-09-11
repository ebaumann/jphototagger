package org.jphototagger.program.database;

import java.util.List;

import org.jphototagger.domain.repository.UserDefinedFileTypesRepository;
import org.jphototagger.domain.filetypes.UserDefinedFileType;
import org.jphototagger.program.database.DatabaseUserDefinedFileTypes;
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
    public void saveUserDefinedFileType(UserDefinedFileType userDefinedFileType) {
        db.insert(userDefinedFileType);
    }

    @Override
    public void updateUserDefinedFileType(UserDefinedFileType userDefinedFileType) {
        String suffix = userDefinedFileType.getSuffix();
        UserDefinedFileType oldUserDefinedFileType = db.findBySuffix(suffix);

        db.update(oldUserDefinedFileType, userDefinedFileType);
    }

    @Override
    public void saveOrUpdateUserDefinedFileType(UserDefinedFileType userDefinedFileType) {
        String suffix = userDefinedFileType.getSuffix();

        if (db.existsSuffix(suffix)) {
            updateUserDefinedFileType(userDefinedFileType);
        } else {
            saveUserDefinedFileType(userDefinedFileType);
        }

    }

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
}
