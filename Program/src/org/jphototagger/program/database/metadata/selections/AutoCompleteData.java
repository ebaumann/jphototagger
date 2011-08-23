package org.jphototagger.program.database.metadata.selections;

import org.jphototagger.lib.util.CollectionUtil;
import org.jphototagger.program.database.DatabaseContent;
import org.jphototagger.domain.database.Column;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.jphototagger.api.core.Storage;
import org.openide.util.Lookup;

/**
 * Contains autocomplete data (words, terms).
 *
 * @author Elmar Baumann
 */
public final class AutoCompleteData {

    private final DatabaseContent db = DatabaseContent.INSTANCE;
    private final LinkedList<String> words = new LinkedList<String>();
    private final Set<Column> columns;

    AutoCompleteData(Collection<? extends Column> columns) {
        this.columns = new LinkedHashSet<Column>(getAutocompleteColumnsOf(columns));
        words.addAll(db.getDistinctValuesOf(this.columns));
        Collections.sort(words);
    }

    AutoCompleteData(Column column) {
        this.columns = new LinkedHashSet<Column>(getAutocompleteColumnsOf(Collections.singleton(column)));
        words.addAll(db.getDistinctValuesOf(column));    // already sorted
    }

    /**
     * Removes from a collection of columns all columns which shouldn't be
     * auto completed.
     *
     * @param  columns columns
     * @return         autocomplete columns or empty set
     */
    private Set<Column> getAutocompleteColumnsOf(Collection<? extends Column> columns) {
        Set<Column> cols = new HashSet<Column>(columns.size());

        for (Column column : columns) {
            if (AutocompleteColumns.contains(column)) {
                cols.add(column);
            }
        }

        return cols;
    }

    // Consider to do that in a separate thread
    public boolean add(String word) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }

        if (isUpdateAutocomplete()) {
            String lcWord = isAutocompleteFastSearchIgnoreCase()
                    ? word.toLowerCase()
                    : word;

            synchronized (words) {
                if (Collections.binarySearch(words, lcWord) < 0) {
                    CollectionUtil.binaryInsert(words, lcWord);

                    return true;
                }
            }
        }

        return false;
    }

    private boolean isAutocompleteFastSearchIgnoreCase() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        return storage.containsKey(Storage.KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE)
                ? storage.getBoolean(Storage.KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE)
                : false;
    }

    private boolean isUpdateAutocomplete() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        return storage.containsKey(Storage.KEY_UPDATE_AUTOCOMPLETE)
                ? storage.getBoolean(Storage.KEY_UPDATE_AUTOCOMPLETE)
                : true;
    }

    /**
     * Returns a <strong>reference</strong> to the list with the autocomplete
     * data.
     *
     * @return autocomplete data
     */
    public List<String> get() {

        // Due performance as reference
        return words;
    }
}
