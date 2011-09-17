package org.jphototagger.program.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openide.util.Lookup;

import org.jphototagger.api.storage.Preferences;
import org.jphototagger.domain.DomainStorageKeys;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.selections.AutoCompleteData;
import org.jphototagger.domain.metadata.selections.AutoCompleteDataOfMetaDataValue;
import org.jphototagger.domain.metadata.selections.FastSearchMetaDataValues;
import org.jphototagger.domain.xmp.Xmp;
import org.jphototagger.lib.componentutil.Autocomplete;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class AutocompleteHelper {

    private AutocompleteHelper() {
    }

    public static void addAutocompleteData(MetaDataValue value, Autocomplete ac, Xmp xmp) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        if (ac == null) {
            throw new NullPointerException("ac == null");
        }

        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        AutoCompleteData acData = AutoCompleteDataOfMetaDataValue.INSTANCE.get(value);

        if ((acData == null) || !isUpdateAutocomplete()) {
            return;
        }

        add(value, acData, ac, xmp);
    }

    private static boolean isUpdateAutocomplete() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(DomainStorageKeys.KEY_UPDATE_AUTOCOMPLETE)
                ? storage.getBoolean(DomainStorageKeys.KEY_UPDATE_AUTOCOMPLETE)
                : true;
    }

    public static void addFastSearchAutocompleteData(Autocomplete ac, Xmp xmp) {
        if (ac == null) {
            throw new NullPointerException("ac == null");
        }

        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        AutoCompleteData acData = AutoCompleteDataOfMetaDataValue.INSTANCE.getFastSearchData();

        if ((acData == null) || !isUpdateAutocomplete()) {
            return;
        }

        for (MetaDataValue vaue : FastSearchMetaDataValues.get()) {
            add(vaue, acData, ac, xmp);
        }
    }

    // Consider to do that in a separate thread
    @SuppressWarnings("unchecked")
    private static void add(MetaDataValue value, AutoCompleteData acData, Autocomplete ac, Xmp xmp) {
        Object xmpValue = xmp.getValue(value);

        if ((xmpValue == null) || !isUpdateAutocomplete()) {
            return;
        }

        List<String> words = new ArrayList<String>();

        if (xmpValue instanceof String) {
            words.add((String) xmpValue);
        } else if (xmpValue instanceof List<?>) {
            List<?> list = (List<?>) xmpValue;
            boolean isStringList = (list.size() > 0)
                    ? list.get(0) instanceof String
                    : false;

            if (isStringList) {
                words = (List<String>) list;
            }
        }

        for (String word : words) {
            acData.add(word);
            ac.add(word);
        }
    }

    // Consider to do that in a separate thread
    public static void addAutocompleteData(MetaDataValue value, Autocomplete ac, Collection<String> words) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        if (ac == null) {
            throw new NullPointerException("ac == null");
        }

        if (words == null) {
            throw new NullPointerException("words == null");
        }

        AutoCompleteData acData = AutoCompleteDataOfMetaDataValue.INSTANCE.get(value);

        if ((acData == null) || !isUpdateAutocomplete()) {
            return;
        }

        for (String word : words) {
            acData.add(word);
            ac.add(word);
        }
    }
}
