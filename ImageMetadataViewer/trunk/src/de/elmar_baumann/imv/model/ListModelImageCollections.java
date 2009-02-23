package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.comparator.ComparatorStringAscending;
import de.elmar_baumann.imv.database.DatabaseImageCollections;
import de.elmar_baumann.lib.componentutil.ListUtil;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * Enthält die Namen aller Bildsammlungen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ListModelImageCollections extends DefaultListModel {

    public ListModelImageCollections() {
        addItems();
    }

    private void addItems() {
        DatabaseImageCollections db = DatabaseImageCollections.INSTANCE;
        List<String> collections = db.getImageCollectionNames();
        for (String collection : collections) {
            addElement(collection);
        }
    }

    public void fireContentsChanged(int index) {
        if (index >= 0 && index < size()) {
            fireContentsChanged(this, index, index);
        }
    }

    public void rename(String oldName, String newName) {
        int index = indexOf(oldName);
        if (index >= 0) {
            remove(index);
            ListUtil.insertSorted(this, newName, ComparatorStringAscending.IGNORE_CASE);
        }
    }
}
