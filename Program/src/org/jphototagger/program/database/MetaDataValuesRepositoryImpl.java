package org.jphototagger.program.database;

import java.util.Set;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.repository.MetaDataValuesRepository;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = MetaDataValuesRepository.class)
public final class MetaDataValuesRepositoryImpl implements MetaDataValuesRepository {

    private final DatabaseMetaDataValues db = DatabaseMetaDataValues.INSTANCE;

    @Override
    public Set<String> getDistinctMetaDataValues(Set<MetaDataValue> metaDataValues) {
        return db.getDistinctMetaDataValues(metaDataValues);
    }

    @Override
    public Set<String> getDistinctMetaDataValues(MetaDataValue metaDataValue) {
        return db.getDistinctMetaDataValues(metaDataValue);
    }
}
