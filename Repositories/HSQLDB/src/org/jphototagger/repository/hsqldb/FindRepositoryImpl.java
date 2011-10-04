package org.jphototagger.repository.hsqldb;

import java.io.File;
import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.search.ParamStatement;
import org.jphototagger.domain.repository.FindRepository;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FindRepository.class)
public final class FindRepositoryImpl implements FindRepository {

    @Override
    public List<File> findImageFiles(ParamStatement paramStatement) {
        return FindDatabase.INSTANCE.findImageFiles(paramStatement);
    }

    @Override
    public List<File> findImageFilesLikeOr(List<MetaDataValue> metaDataValues, String searchString) {
        return FindDatabase.INSTANCE.findImageFilesLikeOr(metaDataValues, searchString);
    }
}
