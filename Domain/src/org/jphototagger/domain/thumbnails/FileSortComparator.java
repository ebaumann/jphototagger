package org.jphototagger.domain.thumbnails;

import java.io.File;
import java.util.Comparator;

import org.jphototagger.api.collections.PositionProvider;

/**
 * @author Elmar Baumann
 */
public interface FileSortComparator extends PositionProvider {

    Comparator<File> getAscendingSortComparator();

    String getAscendingSortComparatorDisplayName();

    Comparator<File> getDescendingSortComparator();

    String getDescendingSortComparatorDisplayName();
}
