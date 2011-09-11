package org.jphototagger.program.model;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;

import org.jphototagger.domain.repository.ImageCollectionsRepository;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.database.ConnectionPool;
import org.openide.util.Lookup;

/**
 * Elements are {@link String}s with all names of image collections retrieved
 * through {@link DatabaseImageCollections#getAllImageCollectionNames()}.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class ListModelImageCollections extends DefaultListModel {

    private static final long serialVersionUID = -929229489709109467L;
    private static final Map<String, Integer> SORT_ORDER_OF_SPECIAL_COLLECTION = new LinkedHashMap<String, Integer>();
    private final ImageCollectionsRepository repo = Lookup.getDefault().lookup(ImageCollectionsRepository.class);
    /**
     * Name of the image collection which contains the previous imported
     * image files
     */
    public static final String NAME_IMAGE_COLLECTION_PREV_IMPORT =
            Bundle.getString(ListModelImageCollections.class, "ListModelImageCollections.DisplayName.ItemImageCollections.LastImport");
    /**
     * Name of the image collection which contains picked images
     */
    public static final String NAME_IMAGE_COLLECTION_PICKED =
            Bundle.getString(ListModelImageCollections.class, "ListModelImageCollections.DisplayName.ItemImageCollections.Picked");
    /**
     * Name of the image collection which contains rejected images
     */
    public static final String NAME_IMAGE_COLLECTION_REJECTED =
            Bundle.getString(ListModelImageCollections.class, "ListModelImageCollections.DisplayName.ItemImageCollections.Rejected");

    static {

        // Order of appearance
        SORT_ORDER_OF_SPECIAL_COLLECTION.put(NAME_IMAGE_COLLECTION_PREV_IMPORT, 0);
        SORT_ORDER_OF_SPECIAL_COLLECTION.put(NAME_IMAGE_COLLECTION_PICKED, 1);
        SORT_ORDER_OF_SPECIAL_COLLECTION.put(NAME_IMAGE_COLLECTION_REJECTED, 2);
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

        String errorMessage = Bundle.getString(ListModelImageCollections.class, "ListModelImageCollections.Error.RenameSpecialCollection");
        if (!checkIsNotSpecialCollection(toName, errorMessage)) {
            return;
        }

        int index = indexOf(fromName);

        if (index >= 0) {
            remove(index);
            insertElementAt(toName, index);
        }
    }

    private void addElements() {
        if (!ConnectionPool.INSTANCE.isInit()) {
            return;
        }

        List<String> collections = repo.getAllImageCollectionNames();

        addSpecialCollections();

        for (String collection : collections) {
            if (!isSpecialCollection(collection)) {
                addElement(collection);
            }
        }
    }

    private void addSpecialCollections() {
        for (String collection : SORT_ORDER_OF_SPECIAL_COLLECTION.keySet()) {
            addElement(collection);
        }
    }

    public static int getSpecialCollectionCount() {
        return SORT_ORDER_OF_SPECIAL_COLLECTION.size();
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

        for (String collection : SORT_ORDER_OF_SPECIAL_COLLECTION.keySet()) {
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
     * @param  collectionName
     * @param  errorMessage
     * @return                true if everything is ok: the image collection is
     *                        <em>not</em> a special collection
     */
    public static boolean checkIsNotSpecialCollection(String collectionName, String errorMessage) {
        if (collectionName == null) {
            throw new NullPointerException("collectionName == null");
        }

        if (errorMessage == null) {
            throw new NullPointerException("propertyKey == null");
        }

        if (isSpecialCollection(collectionName)) {
            MessageDisplayer.warning(null, errorMessage);

            return false;
        }

        return true;
    }

    public Comparator<String> createAscendingSortComparator() {
        return new ImageCollectionSortAscendingComparator();
    }

    private final class ImageCollectionSortAscendingComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            boolean o1IsSpecialCollection = isSpecialCollection(o1);
            boolean o2IsSpecialCollection = isSpecialCollection(o2);
            boolean noneIsSpecialCollection = !o1IsSpecialCollection && !o2IsSpecialCollection;

            if (noneIsSpecialCollection) {
                return o1.compareToIgnoreCase(o2);
            }

            if (o1IsSpecialCollection && !o2IsSpecialCollection) {
                return -1;
            }

            if (!o1IsSpecialCollection && o2IsSpecialCollection) {
                return 1;
            }

            int sortOrderO1 = SORT_ORDER_OF_SPECIAL_COLLECTION.get(o1);
            int sortOrderO2 = SORT_ORDER_OF_SPECIAL_COLLECTION.get(o2);

            return sortOrderO1 > sortOrderO2
                    ? 1
                    : -1;
        }

        private boolean isSpecialCollection(String o) {
            return SORT_ORDER_OF_SPECIAL_COLLECTION.containsKey(o);
        }
    }
}
