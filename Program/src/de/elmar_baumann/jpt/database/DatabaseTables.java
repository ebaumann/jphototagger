/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.database;

import de.elmar_baumann.jpt.app.AppLock;
import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.update.tables.UpdateTablesFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2008-10-21
 */
public final class DatabaseTables extends Database {
    public static final DatabaseTables INSTANCE = new DatabaseTables();

    private DatabaseTables() {}

    /**
     * Creates the necessary tables if not exists.
     * Exits the VM if not successfully.
     */
    public void createTables() {
        Connection con  = null;
        Statement  stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.createStatement();
            createAppTable(con, stmt);    // prior to all other tables!
            createFilesTable(con, stmt);
            createXmpTables(con, stmt);
            createExifTables(con, stmt);
            createCollectionsTables(con, stmt);
            createSavedSearchesTables(con, stmt);
            createAutoScanDirectoriesTable(con, stmt);
            createMetadataTemplateTable(con, stmt);
            createFavoriteDirectoriesTable(con, stmt);
            createFileExcludePatternTable(con, stmt);
            createProgramsTable(con, stmt);
            createActionsAfterDbInsertionTable(con, stmt);
            createHierarchicalSubjectsTable(con, stmt);
            createSynonymsTable(con, stmt);
            createRenameTemplatesTable(con, stmt);
            UpdateTablesFactory.INSTANCE.update(con);
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseTables.class, ex);

            if (ex instanceof SQLException) {
                errorMessageSqlException((SQLException) ex);
            }

