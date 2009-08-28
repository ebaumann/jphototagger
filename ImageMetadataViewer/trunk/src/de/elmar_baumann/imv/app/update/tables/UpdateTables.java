package de.elmar_baumann.imv.app.update.tables;

import de.elmar_baumann.imv.database.Database;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Updates tables from previous application versions
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-23
 */
public final class UpdateTables extends Database {

    public static final UpdateTables INSTANCE = new UpdateTables();

    private UpdateTables() {
    }

    public void update(Connection connection) throws SQLException {
        new UpdateTablesDropColumns().update(connection);
        new UpdateTablesRenameColumns().update(connection);
        new UpdateTablesAddColumns().update(connection);
        new UpdateTablesXmpLastModified().update(connection);
        new UpdateTablesPrograms().update(connection);
        new UpdateTablesDeleteInvalidExif().update(connection);
        new UpdateTablesThumbnails().update(connection);
        UpdateTablesMessages.INSTANCE.getProgressDialog().setVisible(false);
    }
}
