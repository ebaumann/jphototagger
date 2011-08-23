package org.jphototagger.program.helper;

import org.jphototagger.lib.componentutil.Autocomplete;
import org.jphototagger.domain.xmp.Xmp;
import org.jphototagger.domain.database.Column;
import org.jphototagger.program.database.metadata.selections.AutoCompleteData;
import org.jphototagger.program.database.metadata.selections.AutoCompleteDataOfColumn;
import org.jphototagger.program.database.metadata.selections.FastSearchColumns;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jphototagger.api.core.Storage;
import org.openide.util.Lookup;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class AutocompleteHelper {

    private AutocompleteHelper() {
    }

    public static void addAutocompleteData(Column column, Autocomplete ac, Xmp xmp) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        if (ac == null) {
            throw new NullPointerException("ac == null");
        }

        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        AutoCompleteData acData = AutoCompleteDataOfColumn.INSTANCE.get(column);

        if ((acData == null) || !isUpdateAutocomplete()) {
            return;
        }

        add(column, acData, ac, xmp);
    }

    private static boolean isUpdateAutocomplete() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        return storage.containsKey(Storage.KEY_UPDATE_AUTOCOMPLETE)
                ? storage.getBoolean(Storage.KEY_UPDATE_AUTOCOMPLETE)
                : true;
    }

    public static void addFastSearchAutocompleteData(Autocomplete ac, Xmp xmp) {
        if (ac == null) {
            throw new NullPointerException("ac == null");
        }

        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        AutoCompleteData acData = AutoCompleteDataOfColumn.INSTANCE.getFastSearchData();

        if ((acData == null) || !isUpdateAutocomplete()) {
            return;
        }

        for (Column column : FastSearchColumns.get()) {
            add(column, acData, ac, xmp);
        }
    }

    // Consider to do that in a separate thread
    @SuppressWarnings("unchecked")
    private static void add(Column column, AutoCompleteData acData, Autocomplete ac, Xmp xmp) {
        Object xmpValue = xmp.getValue(column);

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
    public static void addAutocompleteData(Column column, Autocomplete ac, Collection<String> words) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        if (ac == null) {
            throw new NullPointerException("ac == null");
        }

        if (words == null) {
            throw new NullPointerException("words == null");
        }

        AutoCompleteData acData = AutoCompleteDataOfColumn.INSTANCE.get(column);

        if ((acData == null) || !isUpdateAutocomplete()) {
            return;
        }

        for (String word : words) {
            acData.add(word);
            ac.add(word);
        }
    }
}
