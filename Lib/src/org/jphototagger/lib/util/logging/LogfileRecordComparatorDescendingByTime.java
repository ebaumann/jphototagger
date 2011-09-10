package org.jphototagger.lib.util.logging;

import java.util.Comparator;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class LogfileRecordComparatorDescendingByTime implements Comparator<LogfileRecord> {

    public static final LogfileRecordComparatorDescendingByTime INSTANCE = new LogfileRecordComparatorDescendingByTime();

    @Override
    public int compare(LogfileRecord record1, LogfileRecord record2) {
        Long millis1 = record1.getMillis();
        Long millis2 = record2.getMillis();

        return millis1 > millis2
                ? -1
                : millis1 < millis2
                ? 1
                : 0;
    }
}
