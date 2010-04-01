/*
 * @(#)UpdateTablesMakePlural.java    Created on 2010-03-31
 *
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

package org.jphototagger.program.app.update.tables.v0;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.SplashScreen;
import org.jphototagger.program.database.Database;
import org.jphototagger.program.database.DatabaseMetadata;
import org.jphototagger.program.resource.JptBundle;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author Elmar Baumann
 */
final class UpdateTablesMakePlural {
    private static final Map<String, String> TO_TABLE_NAME_OF =
        new HashMap<String, String>();
    private static final Map<String, String> TO_INDEX_NAME_OF =
        new HashMap<String, String>();

    static {

        // Tables
        TO_TABLE_NAME_OF.put("exif_lens", "exif_lenses");
        TO_TABLE_NAME_OF.put("dc_creator", "dc_creators");
        TO_TABLE_NAME_OF.put("iptc4xmpcore_location", "iptc4xmpcore_locations");
        TO_TABLE_NAME_OF.put("photoshop_authorsposition",
                             "photoshop_authorspositions");
        TO_TABLE_NAME_OF.put("photoshop_captionwriter",
                             "photoshop_captionwriters");
        TO_TABLE_NAME_OF.put("photoshop_city", "photoshop_cities");
        TO_TABLE_NAME_OF.put("photoshop_country", "photoshop_countries");
        TO_TABLE_NAME_OF.put("photoshop_credit", "photoshop_credits");
        TO_TABLE_NAME_OF.put("photoshop_source", "photoshop_sources");
        TO_TABLE_NAME_OF.put("photoshop_state", "photoshop_states");
        TO_TABLE_NAME_OF.put("user_defined_file_filter",
                             "user_defined_file_filters");
        TO_TABLE_NAME_OF.put("file_exclude_pattern", "file_exclude_patterns");

        // Indices of renamed tables
        TO_INDEX_NAME_OF.put("idx_exif_lens_id", "idx_exif_lenses_id");
        TO_INDEX_NAME_OF.put("idx_exif_lens_lens", "idx_exif_lenses_lens");
        TO_INDEX_NAME_OF.put("idx_dc_creator_id", "idx_dc_creators_id");
        TO_INDEX_NAME_OF.put("idx_dc_creator_creator",
                             "idx_dc_creators_creator");
        TO_INDEX_NAME_OF.put("idx_iptc4xmpcore_location_id",
                             "idx_iptc4xmpcore_locations_id");
        TO_INDEX_NAME_OF.put("idx_iptc4xmpcore_location_location",
                             "idx_iptc4xmpcore_locations_location");
        TO_INDEX_NAME_OF.put("idx_photoshop_authorsposition_id",
                             "idx_photoshop_authorspositions_id");
        TO_INDEX_NAME_OF.put("idx_photoshop_authorsposition_authorsposition",
                             "idx_photoshop_authorspositions_authorsposition");
        TO_INDEX_NAME_OF.put("idx_photoshop_captionwriter_id",
                             "idx_photoshop_captionwriters_id");
        TO_INDEX_NAME_OF.put("idx_photoshop_captionwriter_captionwriter",
                             "idx_photoshop_captionwriters_captionwriter");
        TO_INDEX_NAME_OF.put("idx_photoshop_city_id",
                             "idx_photoshop_cities_id");
        TO_INDEX_NAME_OF.put("idx_photoshop_city_city",
                             "idx_photoshop_cities_city");
        TO_INDEX_NAME_OF.put("idx_photoshop_country_id",
                             "idx_photoshop_countries_id");
        TO_INDEX_NAME_OF.put("idx_photoshop_country_country",
                             "idx_photoshop_countries_country");
        TO_INDEX_NAME_OF.put("idx_photoshop_credit_id",
                             "idx_photoshop_credits_id");
        TO_INDEX_NAME_OF.put("idx_photoshop_credit_credit",
                             "idx_photoshop_credits_credit");
        TO_INDEX_NAME_OF.put("idx_photoshop_source_id",
                             "idx_photoshop_sources_id");
        TO_INDEX_NAME_OF.put("idx_photoshop_source_source",
                             "idx_photoshop_sources_source");
        TO_INDEX_NAME_OF.put("idx_photoshop_state_id",
                             "idx_photoshop_states_id");
        TO_INDEX_NAME_OF.put("idx_photoshop_state_state",
                             "idx_photoshop_states_state");
        TO_INDEX_NAME_OF.put("idx_user_defined_file_filter_name",
                             "idx_user_defined_file_filters_name");
        TO_INDEX_NAME_OF.put("idx_file_exclude_pattern_pattern",
                             "idx_file_exclude_patterns_pattern");
    }

    void update(Connection con) throws SQLException {
        startMessage();
        renameTables(con);
        renameIndices(con);
        SplashScreen.INSTANCE.removeMessage();
    }

    private void renameTables(Connection con) throws SQLException {
        Statement stmt = null;

        try {
            stmt = con.createStatement();

            for (String fromName : TO_TABLE_NAME_OF.keySet()) {
                String toName = TO_TABLE_NAME_OF.get(fromName);

                if (DatabaseMetadata.INSTANCE.existsTable(con, fromName)
                        &&!DatabaseMetadata.INSTANCE.existsTable(con, toName)) {
                    String sql = "ALTER TABLE " + fromName + " RENAME TO "
                                 + toName;

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

            for (String fromName : TO_INDEX_NAME_OF.keySet()) {
                String sql = "ALTER INDEX " + fromName + " RENAME TO "
                             + TO_INDEX_NAME_OF.get(fromName);

                try {
                    stmt.executeUpdate(sql);
                } catch (SQLException ex) {
                    AppLogger.logSevere(getClass(), ex);
                }
            }
        } finally {
            Database.close(stmt);
        }
    }

    private void startMessage() {
        SplashScreen.INSTANCE.setMessage(
            JptBundle.INSTANCE.getString("UpdateTablesMakePlural.Info"));
    }
}
