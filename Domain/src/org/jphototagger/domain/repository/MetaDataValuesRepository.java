package org.jphototagger.domain.repository;

import java.util.Set;

import org.jphototagger.domain.metadata.MetaDataValue;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface MetaDataValuesRepository {

    Set<String> getDistinctMetaDataValues(Set<MetaDataValue> metaDataValues);

    Set<String> getDistinctMetaDataValues(MetaDataValue metaDataValue);
}
