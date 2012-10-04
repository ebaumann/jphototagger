package org.jphototagger.exifmodule.comparators;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import org.jphototagger.domain.thumbnails.FileSortComparator;
import org.jphototagger.domain.thumbnails.FileSortComparators;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileSortComparators.class)
public final class ExifFileSortComparators implements FileSortComparators {

    private final FileSortComparator dateOriginalFileSortComparator = new FileSortComparator() {

        @Override
        public Comparator<File> getAscendingSortComparator() {
            return new ExifDateTimeOriginalAscendingComparator();
        }

        @Override
        public String getAscendingSortComparatorDisplayName() {
            return Bundle.getString(ExifFileSortComparators.class, "ExifFileSortComparators.DateOriginalAscending.DisplayName");
        }

        @Override
        public Comparator<File> getDescendingSortComparator() {
            return new ExifDateTimeOriginalDescendingComparator();
        }

        @Override
        public String getDescendingSortComparatorDisplayName() {
            return Bundle.getString(ExifFileSortComparators.class, "ExifFileSortComparators.DateOriginalDescending.DisplayName");
        }

        @Override
        public int getPosition() {
            return 10;
        }
    };

    private final FileSortComparator timestampOriginalFileSortComparator = new FileSortComparator() {

        @Override
        public Comparator<File> getAscendingSortComparator() {
            return new ExifTimestampOriginalAscendingComparator();
        }

        @Override
        public String getAscendingSortComparatorDisplayName() {
            return Bundle.getString(ExifFileSortComparators.class, "ExifFileSortComparators.TimestampOriginalAscending.DisplayName");
        }

        @Override
        public Comparator<File> getDescendingSortComparator() {
            return new ExifTimestampOriginalDescendingComparator();
        }

        @Override
        public String getDescendingSortComparatorDisplayName() {
            return Bundle.getString(ExifFileSortComparators.class, "ExifFileSortComparators.TimestampOriginalDescending.DisplayName");
        }

        @Override
        public int getPosition() {
            return 20;
        }
    };

    private final FileSortComparator focalLengthSortComparator = new FileSortComparator() {

        @Override
        public Comparator<File> getAscendingSortComparator() {
            return new ExifFocalLengthAscendingComparator();
        }

        @Override
        public String getAscendingSortComparatorDisplayName() {
            return Bundle.getString(ExifFileSortComparators.class, "ExifFileSortComparators.FocalLengthAscending.DisplayName");
        }

        @Override
        public Comparator<File> getDescendingSortComparator() {
            return new ExifFocalLengthDescendingComparator();
        }

        @Override
        public String getDescendingSortComparatorDisplayName() {
            return Bundle.getString(ExifFileSortComparators.class, "ExifFileSortComparators.FocalLengthDescending.DisplayName");
        }

        @Override
        public int getPosition() {
            return 30;
        }
    };

    private final FileSortComparator isoSpeedRatingSortComparator = new FileSortComparator() {

        @Override
        public Comparator<File> getAscendingSortComparator() {
            return new ExifIsoSpeedRatingAscendingComparator();
        }

        @Override
        public String getAscendingSortComparatorDisplayName() {
            return Bundle.getString(ExifFileSortComparators.class, "ExifFileSortComparators.IsoSpeedRatingAscending.DisplayName");
        }

        @Override
        public Comparator<File> getDescendingSortComparator() {
            return new ExifIsoSpeedRatingDescendingComparator();
        }

        @Override
        public String getDescendingSortComparatorDisplayName() {
            return Bundle.getString(ExifFileSortComparators.class, "ExifFileSortComparators.IsoSpeedRatingDescending.DisplayName");
        }

        @Override
        public int getPosition() {
            return 40;
        }
    };

    private final FileSortComparator recordingEquipmentSortComparator = new FileSortComparator() {

        @Override
        public Comparator<File> getAscendingSortComparator() {
            return new ExifRecordingEquipmentAscendingComparator();
        }

        @Override
        public String getAscendingSortComparatorDisplayName() {
            return Bundle.getString(ExifFileSortComparators.class, "ExifFileSortComparators.RecordingEquipmentAscending.DisplayName");
        }

        @Override
        public Comparator<File> getDescendingSortComparator() {
            return new ExifRecordingEquipmentDescendingComparator();
        }

        @Override
        public String getDescendingSortComparatorDisplayName() {
            return Bundle.getString(ExifFileSortComparators.class, "ExifFileSortComparators.RecordingEquipmentDescending.DisplayName");
        }

        @Override
        public int getPosition() {
            return 50;
        }
    };

    @Override
    public Collection<FileSortComparator> getFileSortComparators() {
        return Arrays.asList(
                dateOriginalFileSortComparator, // Position 10
                timestampOriginalFileSortComparator, // Position 20
                focalLengthSortComparator, // Position 30
                isoSpeedRatingSortComparator, // Position 40
                recordingEquipmentSortComparator // Position 50
                );
    }
}
