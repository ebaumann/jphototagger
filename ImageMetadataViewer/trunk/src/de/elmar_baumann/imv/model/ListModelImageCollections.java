package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.app.AppTexts;
import de.elmar_baumann.imv.comparator.ComparatorStringAscending;
import de.elmar_baumann.imv.database.DatabaseImageCollections;
import de.elmar_baumann.lib.componentutil.ListUtil;
import java.util.ArrayList;
import java.util.Enumeration;
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

    public void fireContentsChanged(int index) {
        if (index >= 0 && index < size()) {
            fireContentsChanged(this, index, index);
        }
    }

    public void rename(String oldName, String newName) {
        int index = indexOf(oldName);
        if (index >= 0) {
            remove(index);
            ListUtil.insertSorted(
                    this, newName, ComparatorStringAscending.IGNORE_CASE,
                    getSortStartIndex());
        }
    }

    public void addPrevImportItem() {
        if (!contains(AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PREV_IMPORT)) {
            List<Object> elements = new ArrayList<Object>(getSize());
            for (Enumeration e = elements(); e.hasMoreElements();) {
                elements.add(e.nextElement());
            }
            clear();
            addElement(AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PREV_IMPORT);
            for (Object element : elements) {
                addElement(element);
            }
        }
    }

    private int getSortStartIndex() {
        return contains(AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PREV_IMPORT)
               ? 1
               : 0;
    }

    private void addElements() {
        DatabaseImageCollections db = DatabaseImageCollections.INSTANCE;
        List<String> collections = db.getImageCollectionNames();
        if (DatabaseImageCollections.INSTANCE.existsImageCollection(
                AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PREV_IMPORT)) {
            addElement(AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PREV_IMPORT);
        }
        for (String collection : collections) {
            if (!collection.equalsIgnoreCase(
                    AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PREV_IMPORT)) {
                addElement(collection);
            }
        }
    }
}
