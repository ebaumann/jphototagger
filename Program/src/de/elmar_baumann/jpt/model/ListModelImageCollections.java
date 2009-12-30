/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.model;

import de.elmar_baumann.jpt.app.AppTexts;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.comparator.ComparatorStringAscending;
import de.elmar_baumann.jpt.database.DatabaseImageCollections;
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

    static {
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
        if (!checkIsNotSpecialCollection(newName,
                "ListModelImageCollections.Error.RenameSpecialCollection"))
            return;
        int index = indexOf(oldName);
        if (index >= 0) {
            remove(index);
            ListUtil.insertSorted(this, newName,
                    ComparatorStringAscending.INSTANCE,
                    getSpecialCollectionCount(),
                    getSize() - 1);
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

    public static int getSpecialCollectionCount() {
        return SPECIAL_COLLECTIONS.size();
    }

    /**
     * Returns wheter a collection is a special image collection, e.g. for
     * picked or rejected images.
     *
     * @param  collectionName name of the collection (the name is the identifier)
     * @return                true if that name is the name of a special image
     *                        collection
     */
    public static boolean isSpecialCollection(String collectionName) {
        for (String collection : SPECIAL_COLLECTIONS) {
            if (collection.equalsIgnoreCase(collectionName)) return true;
        }
        return false;
    }

    /**
     * Checks whether an image collection isn't a special collection and when it
     * is, displays a warning message with the name of the image collection as
     * parameter.
     * 
     * @param  collectionName name of the image collection
     * @param  propertyKey    property key to load the warning message. If it
     *                        has a parameter zero, that will be replaced with
     *                        the name of the image collection
     * @return                true if everything is ok: the image collection is
     *                        <em>not</em> a special collection
     */
    public static boolean checkIsNotSpecialCollection(
            String collectionName, String propertyKey) {

        if (isSpecialCollection(collectionName)) {
            MessageDisplayer.warning(
                    null,
                    propertyKey,
                    collectionName);
            return false;
        }
        return true;
    }
}
