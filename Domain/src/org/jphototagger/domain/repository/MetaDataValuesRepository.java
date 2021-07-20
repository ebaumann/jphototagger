package org.jphototagger.domain.repository;

import java.util.Set;
import org.jphototagger.domain.metadata.MetaDataValue;

/**
 * @author Elmar Baumann
 */
public interface MetaDataValuesRepository {

    Set<String> findDistinctMetaDataValues(Set<MetaDataValue> metaDataValues);

    Set<String> findDistinctMetaDataValues(MetaDataValue metaDataValue);
}
