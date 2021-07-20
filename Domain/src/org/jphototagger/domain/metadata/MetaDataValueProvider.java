package org.jphototagger.domain.metadata;

import java.io.File;
import java.util.Collection;
import org.jphototagger.api.collections.PositionProvider;

/**
 * @author Elmar Baumann
 */
public interface MetaDataValueProvider extends PositionProvider {

    Collection<MetaDataValue> getProvidedValues();

    Collection<MetaDataValueData> getMetaDataForImageFile(File file);
}
