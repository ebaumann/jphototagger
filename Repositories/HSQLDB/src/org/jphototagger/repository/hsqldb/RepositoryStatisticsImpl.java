package org.jphototagger.repository.hsqldb;

import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.repository.RepositoryStatistics;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryStatistics.class)
public final class RepositoryStatisticsImpl implements RepositoryStatistics {

    private final DatabaseStatistics db = DatabaseStatistics.INSTANCE;

    @Override
    public boolean existsMetaDataValue(MetaDataValue metaDataValue, String value) {
        return db.existsMetaDataValue(metaDataValue, value);
    }

    @Override
    public boolean existsValueInMetaDataValues(String value, List<MetaDataValue> metaDataValues) {
        return db.existsValueInMetaDataValues(value, metaDataValues);
    }

    @Override
    public int getCountOfMetaDataValue(MetaDataValue metaDataValue) {
        return db.getCountOfMetaDataValue(metaDataValue);
    }

    @Override
    public int getFileCount() {
        return db.getFileCount();
    }

    @Override
    public int getXmpCount() {
        return db.getXmpCount();
    }
}
