package org.jphototagger.program.database.metadata.selections;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.UserSettings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains autocomplete data of specific columns.
 *
 * @author Elmar Baumann
 */
public final class AutoCompleteDataOfColumn {
    public static final AutoCompleteDataOfColumn INSTANCE = new AutoCompleteDataOfColumn();
    private static final AutoCompleteData FAST_SEARCH_DATA = new AutoCompleteData(FastSearchColumns.get());
    private static final Map<Column, AutoCompleteData> DATA_OF_COLUMN = new HashMap<Column, AutoCompleteData>();

    private AutoCompleteDataOfColumn() {}

    /**
     * Returns the autocomplete data of a specific column.
     *
     * @param  column column
     * @return        autocomplete data of that column
     */
    public AutoCompleteData get(Column column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        assert UserSettings.INSTANCE.isAutocomplete();

        synchronized (DATA_OF_COLUMN) {
            AutoCompleteData data = DATA_OF_COLUMN.get(column);

            if (data == null) {
                data = new AutoCompleteData(Arrays.asList(column));
                DATA_OF_COLUMN.put(column, data);
            }

            return data;
        }
    }

    public AutoCompleteData getFastSearchData() {
        assert UserSettings.INSTANCE.isAutocomplete();

        return FAST_SEARCH_DATA;
    }
}
