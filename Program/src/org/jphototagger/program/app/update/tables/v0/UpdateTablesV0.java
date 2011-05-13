package org.jphototagger.program.app.update.tables.v0;

import org.jphototagger.program.app.update.tables.UpdateTablesFactory.Updater;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Updates tables from previous application versions
 *
 * @author Elmar Baumann
 */
public final class UpdateTablesV0 implements Updater {
    @Override
    public void updatePreCreation(Connection con) throws SQLException {

        // Never change the order!
        new UpdateTablesMakePlural().update(con);
    }

    @Override
    public void updatePostCreation(Connection con) throws SQLException {

        // Never change the order!
        new UpdateTablesDropTables().update(con);
        new UpdateTablesDropColumns().update(con);
        new UpdateTablesRenameColumns().update(con);
        new UpdateTablesInsertColumns().update(con);
        new UpdateTablesIndexes().update(con);
        new UpdateTablesPrimaryKeys().update(con);
        new UpdateTablesXmpLastModified().update(con);
        new UpdateTablesPrograms().update(con);
        new UpdateTablesDeleteInvalidExif().update(con);
        new UpdateTablesThumbnails().update(con);
        new UpdateTablesDropCategories().update(con);
        new UpdateTablesXmpDcSubjects().update(con);
        new UpdateTablesMake1n().update(con);
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }
}
