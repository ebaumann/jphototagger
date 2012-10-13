package org.jphototagger.program.module.thumbnails;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.thumbnails.FileSortComparator;
import org.jphototagger.domain.thumbnails.FileSortComparators;
import org.jphototagger.lib.api.LayerUtil;
import org.jphototagger.lib.api.PositionProviderAscendingComparator;
import org.jphototagger.lib.comparator.FileSort;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * Elements are {@link FileSorter}s.
 * @author Elmar Baumann
 */
public final class ThumbnailsSortComboBoxModel extends DefaultComboBoxModel<Object> {

    private static final long serialVersionUID = 1L;
    static final String PERSISTED_SELECTED_ITEM_KEY = "ThumbnailsSortComboBoxModel.SelIndex";

    public static class FileSorter {

        private final Comparator<File> comparator;
        private final String displayName;

        public FileSorter(Comparator<File> comparator, String displayName) {
            if (comparator == null) {
                throw new NullPointerException("comparator == null");
            }

            if (displayName == null) {
                throw new NullPointerException("displayName == null");
            }

            this.comparator = comparator;
            this.displayName = displayName;
        }

        public Comparator<File> getComparator() {
            return comparator;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public ThumbnailsSortComboBoxModel() {
        addFileSorters();
    }

    private void addFileSorters() {
        addElement(new FileSorter(FileSort.PATHS_ASCENDING.getComparator(),
                Bundle.getString(ThumbnailsSortComboBoxModel.class, "ThumbnailsSortComboBoxModel.DisplayName.ComparatorFilePathAscending")));
        addElement(new FileSorter(FileSort.PATHS_DESCENDING.getComparator(),
                Bundle.getString(ThumbnailsSortComboBoxModel.class, "ThumbnailsSortComboBoxModel.DisplayName.ComparatorFilePathDescending")));
        addElement(new FileSorter(FileSort.NAMES_ASCENDING.getComparator(),
                Bundle.getString(ThumbnailsSortComboBoxModel.class, "ThumbnailsSortComboBoxModel.DisplayName.ComparatorFileNameAscending")));
        addElement(new FileSorter(FileSort.NAMES_DESCENDING.getComparator(),
                Bundle.getString(ThumbnailsSortComboBoxModel.class, "ThumbnailsSortComboBoxModel.DisplayName.ComparatorFileNameDescending")));
        addElement(new FileSorter(FileSort.LAST_MODIFIED_ASCENDING.getComparator(),
                Bundle.getString(ThumbnailsSortComboBoxModel.class, "ThumbnailsSortComboBoxModel.DisplayName.ComparatorFileLastModifiedAscending")));
        addElement(new FileSorter(FileSort.LAST_MODIFIED_DESCENDING.getComparator(),
                Bundle.getString(ThumbnailsSortComboBoxModel.class, "ThumbnailsSortComboBoxModel.DisplayName.ComparatorFileLastModifiedDescending")));
        addElement(new FileSorter(FileSort.TYPES_ASCENDING.getComparator(),
                Bundle.getString(ThumbnailsSortComboBoxModel.class, "ThumbnailsSortComboBoxModel.DisplayName.ComparatorFileTypeAscending")));
        addElement(new FileSorter(FileSort.TYPES_DESCENDING.getComparator(),
                Bundle.getString(ThumbnailsSortComboBoxModel.class, "ThumbnailsSortComboBoxModel.DisplayName.ComparatorFileTypeDescending")));

        Collection<? extends FileSortComparators> sortComparators = Lookup.getDefault().lookupAll(FileSortComparators.class);
        List<FileSortComparator> sortedSortComparators = new ArrayList<>();

        for (FileSortComparators fscs : sortComparators) {
            sortedSortComparators.addAll(fscs.getFileSortComparators());
        }

        Collections.sort(sortedSortComparators, PositionProviderAscendingComparator.INSTANCE);
        LayerUtil.logWarningIfNotUniquePositions(sortedSortComparators);

        for (FileSortComparator fileSortComparator : sortedSortComparators) {
            addElement(new FileSorter(fileSortComparator.getAscendingSortComparator(), fileSortComparator.getAscendingSortComparatorDisplayName()));
            addElement(new FileSorter(fileSortComparator.getDescendingSortComparator(), fileSortComparator.getDescendingSortComparatorDisplayName()));
        }

        addElement(new FileSorter(FileSort.NO_SORT.getComparator(),
                Bundle.getString(ThumbnailsSortComboBoxModel.class, "ThumbnailsSortComboBoxModel.DisplayName.ComparatorNoSort")));
    }

    void selectPersistedItem() {
        Preferences references = Lookup.getDefault().lookup(Preferences.class);

        if (references.containsKey(PERSISTED_SELECTED_ITEM_KEY)) {
            int index = references.getInt(PERSISTED_SELECTED_ITEM_KEY);

            if ((index >= 0) && (index < getSize())) {
                setSelectedItem(getElementAt(index));
            }
        }
    }
}
