package org.jphototagger.repository.hsqldb;

import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.repository.RepositoryStatistics;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryStatistics.class)
public final class RepositoryStatisticsImpl implements RepositoryStatistics {

    @Override
    public boolean existsMetaDataValue(MetaDataValue metaDataValue, String value) {
        return DatabaseStatistics.INSTANCE.existsMetaDataValue(metaDataValue, value);
    }

    @Override
    public boolean existsValueInMetaDataValues(String value, List<MetaDataValue> metaDataValues) {
        return DatabaseStatistics.INSTANCE.existsValueInMetaDataValues(value, metaDataValues);
    }

    @Override
    public int getCountOfMetaDataValue(MetaDataValue metaDataValue) {
        return DatabaseStatistics.INSTANCE.getCountOfMetaDataValue(metaDataValue);
    }

    @Override
    public int getFileCount() {
        return DatabaseStatistics.INSTANCE.getFileCount();
    }

    @Override
    public int getXmpCount() {
        return DatabaseStatistics.INSTANCE.getXmpCount();
    }
}