            close(stmt);
            AppLock.unlock();
            System.exit(0);
        } finally {
            close(stmt);
            free(con);
        }
    }

    private void createFilesTable(Connection con, Statement stmt)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(con, "files")) {
            stmt.execute(
                "CREATE CACHED TABLE files ("
                + "  id BIGINT GENERATED BY DEFAULT"
                + " AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY"
                + ", filename VARCHAR_IGNORECASE(512) NOT NULL"
                + ", lastmodified BIGINT, thumbnail BINARY"
                + ", xmp_lastmodified BIGINT);");
            stmt.execute("CREATE UNIQUE INDEX idx_files ON files (filename)");
        }
    }

    private void createXmpTables(Connection con, Statement stmt)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(con, "xmp")) {
            stmt.execute(
                "CREATE CACHED TABLE xmp ("
                + "  id BIGINT GENERATED BY DEFAULT"
                + " AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY"
                + ", id_files BIGINT NOT NULL, id_dc_creator BIGINT"
                + ", dc_description VARCHAR_IGNORECASE(2000)"
                + ", id_dc_rights BIGINT, dc_title VARCHAR_IGNORECASE(64)"
                + ", id_iptc4xmpcore_location BIGINT"
                + ", id_photoshop_authorsposition BIGINT"
                + ", id_photoshop_captionwriter BIGINT"
                + ", id_photoshop_city BIGINT"
                + ", id_photoshop_country BIGINT"
                + ", id_photoshop_credit BIGINT"
                + ", photoshop_headline VARCHAR_IGNORECASE(256)"
                + ", photoshop_instructions VARCHAR_IGNORECASE(256)"
                + ", id_photoshop_source BIGINT"
                + ", id_photoshop_state BIGINT"
                + ", photoshop_transmissionReference VARCHAR_IGNORECASE(32)"
                + ", rating BIGINT"
                + ", iptc4xmpcore_datecreated VARCHAR_IGNORECASE(10)"
                + ", FOREIGN KEY (id_files)"
                + " REFERENCES files (id) ON DELETE CASCADE"
                + ", FOREIGN KEY (id_dc_creator)"
                + " REFERENCES dc_creator (id) ON DELETE SET NULL"
                + ", FOREIGN KEY (id_dc_rights)"
                + " REFERENCES dc_rights (id) ON DELETE SET NULL"
                + ", FOREIGN KEY (id_iptc4xmpcore_location)"
                + " REFERENCES iptc4xmpcore_location (id) ON DELETE SET NULL"
                + ", FOREIGN KEY (id_photoshop_authorsposition)"
                + " REFERENCES photoshop_authorsposition (id)"
                + " ON DELETE SET NULL"
                + ", FOREIGN KEY (id_photoshop_captionwriter)"
                + " REFERENCES photoshop_captionwriter (id) ON DELETE SET NULL"
                + ", FOREIGN KEY (id_photoshop_city)"
                + " REFERENCES photoshop_city (id) ON DELETE SET NULL"
                + ", FOREIGN KEY (id_photoshop_country)"
                + " REFERENCES photoshop_country (id) ON DELETE SET NULL"
                + ", FOREIGN KEY (id_photoshop_credit)"
                + " REFERENCES photoshop_credit (id) ON DELETE SET NULL"
                + ", FOREIGN KEY (id_photoshop_source)"
                + " REFERENCES photoshop_source (id) ON DELETE SET NULL"
                + ", FOREIGN KEY (id_photoshop_state)"
                + " REFERENCES photoshop_state (id) ON DELETE SET NULL);");
            stmt.execute("CREATE UNIQUE INDEX idx_xmp_id_files"
                         + " ON xmp (id_files)");
            stmt.execute("CREATE INDEX idx_xmp_dc_description"
                         + " ON xmp (dc_description)");
            stmt.execute(
                "CREATE INDEX idx_xmp_id_dc_rights ON xmp (id_dc_rights)");
            stmt.execute("CREATE INDEX idx_xmp_dc_title ON xmp (dc_title)");
            stmt.execute("CREATE INDEX idx_xmp_iptc4xmpcore_location"
                         + " ON xmp (iptc4xmpcore_location)");
            stmt.execute("CREATE INDEX idx_xmp_id_photoshop_authorsposition"
                         + " ON xmp (id_photoshop_authorsposition)");
            stmt.execute("CREATE INDEX idx_xmp_id_photoshop_captionwriter"
                         + " ON xmp (id_photoshop_captionwriter)");
            stmt.execute("CREATE INDEX idx_xmp_id_photoshop_city"
                         + " ON xmp (id_photoshop_city)");
            stmt.execute("CREATE INDEX idx_xmp_id_photoshop_country"
                         + " ON xmp (id_photoshop_country)");
            stmt.execute("CREATE INDEX idx_xmp_id_photoshop_credit"
                         + " ON xmp (id_photoshop_credit)");
            stmt.execute("CREATE INDEX idx_xmp_photoshop_headline"
                         + " ON xmp (photoshop_headline)");
            stmt.execute("CREATE INDEX idx_xmp_photoshop_instructions"
                         + " ON xmp (photoshop_instructions)");
            stmt.execute("CREATE INDEX idx_xmp_id_photoshop_source"
                         + " ON xmp (id_photoshop_source)");
            stmt.execute("CREATE INDEX idx_xmp_id_photoshop_state"
                         + " ON xmp (id_photoshop_state)");
            stmt.execute("CREATE INDEX idx_xmp_photoshop_transmissionReference"
                         + " ON xmp (photoshop_transmissionReference)");
            stmt.execute("CREATE INDEX idx_iptc4xmpcore_datecreated"
                         + " ON xmp (iptc4xmpcore_datecreated)");
        }

        create1nTable(con, stmt, "dc_creator", "creator", 128);
        create1nTable(con, stmt, "dc_rights", "rights", 128);
        create1nTable(con, stmt, "iptc4xmpcore_location", "location", 64);
        create1nTable(con, stmt, "photoshop_authorsposition",
                      "authorsposition", 32);
        create1nTable(con, stmt, "photoshop_captionwriter", "captionwriter",
                      32);
        create1nTable(con, stmt, "photoshop_city", "city", 32);
        create1nTable(con, stmt, "photoshop_country", "country", 64);
        create1nTable(con, stmt, "photoshop_credit", "credit", 32);
        create1nTable(con, stmt, "photoshop_source", "source", 32);
        create1nTable(con, stmt, "photoshop_state", "state", 32);

        if (!DatabaseMetadata.INSTANCE.existsTable(con, "dc_subjects")) {
            stmt.execute(
                "CREATE CACHED TABLE dc_subjects ("
                + " id BIGINT GENERATED BY DEFAULT"
                + " AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY"
                + ", subject VARCHAR_IGNORECASE(64));");
            stmt.execute("CREATE UNIQUE INDEX idx_dc_subjects_id"
                         + " ON dc_subjects (id)");
            stmt.execute("CREATE UNIQUE INDEX idx_dc_subjects_subject"
                         + " ON dc_subjects (subject)");
        }

        if (!DatabaseMetadata.INSTANCE.existsTable(con, "xmp_dc_subject")) {
            stmt.execute(
                "CREATE CACHED TABLE xmp_dc_subject (id_xmp BIGINT"
                + ", id_dc_subject BIGINT"
                + ", PRIMARY KEY (id_xmp, id_dc_subject)"
                + ", FOREIGN KEY (id_xmp) REFERENCES xmp (id) ON DELETE CASCADE"
                + ", FOREIGN KEY (id_dc_subject)"
                + " REFERENCES dc_subjects (id) ON DELETE CASCADE);");
            stmt.execute("CREATE INDEX idx_xmp_dc_subject_pk"
                         + " ON xmp_dc_subject (id_xmp, id_dc_subject)");
            stmt.execute("CREATE INDEX idx_xmp_dc_subject_id_xmp"
                         + " ON xmp_dc_subject (id_xmp)");
            stmt.execute("CREATE INDEX idx_xmp_dc_subject_id_dc_subject"
                         + " ON xmp_dc_subject (id_dc_subject)");
        }
    }

    private void create1nTable(Connection con, Statement stmt,
                               String tablename, String columnname, int length)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(con, tablename)) {
            stmt.execute(
                "CREATE CACHED TABLE " + tablename
                + " (id BIGINT GENERATED BY DEFAULT"
                + " AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY, "
                + columnname + " VARCHAR_IGNORECASE("
                + Integer.toString(length) + "))");
            stmt.execute("CREATE UNIQUE INDEX idx_" + tablename + "_id"
                         + " ON " + tablename + " (id)");
            stmt.execute("CREATE UNIQUE INDEX idx_" + tablename + "_"
                         + columnname + " ON " + tablename + " (" + columnname
                         + ")");
        }
    }

    private void createExifTables(Connection con, Statement stmt)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(con, "exif")) {
            stmt.execute(
                "CREATE CACHED TABLE exif (id BIGINT GENERATED BY DEFAULT"
                + " AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY"
                + ", id_files BIGINT NOT NULL"
                + ", id_exif_recording_equipment BIGINT"
                + ", exif_date_time_original DATE, exif_focal_length REAL"
                + ", exif_iso_speed_ratings SMALLINT"
                + ", id_exif_lens  BIGINT, FOREIGN KEY (id_files)"
                + " REFERENCES files (id) ON DELETE CASCADE"
                + ", FOREIGN KEY (id_exif_recording_equipment)"
                + " REFERENCES exif_recording_equipment (id) ON DELETE SET NULL"
                + ", FOREIGN KEY (id_exif_lens)"
                + " REFERENCES exif_lens (id) ON DELETE SET NULL);");
            stmt.execute("CREATE UNIQUE INDEX idx_exif_id_files"
                         + " ON exif (id_files)");
            stmt.execute("CREATE INDEX idx_exif_id_recording_equipment"
                         + " ON exif (id_exif_recording_equipment)");
            stmt.execute("CREATE INDEX idx_exif_date_time_original"
                         + " ON exif (exif_date_time_original)");
            stmt.execute("CREATE INDEX idx_exif_focal_length"
                         + " ON exif (exif_focal_length)");
            stmt.execute("CREATE INDEX idx_exif_iso_speed_ratings"
                         + " ON exif (exif_iso_speed_ratings)");
            stmt.execute(
                "CREATE INDEX idx_exif_id_exif_lens ON exif (id_exif_lens)");
        }

        create1nTable(con, stmt, "exif_recording_equipment", "equipment", 125);
        create1nTable(con, stmt, "exif_lens", "lens", 256);
    }

    private void createCollectionsTables(Connection con, Statement stmt)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(con, "collection_names")) {
            stmt.execute(
                "CREATE CACHED TABLE collection_names ("
                + "  id BIGINT GENERATED BY DEFAULT"
                + " AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY"
                + ", name VARCHAR_IGNORECASE(256));");
            stmt.execute("CREATE UNIQUE INDEX idx_collection_names_id ON"
                         + " collection_names (id)");
            stmt.execute("CREATE INDEX idx_collection_names_name"
                         + " ON collection_names (name)");
        }

        if (!DatabaseMetadata.INSTANCE.existsTable(con, "collections")) {
            stmt.execute(
                "CREATE CACHED TABLE collections ("
                + "  id_collectionnnames BIGINT, id_files BIGINT"
                + ", sequence_number INTEGER"
                + ", FOREIGN KEY (id_collectionnnames)"
                + " REFERENCES collection_names (id) ON DELETE CASCADE"
                + ", FOREIGN KEY (id_files) REFERENCES files (id)"
                + " ON DELETE CASCADE);");
            stmt.execute("CREATE INDEX idx_collections_id_collectionnnames"
                         + " ON collections (id_collectionnnames)");
            stmt.execute("CREATE INDEX idx_collections_id_files"
                         + " ON collections (id_files)");
            stmt.execute("CREATE INDEX idx_collections_sequence_number"
                         + " ON collections (sequence_number)");
        }
    }

    private void createSavedSearchesTables(Connection con, Statement stmt)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(con, "saved_searches")) {
            stmt.execute(
                "CREATE CACHED TABLE saved_searches ("
                + "  id BIGINT GENERATED BY DEFAULT"
                + " AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY"
                + ", name VARCHAR_IGNORECASE(125), sql_string BINARY"
                + ", is_query BOOLEAN, search_type SMALLINT);");
            stmt.execute("CREATE UNIQUE INDEX idx_saved_searches_id"
                         + " ON saved_searches (id)");
            stmt.execute("CREATE UNIQUE INDEX idx_saved_searches_name"
                         + " ON saved_searches (name)");
        }

        if (!DatabaseMetadata.INSTANCE.existsTable(con,
                "saved_searches_values")) {
            stmt.execute("CREATE CACHED TABLE saved_searches_values ("
                         + "  id_saved_searches BIGINT"
                         + ", value VARCHAR(256), value_index INTEGER"
                         + ", FOREIGN KEY (id_saved_searches)"
                         + " REFERENCES saved_searches (id) ON DELETE CASCADE"
                         + ");");
            stmt.execute("CREATE INDEX idx_saved_searches_id_saved_searches"
                         + " ON saved_searches_values (id_saved_searches)");
            stmt.execute("CREATE INDEX idx_saved_searches_value_index"
                         + " ON saved_searches_values (value_index)");
        }

        if (!DatabaseMetadata.INSTANCE.existsTable(con,
                "saved_searches_panels")) {
            stmt.execute("CREATE CACHED TABLE saved_searches_panels ("
                         + "  id_saved_searches BIGINT"
                         + ", panel_index INTEGER, bracket_left_1 BOOLEAN"
                         + ", operator_id INTEGER, bracket_left_2 BOOLEAN"
                         + ", column_id INTEGER, comparator_id  INTEGER"
                         + ", value VARCHAR(256), bracket_right BOOLEAN"
                         + ", FOREIGN KEY (id_saved_searches)"
                         + " REFERENCES saved_searches (id) ON DELETE CASCADE"
                         + ");");
            stmt.execute(
                "CREATE INDEX idx_saved_searches_panels_id_saved_searches"
                + " ON saved_searches_panels (id_saved_searches)");
            stmt.execute("CREATE INDEX idx_saved_searches_panels_panel_index"
                         + " ON saved_searches_panels (panel_index)");
        }
    }

    private void createAutoScanDirectoriesTable(Connection con, Statement stmt)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(con,
                "autoscan_directories")) {
            stmt.execute("CREATE CACHED TABLE autoscan_directories ("
                         + "directory VARCHAR_IGNORECASE(1024));");
            stmt.execute(
                "CREATE UNIQUE INDEX idx_autoscan_directories_directory"
                + " ON autoscan_directories (directory)");
        }
    }

    private void createMetadataTemplateTable(Connection con, Statement stmt)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(con,
                "metadata_edit_templates")) {
            stmt.execute("CREATE CACHED TABLE metadata_edit_templates ("
                         + "  name VARCHAR_IGNORECASE(256)"
                         + ", dcSubjects BINARY, dcTitle BINARY"
                         + ", photoshopHeadline BINARY"
                         + ", dcDescription BINARY"
                         + ", photoshopCaptionwriter BINARY"
                         + ", iptc4xmpcoreLocation BINARY"
                         + ", dcRights BINARY, dcCreator BINARY"
                         + ", photoshopAuthorsposition BINARY"
                         + ", photoshopCity BINARY, photoshopState BINARY"
                         + ", photoshopCountry BINARY"
                         + ", photoshopTransmissionReference BINARY"
                         + ", photoshopInstructions BINARY"
                         + ", photoshopCredit BINARY"
                         + ", photoshopSource BINARY, rating BINARY"
                         + ", iptc4xmpcore_datecreated BINARY);");
            stmt.execute("CREATE UNIQUE INDEX idx_metadata_edit_templates_name"
                         + " ON metadata_edit_templates (name)");
        }
    }

    private void createFavoriteDirectoriesTable(Connection con, Statement stmt)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(con,
                "favorite_directories")) {
            stmt.execute("CREATE CACHED TABLE favorite_directories ("
                         + "  favorite_name  VARCHAR_IGNORECASE(256)"
                         + ", directory_name VARCHAR(512)"
                         + ", favorite_index INTEGER);");
            stmt.execute(
                "CREATE UNIQUE INDEX idx_favorite_directories_favorite_name"
                + " ON favorite_directories (favorite_name)");
        }
    }

    private void createFileExcludePatternTable(Connection con, Statement stmt)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(con,
                "file_exclude_pattern")) {
            stmt.execute("CREATE CACHED TABLE file_exclude_pattern ("
                         + "pattern VARCHAR_IGNORECASE(256));");
            stmt.execute("CREATE UNIQUE INDEX idx_file_exclude_pattern_pattern"
                         + " ON file_exclude_pattern (pattern)");
        }
    }

    private void createProgramsTable(Connection con, Statement stmt)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(con, "programs")) {
            stmt.execute("CREATE CACHED TABLE programs ("
                         + "  id BIGINT NOT NULL, action BOOLEAN"
                         + ", filename VARCHAR(512) NOT NULL"
                         + ", alias VARCHAR_IGNORECASE(250) NOT NULL"
                         + ", parameters_before_filename BINARY"
                         + ", parameters_after_filename BINARY"
                         + ", input_before_execute BOOLEAN"
                         + ", input_before_execute_per_file BOOLEAN"
                         + ", single_file_processing BOOLEAN"
                         + ", change_file BOOLEAN"
                         + ", sequence_number INTEGER"
                         + ", use_pattern BOOLEAN, pattern BINARY);");
            stmt.execute(
                "CREATE UNIQUE INDEX idx_programs_id ON programs (id)");
            stmt.execute(
                "CREATE INDEX idx_programs_filename ON programs (filename)");
            stmt.execute("CREATE INDEX idx_programs_alias ON programs (alias)");
            stmt.execute("CREATE INDEX idx_programs_sequence_number"
                         + " ON programs (sequence_number)");
            stmt.execute(
                "CREATE INDEX idx_programs_action ON programs (action)");
        }
    }

    private void createActionsAfterDbInsertionTable(Connection con,
            Statement stmt)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(con,
                "actions_after_db_insertion")) {
            stmt.execute("CREATE CACHED TABLE actions_after_db_insertion "
                         + " (id_programs BIGINT NOT NULL"
                         + ", action_order INTEGER);");
            stmt.execute(
                "CREATE UNIQUE INDEX idx_actions_after_db_insertion_id_programs"
                + " ON actions_after_db_insertion (id_programs)");
            stmt.execute(
                "CREATE INDEX idx_actions_after_db_insertion_action_order"
                + " ON actions_after_db_insertion (action_order)");
        }
    }

    private void createHierarchicalSubjectsTable(Connection con, Statement stmt)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(con,
                "hierarchical_subjects")) {
            stmt.execute("CREATE CACHED TABLE hierarchical_subjects ("
                         + "  id BIGINT NOT NULL, id_parent BIGINT"
                         + ", subject VARCHAR_IGNORECASE(64) NOT NULL"
                         + ", real BOOLEAN);");
            stmt.execute("CREATE UNIQUE INDEX idx_hierarchical_subjects_id "
                         + "ON hierarchical_subjects (id)");
            stmt.execute("CREATE INDEX idx_hierarchical_subjects_id_parent"
                         + " ON hierarchical_subjects (id_parent)");
            stmt.execute("CREATE INDEX idx_hierarchical_subjects_subject"
                         + " ON hierarchical_subjects (subject)");
            stmt.execute("CREATE INDEX idx_hierarchical_subjects_real"
                         + " ON hierarchical_subjects (real)");
        }
    }

    /**
     * Creates the table for internal application usage such as update
     * information etc.
     *
     * @param con   connection
     * @param stmt  sql statement
     * @throws      SQLException on sql errors
     */
    private void createAppTable(Connection con, Statement stmt)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(con, "application")) {
            stmt.execute("CREATE CACHED TABLE application ("
                         + " key VARCHAR(128) PRIMARY KEY, value BINARY);");
        }
    }

    private void createSynonymsTable(Connection con, Statement stmt)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(con, "synonyms")) {
            stmt.execute("CREATE CACHED TABLE synonyms ("
                         + " word VARCHAR(128), synonym VARCHAR(128)"
                         + ", PRIMARY KEY (word, synonym));");
            stmt.execute(
                "CREATE UNIQUE INDEX idx_synonyms ON synonyms (word, synonym)");
            stmt.execute("CREATE INDEX idx_synonyms_word ON synonyms (word)");
            stmt.execute(
                "CREATE INDEX idx_synonyms_synonym ON synonyms (synonym)");
        }
    }

    private void createRenameTemplatesTable(Connection con, Statement stmt)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(con, "rename_templates")) {
            stmt.execute(
                "CREATE CACHED TABLE rename_templates ("
                + "  id BIGINT GENERATED BY DEFAULT"
                + " AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY"
                + ", name VARCHAR(128) NOT NULL, start_number INTEGER"
                + ", step_width INTEGER, number_count INTEGER"
                + ", date_delimiter VARCHAR(5)"
                + ", format_class_at_begin VARCHAR(512)"
                + ", delimiter_1 VARCHAR(25)"
                + ", format_class_in_the_middle VARCHAR(512)"
                + ", delimiter_2 VARCHAR(25)"
                + ", format_class_at_end VARCHAR(512)"
                + ", text_at_begin VARCHAR(512)"
                + ", text_in_the_middle VARCHAR(512)"
                + ", text_at_end VARCHAR(512), UNIQUE(name));");
            stmt.execute("CREATE UNIQUE INDEX idx_rename_templates_name"
                         + " ON rename_templates (name)");
        }
    }
}
