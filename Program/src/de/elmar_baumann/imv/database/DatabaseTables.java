/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.app.update.tables.UpdateTables;
import de.elmar_baumann.imv.app.AppLock;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.LongMessageDialog;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-21
 */
public final class DatabaseTables extends Database {

    private static final List<String> TABLE_NAMES = new ArrayList<String>();
    public static final DatabaseTables INSTANCE = new DatabaseTables();

    static {
        // TODO PERMANENT: Update after adding a table
        TABLE_NAMES.add("files"); // NOI18N
        TABLE_NAMES.add("xmp"); // NOI18N
        TABLE_NAMES.add("xmp_dc_subjects"); // NOI18N
        TABLE_NAMES.add("xmp_photoshop_supplementalcategories"); // NOI18N
        TABLE_NAMES.add("exif"); // NOI18N
        TABLE_NAMES.add("collection_names"); // NOI18N
        TABLE_NAMES.add("collections"); // NOI18N
        TABLE_NAMES.add("saved_searches"); // NOI18N
        TABLE_NAMES.add("saved_searches_values"); // NOI18N
        TABLE_NAMES.add("saved_searches_panels"); // NOI18N
        TABLE_NAMES.add("autoscan_directories"); // NOI18N
        TABLE_NAMES.add("metadata_edit_templates"); // NOI18N
        TABLE_NAMES.add("favorite_directories"); // NOI18N
        TABLE_NAMES.add("file_exclude_pattern"); // NOI18N
        TABLE_NAMES.add("programs"); // NOI18N
        TABLE_NAMES.add("actions_after_db_insertion"); // NOI18N
        TABLE_NAMES.add("hierarchical_subjects"); // NOI18N
        TABLE_NAMES.add("application"); // NOI18N
    }

    public static List<String> getTableNames() {
        return TABLE_NAMES;
    }

    private DatabaseTables() {
    }

