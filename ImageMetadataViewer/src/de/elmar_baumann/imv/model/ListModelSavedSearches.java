package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.comparator.ComparatorSavedSearch;
import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.database.DatabaseSavedSearches;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/17
 */
public class ListModelSavedSearches extends DefaultListModel {

    public ListModelSavedSearches() {
        addItems();
    }

    private void addItems() {
        List<SavedSearch> searches = DatabaseSavedSearches.getInstance().getSavedSearches();
        for (SavedSearch search : searches) {
            addElement(search);
        }
    }

    synchronized public void insertSorted(SavedSearch search) {
        if (!contains(search)) {
            int size = getSize();
            boolean inserted = false;
            ComparatorSavedSearch comparator = new ComparatorSavedSearch();
            for (int i = 0; !inserted && i < size; i++) {
                if (comparator.compare(search, (SavedSearch) get(i)) < 0) {
                    add(i, search);
                    inserted = true;
                }
            }
            if (!inserted) {
                addElement(search);
            }
        }
    }

    public void rename(SavedSearch oldSearch, SavedSearch newSearch) {
        int index = indexOf(oldSearch);
        if (index >= 0) {
            remove(index);
            insertSorted(newSearch);
        }
    }
}
