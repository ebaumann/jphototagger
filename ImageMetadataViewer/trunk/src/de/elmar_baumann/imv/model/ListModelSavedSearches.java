package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.comparator.ComparatorSavedSearch;
import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.database.DatabaseSavedSearches;
import de.elmar_baumann.lib.componentutil.ListUtil;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-17
 */
public final class ListModelSavedSearches extends DefaultListModel {

    public ListModelSavedSearches() {
        addElements();
    }

    private void addElements() {
        List<SavedSearch> savedSearches =
                DatabaseSavedSearches.INSTANCE.getSavedSearches();
        for (SavedSearch savedSearch : savedSearches) {
            addElement(savedSearch);
        }
    }

    public void rename(SavedSearch oldSavedSearch, SavedSearch newSavedSearch) {
        int index = indexOf(oldSavedSearch);
        if (index >= 0) {
            remove(index);
            ListUtil.insertSorted(
                    this, newSavedSearch, ComparatorSavedSearch.INSTANCE,
                    0, getSize() - 1);
        }
    }
}
