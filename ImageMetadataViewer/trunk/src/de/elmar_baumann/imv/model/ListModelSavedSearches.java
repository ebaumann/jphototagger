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
        List<SavedSearch> searches =
                DatabaseSavedSearches.INSTANCE.getSavedSearches();
        for (SavedSearch search : searches) {
            addElement(search);
        }
    }

    public void rename(SavedSearch oldSearch, SavedSearch newSearch) {
        int index = indexOf(oldSearch);
        if (index >= 0) {
            remove(index);
            ListUtil.insertSorted(
                    this, newSearch, ComparatorSavedSearch.INSTANCE, 0);
        }
    }
}
