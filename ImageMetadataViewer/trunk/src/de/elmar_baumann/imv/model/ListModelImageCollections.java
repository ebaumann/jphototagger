package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.app.AppTexts;
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
        addElements();
    }

    private void addElements() {
        DatabaseImageCollections db = DatabaseImageCollections.INSTANCE;
        List<String> collections = db.getImageCollectionNames();
        if (DatabaseImageCollections.INSTANCE.existsImageCollection(
                AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_LAST_IMPORT)) {
            addElement(AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_LAST_IMPORT);
        }
        for (String collection : collections) {
            if (!collection.equalsIgnoreCase(
                    AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_LAST_IMPORT)) {
                addElement(collection);
            }
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
            ListUtil.insertSorted(this, newName,
                    ComparatorStringAscending.IGNORE_CASE);
        }
    }
}
