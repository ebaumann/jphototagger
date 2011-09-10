package org.jphototagger.program.database.metadata.selections;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jphototagger.domain.metadata.MetaDataValue;

/**
 * Contains autocomplete data of specific value.
 *
 * @author Elmar Baumann
 */
public final class AutoCompleteDataOfMetaDataValue {

    public static final AutoCompleteDataOfMetaDataValue INSTANCE = new AutoCompleteDataOfMetaDataValue();
    private static final AutoCompleteData FAST_SEARCH_DATA = new AutoCompleteData(FastSearchMetaDataValues.get());
    private static final Map<MetaDataValue, AutoCompleteData> DATA_OF_META_DATA_VALUE = new HashMap<MetaDataValue, AutoCompleteData>();

    private AutoCompleteDataOfMetaDataValue() {
    }

    /**
     * Returns the autocomplete data of a specific value.
     *
     * @param  value
     * @return        autocomplete data of that value
     */
    public AutoCompleteData get(MetaDataValue value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        synchronized (DATA_OF_META_DATA_VALUE) {
            AutoCompleteData data = DATA_OF_META_DATA_VALUE.get(value);

            if (data == null) {
                data = new AutoCompleteData(Arrays.asList(value));
                DATA_OF_META_DATA_VALUE.put(value, data);
            }

            return data;
        }
    }

    public AutoCompleteData getFastSearchData() {
        return FAST_SEARCH_DATA;
    }
}
