package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.app.AppTexts;
import de.elmar_baumann.imv.comparator.ComparatorStringAscending;
import de.elmar_baumann.imv.database.DatabaseImageCollections;
import de.elmar_baumann.lib.componentutil.ListUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * Enthält die Namen aller Bildsammlungen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ListModelImageCollections extends DefaultListModel {

    private static final List<String> SPECIAL_COLLECTIONS =
            new ArrayList<String>();

    {
        // Order of appearance
        SPECIAL_COLLECTIONS.add(
                AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PREV_IMPORT);
        SPECIAL_COLLECTIONS.add(
                AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PICKED);
        SPECIAL_COLLECTIONS.add(
                AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_REJECTED);
    }

    public ListModelImageCollections() {
        addElements();
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
            ListUtil.insertSorted(this, newName,
                    ComparatorStringAscending.IGNORE_CASE, 0);
        }
    }

    private void addElements() {
        DatabaseImageCollections db = DatabaseImageCollections.INSTANCE;
        List<String> collections = db.getImageCollectionNames();
        addSpecialCollections();
        for (String collection : collections) {
            if (!isSpecialCollection(collection)) {
                addElement(collection);
            }
        }
    }

    private void addSpecialCollections() {
        for (String collection : SPECIAL_COLLECTIONS) {
            addElement(collection);
        }
    }

    // ignores case
    private boolean isSpecialCollection(String name) {
        for (String collection : SPECIAL_COLLECTIONS) {
            if (collection.equalsIgnoreCase(name)) return true;
        }
        return false;
    }
}
