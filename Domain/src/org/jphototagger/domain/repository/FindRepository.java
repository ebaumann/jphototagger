package org.jphototagger.domain.repository;

import java.io.File;
import java.util.List;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.search.ParamStatement;

/**
 * @author Elmar Baumann
 */
public interface FindRepository {

    List<File> findImageFiles(ParamStatement paramStatement);

    List<File> findImageFilesLikeOr(List<MetaDataValue> metaDataValues, String searchString);
}
