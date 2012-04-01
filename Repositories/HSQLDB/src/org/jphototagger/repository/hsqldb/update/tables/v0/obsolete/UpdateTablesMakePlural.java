package org.jphototagger.repository.hsqldb.update.tables.v0.obsolete;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.repository.hsqldb.Database;
import org.jphototagger.repository.hsqldb.DatabaseMetadata;

/**
 * @author Elmar Baumann
 */
final class UpdateTablesMakePlural {

    private static final Map<String, String> TO_TABLE_NAME_OF = new HashMap<String, String>();
    private static final List<IndexRenameInfo> INDICES_TO_RENAME = new ArrayList<IndexRenameInfo>();
    private static final Logger LOGGER = Logger.getLogger(UpdateTablesMakePlural.class.getName());

    private static class IndexRenameInfo {

        private final String tableName;
        private final String fromName;
        private final String toName;

        private IndexRenameInfo(String tableName, String fromName, String toName) {
            this.tableName = tableName;
            this.fromName = fromName;
            this.toName = toName;
        }
    }

    static {

        // Tables
        TO_TABLE_NAME_OF.put("exif_lens", "exif_lenses");
        TO_TABLE_NAME_OF.put("dc_creator", "dc_creators");
        TO_TABLE_NAME_OF.put("iptc4xmpcore_location", "iptc4xmpcore_locations");
        TO_TABLE_NAME_OF.put("photoshop_authorsposition", "photoshop_authorspositions");
        TO_TABLE_NAME_OF.put("photoshop_captionwriter", "photoshop_captionwriters");
        TO_TABLE_NAME_OF.put("photoshop_city", "photoshop_cities");
        TO_TABLE_NAME_OF.put("photoshop_country", "photoshop_countries");
        TO_TABLE_NAME_OF.put("photoshop_credit", "photoshop_credits");
        TO_TABLE_NAME_OF.put("photoshop_source", "photoshop_sources");
        TO_TABLE_NAME_OF.put("photoshop_state", "photoshop_states");
        TO_TABLE_NAME_OF.put("user_defined_file_filter", "user_defined_file_filters");
        TO_TABLE_NAME_OF.put("file_exclude_pattern", "file_exclude_patterns");

        // Indices of renamed tables
        INDICES_TO_RENAME.add(new IndexRenameInfo("exif_lenses", "idx_exif_lens_id", "idx_exif_lenses_id"));
        INDICES_TO_RENAME.add(new IndexRenameInfo("exif_lenses", "idx_exif_lens_lens", "idx_exif_lenses_lens"));
        INDICES_TO_RENAME.add(new IndexRenameInfo("dc_creators", "idx_dc_creator_id", "idx_dc_creators_id"));
        INDICES_TO_RENAME.add(new IndexRenameInfo("dc_creators", "idx_dc_creator_creator", "idx_dc_creators_creator"));
        INDICES_TO_RENAME.add(new IndexRenameInfo("iptc4xmpcore_locations", "idx_iptc4xmpcore_location_id",
                "idx_iptc4xmpcore_locations_id"));
        INDICES_TO_RENAME.add(new IndexRenameInfo("iptc4xmpcore_locations", "idx_iptc4xmpcore_location_location",
                "idx_iptc4xmpcore_locations_location"));
        INDICES_TO_RENAME.add(new IndexRenameInfo("photoshop_authorspositions", "idx_photoshop_authorsposition_id",
                "idx_photoshop_authorspositions_id"));
        INDICES_TO_RENAME.add(new IndexRenameInfo("photoshop_authorspositions",
                "idx_photoshop_authorsposition_authorsposition", "idx_photoshop_authorspositions_authorsposition"));
        INDICES_TO_RENAME.add(new IndexRenameInfo("photoshop_captionwriters", "idx_photoshop_captionwriter_id",
                "idx_photoshop_captionwriters_id"));
        INDICES_TO_RENAME.add(new IndexRenameInfo("photoshop_captionwriters",
                "idx_photoshop_captionwriter_captionwriter", "idx_photoshop_captionwriters_captionwriter"));
        INDICES_TO_RENAME.add(new IndexRenameInfo("photoshop_cities", "idx_photoshop_city_id",
                "idx_photoshop_cities_id"));
        INDICES_TO_RENAME.add(new IndexRenameInfo("photoshop_cities", "idx_photoshop_city_city",
                "idx_photoshop_cities_city"));
        INDICES_TO_RENAME.add(new IndexRenameInfo("photoshop_countries", "idx_photoshop_country_id",
                "idx_photoshop_countries_id"));
        INDICES_TO_RENAME.add(new IndexRenameInfo("photoshop_countries", "idx_photoshop_country_country",
                "idx_photoshop_countries_country"));
        INDICES_TO_RENAME.add(new IndexRenameInfo("photoshop_credits", "idx_photoshop_credit_id",
                "idx_photoshop_credits_id"));
        INDICES_TO_RENAME.add(new IndexRenameInfo("photoshop_credits", "idx_photoshop_credit_credit",
                "idx_photoshop_credits_credit"));
        INDICES_TO_RENAME.add(new IndexRenameInfo("photoshop_sources", "idx_photoshop_source_id",
                "idx_photoshop_sources_id"));
        INDICES_TO_RENAME.add(new IndexRenameInfo("photoshop_sources", "idx_photoshop_source_source",
                "idx_photoshop_sources_source"));
        INDICES_TO_RENAME.add(new IndexRenameInfo("photoshop_states", "idx_photoshop_state_id",
                "idx_photoshop_states_id"));
        INDICES_TO_RENAME.add(new IndexRenameInfo("photoshop_states", "idx_photoshop_state_state",
                "idx_photoshop_states_state"));
        INDICES_TO_RENAME.add(new IndexRenameInfo("user_defined_file_filters", "idx_user_defined_file_filter_name",
                "idx_user_defined_file_filters_name"));
        INDICES_TO_RENAME.add(new IndexRenameInfo("file_exclude_patterns", "idx_file_exclude_pattern_pattern",
                "idx_file_exclude_patterns_pattern"));
    }

    void update(Connection con) throws SQLException {
        LOGGER.log(Level.INFO, "Renaming tables into plural");
        renameTables(con);
        renameIndices(con);
    }

    private void renameTables(Connection con) throws SQLException {
        Statement stmt = null;

        try {
            stmt = con.createStatement();

            for (String fromName : TO_TABLE_NAME_OF.keySet()) {
                String toName = TO_TABLE_NAME_OF.get(fromName);

                if (DatabaseMetadata.INSTANCE.existsTable(con, fromName)
                        && !DatabaseMetadata.INSTANCE.existsTable(con, toName)) {
                    String sql = "ALTER TABLE " + fromName + " RENAME TO " + toName;

                    LOGGER.log(Level.FINER, sql);
                    stmt.executeUpdate(sql);
                }
            }
        } finally {
            Database.close(stmt);
        }
    }

    private void renameIndices(Connection con) throws SQLException {
        Statement stmt = null;

        try {
            stmt = con.createStatement();

            for (IndexRenameInfo info : INDICES_TO_RENAME) {
                if (DatabaseMetadata.existsIndex(con, info.fromName, info.tableName)
                        && !DatabaseMetadata.existsIndex(con, info.toName, info.tableName)) {
                    String sql = "ALTER INDEX " + info.fromName + " RENAME TO " + info.toName;

                    LOGGER.log(Level.FINER, sql);
                    stmt.executeUpdate(sql);
                }
            }
        } finally {
            Database.close(stmt);
        }
    }
}
