package org.jphototagger.domain.imagecollections;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ImageCollectionSortAscendingComparator implements Comparator<String> {

    public static final Map<String, Integer> SORT_ORDER_OF_SPECIAL_COLLECTION = new LinkedHashMap<String, Integer>();

    static {

        // Order of appearance
        SORT_ORDER_OF_SPECIAL_COLLECTION.put(ImageCollection.PREVIOUS_IMPORT_NAME, 0);
        SORT_ORDER_OF_SPECIAL_COLLECTION.put(ImageCollection.PICKED_NAME, 1);
        SORT_ORDER_OF_SPECIAL_COLLECTION.put(ImageCollection.REJECTED_NAME, 2);
    }

    public ImageCollectionSortAscendingComparator() {
    }

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
        return sortOrderO1 > sortOrderO2 ? 1 : -1;
    }

    private boolean isSpecialCollection(String o) {
        return SORT_ORDER_OF_SPECIAL_COLLECTION.containsKey(o);
    }
}
