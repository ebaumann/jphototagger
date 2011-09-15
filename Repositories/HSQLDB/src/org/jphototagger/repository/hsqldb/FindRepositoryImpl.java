package org.jphototagger.repository.hsqldb;

import java.io.File;
import java.util.List;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.search.ParamStatement;
import org.jphototagger.domain.repository.FindRepository;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = FindRepository.class)
public final class FindRepositoryImpl implements FindRepository {

    private final FindDatabase db = FindDatabase.INSTANCE;

    @Override
    public List<File> findImageFiles(ParamStatement paramStatement) {
        return db.findImageFiles(paramStatement);
    }

    @Override
    public List<File> findImageFilesLikeOr(List<MetaDataValue> metaDataValues, String searchString) {
        return db.findImageFilesLikeOr(metaDataValues, searchString);
    }
}
