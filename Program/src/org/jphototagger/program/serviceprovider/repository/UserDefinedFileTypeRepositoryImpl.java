package org.jphototagger.program.serviceprovider.repository;

import java.util.List;

import org.jphototagger.domain.database.repository.UserDefinedFileTypesRepository;
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
    public void save(UserDefinedFileType userDefinedFileType) {
        db.insert(userDefinedFileType);
    }

    @Override
    public void update(UserDefinedFileType userDefinedFileType) {
        String suffix = userDefinedFileType.getSuffix();
        UserDefinedFileType oldUserDefinedFileType = db.findBySuffix(suffix);

        db.update(oldUserDefinedFileType, userDefinedFileType);
    }

    @Override
    public void saveOrUpdate(UserDefinedFileType userDefinedFileType) {
        String suffix = userDefinedFileType.getSuffix();

        if (db.existsSuffix(suffix)) {
            update(userDefinedFileType);
        } else {
            save(userDefinedFileType);
        }

    }

    @Override
    public void remove(UserDefinedFileType serDefinedFileType) {
        db.delete(serDefinedFileType);
    }

    @Override
    public List<UserDefinedFileType> findAll() {
        return db.getAll();
    }

    @Override
    public boolean existsFileTypeWithSuffix(String suffix) {
        return db.existsSuffix(suffix);
    }

    @Override
    public UserDefinedFileType findBySuffix(String suffix) {
        return DatabaseUserDefinedFileTypes.INSTANCE.findBySuffix(suffix);
    }
}