    /**
     * Creates the necessary tables if not exists. Exits the VM if not successfully.
     */
    public void createTables() {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            Statement stmt = connection.createStatement();
            createAppTable(connection, stmt); // prior to all other tables!
            createFilesTable(connection, stmt);
            createXmpTables(connection, stmt);
            createExifTables(connection, stmt);
            createCollectionsTables(connection, stmt);
            createSavedSearchesTables(connection, stmt);
            createAutoScanDirectoriesTable(connection, stmt);
            createMetadataEditTemplateTable(connection, stmt);
            createFavoriteDirectoriesTable(connection, stmt);
            createFileExcludePatternTable(connection, stmt);
            createProgramsTable(connection, stmt);
            createActionsAfterDbInsertionTable(connection, stmt);
            createHierarchicalSubjectsTable(connection, stmt);
            UpdateTables.INSTANCE.update(connection);
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseTables.class, ex);
            errorMessageSqlException(ex);
            AppLock.unlock();
            System.exit(0);
        } finally {
            free(connection);
        }
    }

    private void createFilesTable(Connection connection, Statement stmt) throws
            SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(connection, "files")) { // NOI18N
            stmt.execute("CREATE CACHED TABLE files " + // NOI18N
                    " (" + // NOI18N
                    "id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY" + // NOI18N
                    ", filename  VARCHAR_IGNORECASE(512) NOT NULL" + // NOI18N
                    ", lastmodified  BIGINT" + // NOI18N
                    ", thumbnail BINARY" + // NOI18N
                    ", xmp_lastmodified  BIGINT" + // NOI18N
                    ");"); // NOI18N
            stmt.execute("CREATE UNIQUE INDEX idx_files ON files (filename)"); // NOI18N
        }
    }

    private void createXmpTables(Connection connection, Statement stmt) throws
            SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(connection, "xmp")) { // NOI18N
            stmt.execute("CREATE CACHED TABLE xmp" + // NOI18N
                    " (" + // NOI18N
                    "id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY" + // NOI18N
                    ", id_files BIGINT NOT NULL" + // NOI18N
                    ", dc_creator VARCHAR(128)" + // NOI18N
                    ", dc_description VARCHAR_IGNORECASE(2000)" + // NOI18N
                    ", dc_rights VARCHAR_IGNORECASE(128)" + // NOI18N
                    ", dc_title VARCHAR_IGNORECASE(64)" + // NOI18N
                    ", iptc4xmpcore_countrycode VARCHAR_IGNORECASE(3)" + // NOI18N
                    ", iptc4xmpcore_location VARCHAR_IGNORECASE(64)" + // NOI18N
                    ", photoshop_authorsposition VARCHAR_IGNORECASE(32)" + // NOI18N
                    ", photoshop_captionwriter VARCHAR_IGNORECASE(32)" + // NOI18N
                    ", photoshop_category VARCHAR_IGNORECASE(128)" + // NOI18N
                    ", photoshop_city VARCHAR_IGNORECASE(32)" + // NOI18N
                    ", photoshop_country VARCHAR_IGNORECASE(64)" + // NOI18N
                    ", photoshop_credit VARCHAR_IGNORECASE(32)" + // NOI18N
                    ", photoshop_headline VARCHAR_IGNORECASE(256)" + // NOI18N
                    ", photoshop_instructions VARCHAR_IGNORECASE(256)" + // NOI18N
                    ", photoshop_source VARCHAR_IGNORECASE(32)" + // NOI18N
                    ", photoshop_state VARCHAR_IGNORECASE(32)" + // NOI18N
                    ", photoshop_transmissionReference VARCHAR_IGNORECASE(32)" + // NOI18N
                    ", rating BIGINT" + // NOI18N
                    ", FOREIGN KEY (id_files) REFERENCES files (id) ON DELETE CASCADE" + // NOI18N
                    ");"); // NOI18N
            stmt.execute(
                    "CREATE UNIQUE INDEX idx_xmp_id_files ON xmp (id_files)"); // NOI18N
            stmt.execute(
                    "CREATE INDEX idx_xmp_dc_description ON xmp (dc_description)"); // NOI18N
            stmt.execute("CREATE INDEX idx_xmp_dc_rights ON xmp (dc_rights)"); // NOI18N
            stmt.execute("CREATE INDEX idx_xmp_dc_title ON xmp (dc_title)"); // NOI18N
            stmt.execute("CREATE INDEX idx_xmp_iptc4xmpcore_countrycode" + // NOI18N
                    " ON xmp (iptc4xmpcore_countrycode)"); // NOI18N
            stmt.execute("CREATE INDEX idx_xmp_iptc4xmpcore_location" + // NOI18N
                    " ON xmp (iptc4xmpcore_location)"); // NOI18N
            stmt.execute("CREATE INDEX idx_xmp_photoshop_authorsposition" + // NOI18N
                    " ON xmp (photoshop_authorsposition)"); // NOI18N
            stmt.execute("CREATE INDEX idx_xmp_photoshop_captionwriter" + // NOI18N
                    " ON xmp (photoshop_captionwriter)"); // NOI18N
            stmt.execute("CREATE INDEX idx_xmp_photoshop_category" + // NOI18N
                    " ON xmp (photoshop_category)"); // NOI18N
            stmt.execute("CREATE INDEX idx_xmp_photoshop_city" + // NOI18N
                    " ON xmp (photoshop_city)"); // NOI18N
            stmt.execute("CREATE INDEX idx_xmp_photoshop_country" + // NOI18N
                    " ON xmp (photoshop_country)"); // NOI18N
            stmt.execute("CREATE INDEX idx_xmp_photoshop_credit" + // NOI18N
                    " ON xmp (photoshop_credit)"); // NOI18N
            stmt.execute("CREATE INDEX idx_xmp_photoshop_headline" + // NOI18N
                    " ON xmp (photoshop_headline)"); // NOI18N
            stmt.execute("CREATE INDEX idx_xmp_photoshop_instructions" + // NOI18N
                    " ON xmp (photoshop_instructions)"); // NOI18N
            stmt.execute("CREATE INDEX idx_xmp_photoshop_source" + // NOI18N
                    " ON xmp (photoshop_source)"); // NOI18N
            stmt.execute("CREATE INDEX idx_xmp_photoshop_state" + // NOI18N
                    " ON xmp (photoshop_state)"); // NOI18N
            stmt.execute("CREATE INDEX idx_xmp_photoshop_transmissionReference" + // NOI18N
                    " ON xmp (photoshop_transmissionReference)"); // NOI18N
        }
        if (!DatabaseMetadata.INSTANCE.existsTable(connection, "xmp_dc_subjects")) { // NOI18N
            stmt.execute("CREATE CACHED TABLE xmp_dc_subjects" + // NOI18N
                    " (" + // NOI18N
                    "id_xmp BIGINT NOT NULL" + // NOI18N
                    ", subject VARCHAR_IGNORECASE(64)" + // NOI18N
                    ", FOREIGN KEY (id_xmp) REFERENCES xmp (id) ON DELETE CASCADE" + // NOI18N
                    ");"); // NOI18N
            stmt.execute("CREATE INDEX idx_xmp_dc_subjects_id_xmp" + // NOI18N
                    " ON xmp_dc_subjects (id_xmp)"); // NOI18N
            stmt.execute("CREATE INDEX idx_xmp_dc_subjects_subject" + // NOI18N
                    " ON xmp_dc_subjects (subject)");  // NOI18N
        }
        if (!DatabaseMetadata.INSTANCE.existsTable(connection,
                "xmp_photoshop_supplementalcategories")) { // NOI18N
            stmt.execute("CREATE CACHED TABLE xmp_photoshop_supplementalcategories" + // NOI18N
                    " (" + // NOI18N
                    "id_xmp BIGINT NOT NULL" + // NOI18N
                    ", supplementalcategory VARCHAR_IGNORECASE(32)" + // NOI18N
                    ", FOREIGN KEY (id_xmp) REFERENCES xmp (id) ON DELETE CASCADE" + // NOI18N
                    ");"); // NOI18N
            stmt.execute("CREATE INDEX idx_xmp_photoshop_supplementalcategories_id_xmp" + // NOI18N
                    " ON xmp_photoshop_supplementalcategories (id_xmp)"); // NOI18N
            stmt.execute(
                    "CREATE INDEX idx_xmp_photoshop_supplementalcategories_supplementalcategory" + // NOI18N
                    " ON xmp_photoshop_supplementalcategories (supplementalcategory)"); // NOI18N
        }
    }

    private void createExifTables(Connection connection, Statement stmt) throws
            SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(connection, "exif")) { // NOI18N
            stmt.execute("CREATE CACHED TABLE exif" + // NOI18N
                    " (" + // NOI18N
                    "id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY" + // NOI18N
                    ", id_files BIGINT NOT NULL" + // NOI18N
                    ", exif_recording_equipment VARCHAR_IGNORECASE(125)" + // NOI18N
                    ", exif_date_time_original DATE" + // NOI18N
                    ", exif_focal_length REAL" + // NOI18N
                    ", exif_iso_speed_ratings SMALLINT" + // NOI18N
                    ", FOREIGN KEY (id_files) REFERENCES files (id) ON DELETE CASCADE" + // NOI18N
                    ");"); // NOI18N
            stmt.execute(
                    "CREATE UNIQUE INDEX idx_exif_id_files ON exif (id_files)"); // NOI18N
            stmt.execute(
                    "CREATE INDEX idx_exif_recording_equipment ON exif (exif_recording_equipment)"); // NOI18N
            stmt.execute(
                    "CREATE INDEX idx_exif_date_time_original ON exif (exif_date_time_original)"); // NOI18N
            stmt.execute(
                    "CREATE INDEX idx_exif_focal_length ON exif (exif_focal_length)"); // NOI18N
            stmt.execute(
                    "CREATE INDEX idx_exif_iso_speed_ratings ON exif (exif_iso_speed_ratings)"); // NOI18N
        }
    }

    private void createCollectionsTables(Connection connection, Statement stmt)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(connection,
                "collection_names")) { // NOI18N
            stmt.execute("CREATE CACHED TABLE collection_names" + // NOI18N
                    " (" + // NOI18N
                    "id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY" + // NOI18N
                    ", name VARCHAR_IGNORECASE(256)" + // NOI18N
                    ");"); // NOI18N
            stmt.execute(
                    "CREATE UNIQUE INDEX idx_collection_names_id ON collection_names (id)"); // NOI18N
            stmt.execute(
                    "CREATE INDEX idx_collection_names_name ON collection_names (name)");  // NOI18N
        }
        if (!DatabaseMetadata.INSTANCE.existsTable(connection, "collections")) { // NOI18N
            stmt.execute("CREATE CACHED TABLE collections" + // NOI18N
                    " (" + // NOI18N
                    "id_collectionnnames BIGINT" + // NOI18N
                    ", id_files BIGINT" + // NOI18N
                    ", sequence_number INTEGER" + // NOI18N
                    ", FOREIGN KEY (id_collectionnnames) REFERENCES collection_names (id) ON DELETE CASCADE" + // NOI18N
                    ", FOREIGN KEY (id_files) REFERENCES files (id) ON DELETE CASCADE" + // NOI18N
                    ");"); // NOI18N
            stmt.execute(
                    "CREATE INDEX idx_collections_id_collectionnnames ON collections (id_collectionnnames)"); // NOI18N
            stmt.execute(
                    "CREATE INDEX idx_collections_id_files ON collections (id_files)"); // NOI18N
            stmt.execute(
                    "CREATE INDEX idx_collections_sequence_number ON collections (sequence_number)"); // NOI18N
        }
    }

    private void createSavedSearchesTables(Connection connection, Statement stmt)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(connection, "saved_searches")) { // NOI18N
            stmt.execute("CREATE CACHED TABLE saved_searches" + // NOI18N
                    " (" + // NOI18N
                    "id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY" + // NOI18N
                    ", name VARCHAR_IGNORECASE(125)" + // NOI18N
                    ", sql_string BINARY" + // NOI18N
                    ", is_query BOOLEAN" + // NOI18N
                    ");"); // NOI18N
            stmt.execute(
                    "CREATE UNIQUE INDEX idx_saved_searches_id ON saved_searches (id)"); // NOI18N
            stmt.execute(
                    "CREATE UNIQUE INDEX idx_saved_searches_name ON saved_searches (name)");  // NOI18N
        }
        if (!DatabaseMetadata.INSTANCE.existsTable(connection,
                "saved_searches_values")) { // NOI18N
            stmt.execute("CREATE CACHED TABLE saved_searches_values" + // NOI18N
                    " (" + // NOI18N
                    "id_saved_searches BIGINT" + // NOI18N
                    ", value VARCHAR(256)" + // NOI18N
                    ", value_index INTEGER" + // NOI18N
                    ", FOREIGN KEY (id_saved_searches) REFERENCES saved_searches (id) ON DELETE CASCADE" + // NOI18N
                    ");"); // NOI18N
            stmt.execute(
                    "CREATE INDEX idx_saved_searches_id_saved_searches ON saved_searches_values (id_saved_searches)"); // NOI18N
            stmt.execute(
                    "CREATE INDEX idx_saved_searches_value_index ON saved_searches_values (value_index)"); // NOI18N
        }
        if (!DatabaseMetadata.INSTANCE.existsTable(connection,
                "saved_searches_panels")) { // NOI18N
            stmt.execute("CREATE CACHED TABLE saved_searches_panels" + // NOI18N
                    " (" + // NOI18N
                    "id_saved_searches BIGINT" + // NOI18N
                    ", panel_index INTEGER" + // NOI18N
                    ", bracket_left_1 BOOLEAN" + // NOI18N
                    ", operator_id INTEGER" + // NOI18N
                    ", bracket_left_2 BOOLEAN" + // NOI18N
                    ", column_id INTEGER" + // NOI18N
                    ", comparator_id INTEGER" + // NOI18N
                    ", value VARCHAR(256)" + // NOI18N
                    ", bracket_right BOOLEAN" + // NOI18N
                    ", FOREIGN KEY (id_saved_searches) REFERENCES saved_searches (id) ON DELETE CASCADE" + // NOI18N
                    ");"); // NOI18N
            stmt.execute(
                    "CREATE INDEX idx_saved_searches_panels_id_saved_searches ON saved_searches_panels (id_saved_searches)"); // NOI18N
            stmt.execute(
                    "CREATE INDEX idx_saved_searches_panels_panel_index ON saved_searches_panels (panel_index)"); // NOI18N
        }
    }

    private void createAutoScanDirectoriesTable(Connection connection,
            Statement stmt) throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(connection,
                "autoscan_directories")) { // NOI18N
            stmt.execute("CREATE CACHED TABLE autoscan_directories" + // NOI18N
                    " (" + // NOI18N
                    "directory VARCHAR_IGNORECASE(1024)" + // NOI18N
                    ");"); // NOI18N
            stmt.execute(
                    "CREATE UNIQUE INDEX idx_autoscan_directories_directory ON autoscan_directories (directory)"); // NOI18N
        }
    }

    private void createMetadataEditTemplateTable(Connection connection,
            Statement stmt) throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(connection,
                "metadata_edit_templates")) { // NOI18N
            stmt.execute("CREATE CACHED TABLE metadata_edit_templates" + // NOI18N
                    " (" + // NOI18N
                    "name VARCHAR_IGNORECASE(256)" + // NOI18N
                    ", dcSubjects BINARY" + // NOI18N
                    ", dcTitle BINARY" + // NOI18N
                    ", photoshopHeadline BINARY" + // NOI18N
                    ", dcDescription BINARY" + // NOI18N
                    ", photoshopCaptionwriter BINARY" + // NOI18N
                    ", iptc4xmpcoreLocation BINARY" + // NOI18N
                    ", iptc4xmpcoreCountrycode BINARY" + // NOI18N
                    ", photoshopCategory BINARY" + // NOI18N
                    ", photoshopSupplementalCategories BINARY" + // NOI18N
                    ", dcRights BINARY" + // NOI18N
                    ", dcCreator BINARY" + // NOI18N
                    ", photoshopAuthorsposition BINARY" + // NOI18N
                    ", photoshopCity BINARY" + // NOI18N
                    ", photoshopState BINARY" + // NOI18N
                    ", photoshopCountry BINARY" + // NOI18N
                    ", photoshopTransmissionReference BINARY" + // NOI18N
                    ", photoshopInstructions BINARY" + // NOI18N
                    ", photoshopCredit BINARY" + // NOI18N
                    ", rating BINARY" +  // NOI18N
                    ", photoshopSource BINARY" + ");"); // NOI18N
            stmt.execute(
                    "CREATE UNIQUE INDEX idx_metadata_edit_templates_name ON metadata_edit_templates (name)"); // NOI18N
        }
    }

    private void createFavoriteDirectoriesTable(Connection connection,
            Statement stmt) throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(connection,
                "favorite_directories")) { // NOI18N
            stmt.execute("CREATE CACHED TABLE favorite_directories" + // NOI18N
                    " (" + // NOI18N
                    "favorite_name VARCHAR_IGNORECASE(256)" + // NOI18N
                    ", directory_name VARCHAR(512)" + // NOI18N
                    ", favorite_index INTEGER" + // NOI18N
                    ");"); // NOI18N
            stmt.execute(
                    "CREATE UNIQUE INDEX idx_favorite_directories_favorite_name ON favorite_directories (favorite_name)"); // NOI18N
        }
    }

    private void createFileExcludePatternTable(Connection connection,
            Statement stmt) throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(connection,
                "file_exclude_pattern")) { // NOI18N
            stmt.execute("CREATE CACHED TABLE file_exclude_pattern" + // NOI18N
                    " (" + // NOI18N
                    "pattern VARCHAR_IGNORECASE(256)" + // NOI18N
                    ");"); // NOI18N
            stmt.execute(
                    "CREATE UNIQUE INDEX idx_file_exclude_pattern_pattern ON file_exclude_pattern (pattern)"); // NOI18N
        }
    }

    private void createProgramsTable(Connection connection, Statement stmt)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(connection, "programs")) { // NOI18N
            stmt.execute("CREATE CACHED TABLE programs " + // NOI18N
                    " (" + // NOI18N
                    "id BIGINT NOT NULL" + // NOI18N
                    ", action BOOLEAN" + // NOI18N
                    ", filename VARCHAR(512) NOT NULL" + // NOI18N
                    ", alias VARCHAR_IGNORECASE(250) NOT NULL" + // NOI18N
                    ", parameters_before_filename BINARY" + // NOI18N
                    ", parameters_after_filename BINARY" + // NOI18N
                    ", input_before_execute BOOLEAN" + // NOI18N
                    ", input_before_execute_per_file BOOLEAN" + // NOI18N
                    ", single_file_processing BOOLEAN" + // NOI18N
                    ", change_file BOOLEAN" + // NOI18N
                    ", sequence_number INTEGER" + // NOI18N
                    ");"); // NOI18N
            stmt.execute("CREATE UNIQUE INDEX idx_programs_id ON programs (id)"); // NOI18N
            stmt.execute(
                    "CREATE INDEX idx_programs_filename ON programs (filename)"); // NOI18N
            stmt.execute("CREATE INDEX idx_programs_alias ON programs (alias)"); // NOI18N
            stmt.execute(
                    "CREATE INDEX idx_programs_sequence_number ON programs (sequence_number)"); // NOI18N
            stmt.execute("CREATE INDEX idx_programs_action ON programs (action)"); // NOI18N
        }
    }

    private void createActionsAfterDbInsertionTable(Connection connection,
            Statement stmt) throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(connection,
                "actions_after_db_insertion")) { // NOI18N
            stmt.execute("CREATE CACHED TABLE actions_after_db_insertion " + // NOI18N
                    " (" + // NOI18N
                    "id_programs BIGINT NOT NULL" + // NOI18N
                    ", action_order INTEGER" + // NOI18N
                    ");"); // NOI18N
            stmt.execute(
                    "CREATE UNIQUE INDEX idx_actions_after_db_insertion_id_programs ON actions_after_db_insertion (id_programs)"); // NOI18N
            stmt.execute(
                    "CREATE INDEX idx_actions_after_db_insertion_action_order ON actions_after_db_insertion (action_order)"); // NOI18N
        }
    }

    private void createHierarchicalSubjectsTable(Connection connection,
            Statement stmt) throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(connection,
                "hierarchical_subjects")) { // NOI18N
            stmt.execute("CREATE CACHED TABLE hierarchical_subjects " + // NOI18N
                    " (" + // NOI18N
                    "id BIGINT NOT NULL" + // NOI18N
                    ", id_parent BIGINT" + // NOI18N
                    ", subject  VARCHAR_IGNORECASE(64) NOT NULL" + // NOI18N: Same size as xmp_dc_subjects.subject
                    ", real BOOLEAN" + // NOI18N
                    ");"); // NOI18N
            stmt.execute(
                    "CREATE UNIQUE INDEX idx_hierarchical_subjects_id ON hierarchical_subjects (id)"); // NOI18N
            stmt.execute(
                    "CREATE INDEX idx_hierarchical_subjects_id_parent ON hierarchical_subjects (id_parent)"); // NOI18N
            stmt.execute(
                    "CREATE INDEX idx_hierarchical_subjects_subject ON hierarchical_subjects (subject)"); // NOI18N
            stmt.execute(
                    "CREATE INDEX idx_hierarchical_subjects_real ON hierarchical_subjects (real)"); // NOI18N
        }
    }

    private void errorMessageSqlException(SQLException ex) {
        LongMessageDialog dlg = new LongMessageDialog(null, true);
        dlg.setTitle(Bundle.getString("DatabaseTables.Error.Title")); // NOI18N
        dlg.setMessage(getExceptionMessage(ex));
        dlg.setVisible(true);
    }

    private String getExceptionMessage(SQLException ex) {
        return Bundle.getString("DatabaseTables.Error", // NOI18N
                ex.getLocalizedMessage());
    }

    /**
     * Creates the table for internal application usage such as update
     * information etc.
     * 
     * @param connection connection
     * @param stmt       sql statement
     * @throws           SQLException on sql errors
     */
    private void createAppTable(Connection connection, Statement stmt)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(connection, "application")) { // NOI18N
            stmt.execute("CREATE CACHED TABLE application " + // NOI18N
                    " (" + // NOI18N
                    "key VARCHAR(128) PRIMARY KEY" + // NOI18N
                    ", value BINARY" + // NOI18N
                    ");"); // NOI18N
        }
    }
}
