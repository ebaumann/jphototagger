/*
 * @(#)ListModelImageCollections.java    Created on 2008-10-05
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.model;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.comparator.ComparatorStringAscending;
import org.jphototagger.program.database.DatabaseImageCollections;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.lib.componentutil.ListUtil;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

/**
 * Elements are {@link String}s with all names of image collections retrieved
 * through {@link DatabaseImageCollections#getAll()}.
 *
 * @author  Elmar Baumann, Tobias Stening
 */
public final class ListModelImageCollections extends DefaultListModel {
    private static final List<String> SPECIAL_COLLECTIONS =
        new ArrayList<String>();
    private static final long serialVersionUID = -929229489709109467L;

    /**
     * Name of the image collection which contains the previous imported
     * image files
     */
    public static final String NAME_IMAGE_COLLECTION_PREV_IMPORT =
        JptBundle.INSTANCE.getString(
            "ListModelImageCollections.DisplayName.ItemImageCollections.LastImport");

    /**
     * Name of the image collection which contains picked images
     */
    public static final String NAME_IMAGE_COLLECTION_PICKED =
        JptBundle.INSTANCE.getString(
            "ListModelImageCollections.DisplayName.ItemImageCollections.Picked");

    /**
     * Name of the image collection which contains rejected images
     */
    public static final String NAME_IMAGE_COLLECTION_REJECTED =
        JptBundle.INSTANCE.getString(
            "ListModelImageCollections.DisplayName.ItemImageCollections.Rejected");

    static {

        // Order of appearance
        SPECIAL_COLLECTIONS.add(NAME_IMAGE_COLLECTION_PREV_IMPORT);
        SPECIAL_COLLECTIONS.add(NAME_IMAGE_COLLECTION_PICKED);
        SPECIAL_COLLECTIONS.add(NAME_IMAGE_COLLECTION_REJECTED);
    }

    public ListModelImageCollections() {
        addElements();
    }

    public void fireContentsChanged(int index) {
        if ((index >= 0) && (index < size())) {
            fireContentsChanged(this, index, index);
        }
    }

    public void rename(String fromName, String toName) {
        if (!checkIsNotSpecialCollection(
                toName,
                "ListModelImageCollections.Error.RenameSpecialCollection")) {
            return;
        }

        int index = indexOf(fromName);

        if (index >= 0) {
            remove(index);
            ListUtil.insertSorted(this, toName,
                                  ComparatorStringAscending.INSTANCE,
                                  getSpecialCollectionCount(), getSize() - 1);
        }
    }

    private void addElements() {
        DatabaseImageCollections db          =
            DatabaseImageCollections.INSTANCE;
        List<String>             collections = db.getAll();

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
            if (collection.equalsIgnoreCase(collectionName)) {
                return true;
            }
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
    public static boolean checkIsNotSpecialCollection(String collectionName,
            String propertyKey) {
        if (isSpecialCollection(collectionName)) {
            MessageDisplayer.warning(null, propertyKey, collectionName);

            return false;
        }

        return true;
    }
}
