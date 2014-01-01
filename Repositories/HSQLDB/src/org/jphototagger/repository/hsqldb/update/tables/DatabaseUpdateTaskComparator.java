package org.jphototagger.repository.hsqldb.update.tables;

import java.util.Comparator;
import org.jphototagger.lib.util.Version;

/**
 * @author Elmar Baumann
 */
public final class DatabaseUpdateTaskComparator implements Comparator<DatabaseUpdateTask> {

    @Override
    public int compare(DatabaseUpdateTask task1, DatabaseUpdateTask task2) {
        Version dbVersionTask1 = task1.getUpdatesToDatabaseVersion();
        Version dbVersionTask2 = task2.getUpdatesToDatabaseVersion();

        return dbVersionTask1.compareTo(dbVersionTask2);
    }
}
