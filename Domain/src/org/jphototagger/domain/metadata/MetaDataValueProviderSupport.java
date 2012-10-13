package org.jphototagger.domain.metadata;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class MetaDataValueProviderSupport {

    private final Map<MetaDataValue, List<MetaDataValueProvider>> providersOfValue = new HashMap<>();

    public MetaDataValueProviderSupport() {
        lookupProviders();
    }

    private void lookupProviders() {
        Collection<? extends MetaDataValueProvider> providers = Lookup.getDefault().lookupAll(MetaDataValueProvider.class);
        for (MetaDataValueProvider provider : providers) {
            for (MetaDataValue value : provider.getProvidedValues()) {
                List<MetaDataValueProvider> valueProviders = providersOfValue.get(value);
                if (valueProviders == null) {
                    valueProviders = new ArrayList<>();
                    providersOfValue.put(value, valueProviders);
                }
                valueProviders.add(provider);
            }
        }
    }

    public Collection<MetaDataValueData> lookupMetaDataForFile(File file, MetaDataValue forValue) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        if (forValue == null) {
            throw new NullPointerException("forValue == null");
        }
        List<MetaDataValueData> metaData = new ArrayList<>();
        List<MetaDataValueProvider> valueProviders = providersOfValue.get(forValue);
        if (valueProviders != null) {
            for (MetaDataValueProvider provider : valueProviders) {
                for (MetaDataValueData valueData : provider.getMetaDataForImageFile(file)) {
                    if (forValue.equals(valueData.getMetaDataValue())) {
                       metaData.add(valueData);
                    }
                }
            }
        }
        return metaData;
    }
}
