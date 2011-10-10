package org.jphototagger.program.module.xmp.comparators;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.thumbnails.FileSortComparator;
import org.jphototagger.domain.thumbnails.FileSortComparators;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileSortComparators.class)
public final class XmpFileSortComparators implements FileSortComparators {

    private final FileSortComparator ratingSortComparator = new FileSortComparator() {

        @Override
        public Comparator<File> getAscendingSortComparator() {
            return new XmpRatingAscendingComparator();
        }

        @Override
        public String getAscendingSortComparatorDisplayName() {
            return Bundle.getString(XmpFileSortComparators.class, "XmpFileSortComparators.RatingAscending.DisplayName");
        }

        @Override
        public Comparator<File> getDescendingSortComparator() {
            return new XmpRatingDescendingComparator();
        }

        @Override
        public String getDescendingSortComparatorDisplayName() {
            return Bundle.getString(XmpFileSortComparators.class, "XmpFileSortComparators.RatingDescending.DisplayName");
        }

        @Override
        public int getPosition() {
            return 1000;
        }
    };

    private final FileSortComparator locationSortComparator = new FileSortComparator() {

        @Override
        public Comparator<File> getAscendingSortComparator() {
            return new XmpIptcLocationAscendingComparator();
        }

        @Override
        public String getAscendingSortComparatorDisplayName() {
            return Bundle.getString(XmpFileSortComparators.class, "XmpFileSortComparators.LocationAscending.DisplayName");
        }

        @Override
        public Comparator<File> getDescendingSortComparator() {
            return new XmpIptcLocationDescendingComparator();
        }

        @Override
        public String getDescendingSortComparatorDisplayName() {
            return Bundle.getString(XmpFileSortComparators.class, "XmpFileSortComparators.LocationDescending.DisplayName");
        }

        @Override
        public int getPosition() {
            return 1010;
        }
    };

    private final FileSortComparator xmpExifDateTimeSortComparator = new FileSortComparator() {

        @Override
        public Comparator<File> getAscendingSortComparator() {
            return new XmpExifDateTimeAscendingComparator();
        }

        @Override
        public String getAscendingSortComparatorDisplayName() {
            return Bundle.getString(XmpFileSortComparators.class, "XmpFileSortComparators.XmpExifDateTimeAscending.DisplayName");
        }

        @Override
        public Comparator<File> getDescendingSortComparator() {
            return new XmpExifDateTimeDescendingComparator();
        }

        @Override
        public String getDescendingSortComparatorDisplayName() {
            return Bundle.getString(XmpFileSortComparators.class, "XmpFileSortComparators.XmpExifDateTimeDescending.DisplayName");
        }

        @Override
        public int getPosition() {
            return 1020;
        }
    };

    @Override
    public Collection<FileSortComparator> getFileSortComparators() {
        return Arrays.asList(
                ratingSortComparator, // Position: 1000
                locationSortComparator, // Position: 1010
                xmpExifDateTimeSortComparator // Position: 1020
                );
    }
}
