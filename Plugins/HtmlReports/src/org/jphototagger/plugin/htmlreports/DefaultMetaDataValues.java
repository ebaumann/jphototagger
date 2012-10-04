package org.jphototagger.plugin.htmlreports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValues;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
final class DefaultMetaDataValues {

    static final DefaultMetaDataValues INSTANCE = new DefaultMetaDataValues();
    private static final String KEY_DEFAULT_VALUES = "HtmlReports.DefaultValues";
    private static final Map<String, MetaDataValue> META_DATA_VALUE_OF_CLASSNAME = new HashMap<String, MetaDataValue>();

    static {
        for (MetaDataValue metaDataValue : MetaDataValues.get()) {
            META_DATA_VALUE_OF_CLASSNAME.put(metaDataValue.getClass().getName(), metaDataValue);
        }
    }

    void setValues(Collection<MetaDataValue> values) {
        List<String> valuesToPersist = new ArrayList<String>(values.size());
        for (MetaDataValue metaDataValue : values) {
            valuesToPersist.add(metaDataValue.getClass().getName());
        }
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        if (preferences != null) {
            preferences.setStringCollection(KEY_DEFAULT_VALUES, valuesToPersist);
        }
    }

    Collection<MetaDataValue> getValues() {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        if (preferences != null) {
            List<String> persistedValues = preferences.getStringCollection(KEY_DEFAULT_VALUES);
            List<MetaDataValue> values = new ArrayList<MetaDataValue>(persistedValues.size());
            for (String className : persistedValues) {
                MetaDataValue metaDataValue = META_DATA_VALUE_OF_CLASSNAME.get(className);
                if (metaDataValue != null) {
                    values.add(metaDataValue);
                }
            }
            return values;
        }
        return Collections.emptyList();
    }

    private DefaultMetaDataValues() {
    }
}
