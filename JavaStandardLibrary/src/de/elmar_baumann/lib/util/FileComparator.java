package de.elmar_baumann.lib.util;

import de.elmar_baumann.lib.types.SortType;
import java.util.Comparator;

/**
 * Vergleicht Objekte des Typs File.
 * 
 * @param <File> 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/23
 */
public final class FileComparator<File> implements
    Comparator<File> {

    private SortType sortType = SortType.none;

    public FileComparator(SortType sortType) {
        this.sortType = sortType;
    }

    @Override
    public int compare(File o1, File o2) {
        if (sortType.equals(SortType.none)) {
            return 0;
        }
        int compare = sortType.equals(SortType.ascending) || sortType.equals(
            SortType.descending) ? o1.toString().compareTo(o2.toString()) : o1.toString().compareToIgnoreCase(o2.toString());
        if (sortType.equals(SortType.descending) || sortType.equals(
            SortType.descendingNoCase)) {
            compare *= -1;
        }
        return compare;
    }
}
