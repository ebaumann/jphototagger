package org.jphototagger.program.model;

import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.comparator.ComparatorStringAscending;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseImageCollections;
import org.jphototagger.program.resource.JptBundle;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

/**
 * Elements are {@link String}s with all names of image collections retrieved
 * through {@link DatabaseImageCollections#getAll()}.
 *
 * @author Elmar Baumann, Tobias Stening
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

    public void rename(final String fromName, final String toName) {
        if (fromName == null) {
            throw new NullPointerException("fromName == null");
        }

        if (toName == null) {
            throw new NullPointerException("toName == null");
        }

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
        if (!ConnectionPool.INSTANCE.isInit()) {
            return;
        }

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
     * @param  collectionName name of the collection (the name is the
     *                        identifier)
     * @return                true if that name is the name of a special image
     *                        collection
     */
    public static boolean isSpecialCollection(String collectionName) {
        if (collectionName == null) {
            throw new NullPointerException("collectionName == null");
        }

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
        if (collectionName == null) {
            throw new NullPointerException("collectionName == null");
        }

        if (propertyKey == null) {
            throw new NullPointerException("propertyKey == null");
        }

        if (isSpecialCollection(collectionName)) {
            MessageDisplayer.warning(null, propertyKey, collectionName);

            return false;
        }

        return true;
    }
}
