package org.jphototagger.domain.repository;

import java.util.List;
import org.jphototagger.domain.metadata.MetaDataValue;

/**
 * @author Elmar Baumann
 */
public interface RepositoryStatistics {

    boolean existsMetaDataValue(MetaDataValue metaDataValue, String value);

    boolean existsValueInMetaDataValues(String value, List<MetaDataValue> metaDataValues);

    int getCountOfMetaDataValue(MetaDataValue metaDataValue);

    int getFileCount();

    int getXmpCount();
}
