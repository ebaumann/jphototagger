package org.jphototagger.domain.metadata.selections;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.repository.MetaDataValuesRepository;
import org.jphototagger.lib.util.CollectionUtil;
import org.openide.util.Lookup;

/**
 * Contains autocomplete data (words, terms).
 *
 * @author Elmar Baumann
 */
public final class AutoCompleteData {

    private final LinkedList<String> words = new LinkedList<String>();
    private final Set<MetaDataValue> metaDataValues;
    private final MetaDataValuesRepository repo = Lookup.getDefault().lookup(MetaDataValuesRepository.class);

    AutoCompleteData(Collection<? extends MetaDataValue> values) {
        this.metaDataValues = new LinkedHashSet<MetaDataValue>(getAutocompleteMetaDataValuesOf(values));
        words.addAll(repo.findDistinctMetaDataValues(this.metaDataValues));
        Collections.sort(words);
    }

    AutoCompleteData(MetaDataValue value) {
        this.metaDataValues = new LinkedHashSet<MetaDataValue>(getAutocompleteMetaDataValuesOf(Collections.singleton(value)));
        words.addAll(repo.findDistinctMetaDataValues(value));    // already sorted
    }

    /**
     * Removes from a collection of values which shouldn't be auto completed.
     *
     * @param  values
     * @return         autocomplete values or empty set
     */
    private Set<MetaDataValue> getAutocompleteMetaDataValuesOf(Collection<? extends MetaDataValue> values) {
        Set<MetaDataValue> cols = new HashSet<MetaDataValue>(values.size());

        for (MetaDataValue value : values) {
            if (AutocompleteMetaDataValues.contains(value)) {
                cols.add(value);
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
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(DomainPreferencesKeys.KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE)
                ? prefs.getBoolean(DomainPreferencesKeys.KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE)
                : false;
    }

    private boolean isUpdateAutocomplete() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(DomainPreferencesKeys.KEY_UPDATE_AUTOCOMPLETE)
                ? prefs.getBoolean(DomainPreferencesKeys.KEY_UPDATE_AUTOCOMPLETE)
                : true;
    }

    /**
     * Returns a <strong>reference</strong> to the list with the autocomplete
     * data.
     *
     * @return autocomplete data
     */
    public List<String> get() {

        // No list copy due performance
        return words;
    }
}
