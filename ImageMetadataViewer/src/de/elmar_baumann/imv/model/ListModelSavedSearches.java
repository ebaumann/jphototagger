package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.database.Database;
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
        List<SavedSearch> searches = Database.getInstance().getSavedSearches();
        for (SavedSearch search : searches) {
            addElement(search);
        }
    }
    
    public void rename(SavedSearch oldSearch, SavedSearch newSearch) {
        int index = indexOf(oldSearch);
        if (index >= 0) {
            remove(index);
            add(index, newSearch);
        }
    }
}
