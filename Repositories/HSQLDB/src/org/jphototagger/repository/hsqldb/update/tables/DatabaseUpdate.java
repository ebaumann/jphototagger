package org.jphototagger.repository.hsqldb.update.tables;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.openide.util.Lookup;

import org.jphototagger.lib.util.Version;
import org.jphototagger.repository.hsqldb.AppDatabase;

/**
 * @author Elmar Baumann
 */
public final class DatabaseUpdate {

    private final List<DatabaseUpdateTask> requiredUpdateTasks = new LinkedList<DatabaseUpdateTask>();

    public DatabaseUpdate() {
        lookupUpdaters();
        Collections.sort(requiredUpdateTasks, new DatabaseUpdateTaskComparator()); // Older versions first!
    }

    private synchronized void lookupUpdaters() {
        Version databaseVersion = AppDatabase.getPersistedDatabaseVersion();
        Collection<? extends DatabaseUpdateTask> allUpdateTasks = Lookup.getDefault().lookupAll(DatabaseUpdateTask.class);

        for (DatabaseUpdateTask updateTask : allUpdateTasks) {
            Version updatesToDatabaseVersion = updateTask.getUpdatesToDatabaseVersion();

            esnureCanUpdateDatabaseVersion(updateTask, databaseVersion);

            boolean databaseHasOlderVersionThanTask = databaseVersion.compareTo(updatesToDatabaseVersion) < 0;

            if (databaseHasOlderVersionThanTask) {
                requiredUpdateTasks.add(updateTask);
            }
        }
    }

    public synchronized void preCreateTables() throws SQLException {
        for (DatabaseUpdateTask updater : requiredUpdateTasks) {
            updater.preCreateTables();
        }
    }

    public synchronized void postCreateTables() throws SQLException {
        for (DatabaseUpdateTask updater : requiredUpdateTasks) {
            updater.postCreateTables();
        }
    }

    private void esnureCanUpdateDatabaseVersion(DatabaseUpdateTask updateTask, Version databaseVersion) {
        if (!updateTask.canUpdateDatabaseVersion(databaseVersion)) {
            throw new IllegalStateException("Database update task '" + updateTask.toString()
                    + "' can't update database version " + databaseVersion.toString3());
        }
    }
}
