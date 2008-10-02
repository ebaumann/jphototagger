package de.elmar_baumann.lib.util;

import de.elmar_baumann.lib.io.DirectoryTreeModelFile;
import de.elmar_baumann.lib.io.DirectoryTreeModelFile.SortType;
import java.util.Comparator;

/**
 * Vergleicht Objekte des Typs {@link de.elmar_baumann.lib.io.DirectoryTreeModelFile}.
 * 
 * @param <DirectoryTreeModelFile> 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/23
 */
public class DirectoryTreeModelFileComparator<DirectoryTreeModelFile> implements
    Comparator<DirectoryTreeModelFile> {

    private SortType sortType = SortType.none;

    public DirectoryTreeModelFileComparator(SortType sortType) {
        this.sortType = sortType;
    }

    @Override
    public int compare(DirectoryTreeModelFile o1, DirectoryTreeModelFile o2) {
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
