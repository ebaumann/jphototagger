package org.jphototagger.repository.hsqldb.update.tables.v0.obsolete;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.repository.hsqldb.DatabaseMetadata;

/**
 * @author Elmar Baumann
 */
final class UpdateTablesDropCategories {

    private static final Logger LOGGER = Logger.getLogger(UpdateTablesDropCategories.class.getName());

    void update(Connection con) throws SQLException {
        LOGGER.log(Level.INFO, "Dropping categories");

        if (DatabaseMetadata.INSTANCE.existsTable(con, "xmp_photoshop_supplementalcategories")
                && !categoriesAlreadyDropped(con)) {
            MessageDisplayer.error(null, Bundle.getString(UpdateTablesDropCategories.class, "UpdateTablesDropCategories.Error.NotLongerSupported"));
            throw new RuntimeException("Dropping categories is not supported");
        }
    }

    private boolean categoriesAlreadyDropped(Connection con) throws SQLException {
        return !DatabaseMetadata.INSTANCE.existsColumn(con, "xmp", "photoshop_category")
                || !DatabaseMetadata.INSTANCE.existsColumn(con, "xmp_photoshop_supplementalcategories",
                "supplementalcategory");
    }
}
