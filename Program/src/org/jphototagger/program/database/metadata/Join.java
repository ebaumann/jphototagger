/*
 * @(#)Join.java    Created on 2008-10-05
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

package org.jphototagger.program.database.metadata;

import java.util.HashMap;
import java.util.Map;

/**
 * SQL-Joins.
 *
 * @author  Elmar Baumann
 */
public final class Join {
    private static final Map<String, String> JOIN_FROM_FILES =
        new HashMap<String, String>();
    private static final Map<String, String> NULL_SQL_OF = new HashMap<String,
                                                               String>();
    private static final Map<String, String> NOT_NULL_SQL_OF =
        new HashMap<String, String>();
    private static final Map<String, String> DELETE_SQL_OF =
        new HashMap<String, String>();

    static {
        JOIN_FROM_FILES.put("files", "");
        JOIN_FROM_FILES.put(
            "dc_subjects",
            "\\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_files"
            + " INNER JOIN xmp_dc_subject ON xmp.id = xmp_dc_subject.id_xmp"
            + " INNER JOIN dc_subjects"
            + " ON xmp_dc_subject.id_dc_subject = dc_subjects.id");
        JOIN_FROM_FILES.put("exif",
                            "\\JOIN_TYPE\\ JOIN exif"
                            + " ON files.id = exif.id_files");
        JOIN_FROM_FILES.put(
            "xmp", "\\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_files");
        JOIN_FROM_FILES.put("exif_recording_equipment",
                            "\\JOIN_TYPE\\ JOIN exif"
                            + " ON files.id = exif.id_files"
                            + " INNER JOIN exif_recording_equipment"
                            + " ON exif.id_exif_recording_equipment"
                            + " = exif_recording_equipment.id");
        JOIN_FROM_FILES.put("exif_lens",
                            "\\JOIN_TYPE\\ JOIN exif"
                            + " ON files.id = exif.id_files"
                            + " INNER JOIN exif_lens"
                            + " ON exif.id_exif_lens = exif_lens.id");
        JOIN_FROM_FILES.put("dc_creator",
                            "\\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_files"
                            + " INNER JOIN dc_creator"
                            + " ON xmp.id_dc_creator = dc_creator.id");
        JOIN_FROM_FILES.put("dc_rights",
                            "\\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_files"
                            + " INNER JOIN dc_rights"
                            + " ON xmp.id_dc_rights = dc_rights.id");
        JOIN_FROM_FILES.put("iptc4xmpcore_location",
                            "\\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_files"
                            + " INNER JOIN iptc4xmpcore_location"
                            + " ON xmp.id_iptc4xmpcore_location"
                            + " = iptc4xmpcore_location.id");
        JOIN_FROM_FILES.put("photoshop_authorsposition",
                            "\\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_files"
                            + " INNER JOIN photoshop_authorsposition"
                            + " ON xmp.id_photoshop_authorsposition"
                            + " = photoshop_authorsposition.id");
        JOIN_FROM_FILES.put("photoshop_captionwriter",
                            "\\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_files"
                            + " INNER JOIN photoshop_captionwriter"
                            + " ON xmp.id_photoshop_captionwriter"
                            + " = photoshop_captionwriter.id");
        JOIN_FROM_FILES.put("photoshop_city",
                            "\\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_files"
                            + " INNER JOIN photoshop_city"
                            + " ON xmp.id_photoshop_city = photoshop_city.id");
        JOIN_FROM_FILES.put("photoshop_country",
                            "\\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_files"
                            + " INNER JOIN photoshop_country"
                            + " ON xmp.id_photoshop_country"
                            + " = photoshop_country.id");
        JOIN_FROM_FILES.put("photoshop_credit",
                            "\\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_files"
                            + " INNER JOIN photoshop_credit"
                            + " ON xmp.id_photoshop_credit"
                            + " = photoshop_credit.id");
        JOIN_FROM_FILES.put("photoshop_source",
                            "\\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_files"
                            + " INNER JOIN photoshop_source"
                            + " ON xmp.id_photoshop_source"
                            + " = photoshop_source.id");
        JOIN_FROM_FILES.put(
            "photoshop_state",
            "\\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_files"
            + " INNER JOIN photoshop_state"
            + " ON xmp.id_photoshop_state = photoshop_state.id");
        NULL_SQL_OF.put("exif_recording_equipment",
                        "SELECT files.filename FROM exif INNER JOIN files"
                        + " ON exif.id_files = files.id"
                        + " WHERE exif.id_exif_recording_equipment IS NULL"
                        + " UNION SELECT files.filename FROM files "
                        + getUnjoinedFilesSqlWhere("exif"));
        NULL_SQL_OF.put(
            "exif_lens",
            "SELECT files.filename FROM exif INNER JOIN files"
            + " ON exif.id_files = files.id WHERE exif.id_exif_lens IS NULL"
            + " UNION SELECT files.filename FROM files "
            + getUnjoinedFilesSqlWhere("exif"));
        NULL_SQL_OF.put(
            "dc_creator",
            "SELECT files.filename FROM xmp INNER JOIN files"
            + " ON xmp.id_files = files.id WHERE xmp.id_dc_creator IS NULL"
            + " UNION SELECT files.filename FROM files "
            + getUnjoinedFilesSqlWhere("xmp"));
        NULL_SQL_OF.put(
            "dc_rights",
            "SELECT files.filename FROM xmp INNER JOIN files"
            + " ON xmp.id_files = files.id WHERE xmp.id_dc_rights IS NULL"
            + " UNION SELECT files.filename FROM files "
            + getUnjoinedFilesSqlWhere("xmp"));
        NULL_SQL_OF.put("iptc4xmpcore_location",
                        "SELECT files.filename FROM xmp INNER JOIN files"
                        + " ON xmp.id_files = files.id"
                        + " WHERE xmp.id_iptc4xmpcore_location IS NULL"
                        + " UNION SELECT files.filename FROM files "
                        + getUnjoinedFilesSqlWhere("xmp"));
        NULL_SQL_OF.put("photoshop_authorsposition",
                        "SELECT files.filename FROM xmp INNER JOIN files"
                        + " ON xmp.id_files = files.id"
                        + " WHERE xmp.id_photoshop_authorsposition IS NULL"
                        + " UNION SELECT files.filename FROM files "
                        + getUnjoinedFilesSqlWhere("xmp"));
        NULL_SQL_OF.put("photoshop_captionwriter",
                        "SELECT files.filename FROM xmp INNER JOIN files"
                        + " ON xmp.id_files = files.id"
                        + " WHERE xmp.id_photoshop_captionwriter IS NULL"
                        + " UNION SELECT files.filename FROM files "
                        + getUnjoinedFilesSqlWhere("xmp"));
        NULL_SQL_OF.put("photoshop_city",
                        "SELECT files.filename FROM xmp INNER JOIN files"
                        + " ON xmp.id_files = files.id"
                        + " WHERE xmp.id_photoshop_city IS NULL"
                        + " UNION SELECT files.filename FROM files "
                        + getUnjoinedFilesSqlWhere("xmp"));
        NULL_SQL_OF.put("photoshop_country",
                        "SELECT files.filename FROM xmp INNER JOIN files"
                        + " ON xmp.id_files = files.id"
                        + " WHERE xmp.id_photoshop_country IS NULL"
                        + " UNION SELECT files.filename FROM files "
                        + getUnjoinedFilesSqlWhere("xmp"));
        NULL_SQL_OF.put("photoshop_credit",
                        "SELECT files.filename FROM xmp INNER JOIN files"
                        + " ON xmp.id_files = files.id"
                        + " WHERE xmp.id_photoshop_credit IS NULL"
                        + " UNION SELECT files.filename FROM files "
                        + getUnjoinedFilesSqlWhere("xmp"));
        NULL_SQL_OF.put("photoshop_source",
                        "SELECT files.filename FROM xmp INNER JOIN files"
                        + " ON xmp.id_files = files.id"
                        + " WHERE xmp.id_photoshop_source IS NULL"
                        + " UNION SELECT files.filename FROM files "
                        + getUnjoinedFilesSqlWhere("xmp"));
        NULL_SQL_OF.put("photoshop_state",
                        "SELECT files.filename FROM xmp INNER JOIN files"
                        + " ON xmp.id_files = files.id"
                        + " WHERE xmp.id_photoshop_state IS NULL"
                        + " UNION SELECT files.filename FROM files "
                        + getUnjoinedFilesSqlWhere("xmp"));
        NULL_SQL_OF.put("dc_subjects",
                        "SELECT files.filename FROM files"
                        + " INNER JOIN xmp ON files.id = xmp.id_files"
                        + " WHERE xmp.id NOT IN"
                        + " (SELECT id_xmp FROM xmp_dc_subject)"
                        + " UNION SELECT files.filename FROM files "
                        + getUnjoinedFilesSqlWhere("xmp"));
        NOT_NULL_SQL_OF.put(
            "exif_recording_equipment",
            "SELECT files.filename FROM exif INNER JOIN files"
            + " ON exif.id_files = files.id"
            + " WHERE exif.id_exif_recording_equipment IS NOT NULL");
        NOT_NULL_SQL_OF.put("exif_lens",
                            "SELECT files.filename FROM exif INNER JOIN files"
                            + " ON exif.id_files = files.id"
                            + " WHERE exif.id_exif_lens IS NOT NULL");
        NOT_NULL_SQL_OF.put("dc_creator",
                            "SELECT files.filename FROM xmp INNER JOIN files"
                            + " ON xmp.id_files = files.id"
                            + " WHERE xmp.id_dc_creator IS NOT NULL");
        NOT_NULL_SQL_OF.put("dc_rights",
                            "SELECT files.filename FROM xmp INNER JOIN files"
                            + " ON xmp.id_files = files.id"
                            + " WHERE xmp.id_dc_rights IS NOT NULL");
        NOT_NULL_SQL_OF.put(
            "iptc4xmpcore_location",
            "SELECT files.filename FROM xmp INNER JOIN files"
            + " ON xmp.id_files = files.id"
            + " WHERE xmp.id_iptc4xmpcore_location IS NOT NULL");
        NOT_NULL_SQL_OF.put(
            "photoshop_authorsposition",
            "SELECT files.filename FROM xmp INNER JOIN files"
            + " ON xmp.id_files = files.id"
            + " WHERE xmp.id_photoshop_authorsposition IS NOT NULL");
        NOT_NULL_SQL_OF.put(
            "photoshop_captionwriter",
            "SELECT files.filename FROM xmp INNER JOIN files"
            + " ON xmp.id_files = files.id"
            + " WHERE xmp.id_photoshop_captionwriter IS NOT NULL");
        NOT_NULL_SQL_OF.put("photoshop_city",
                            "SELECT files.filename FROM xmp INNER JOIN files"
                            + " ON xmp.id_files = files.id"
                            + " WHERE xmp.id_photoshop_city IS NOT NULL");
        NOT_NULL_SQL_OF.put("photoshop_country",
                            "SELECT files.filename FROM xmp INNER JOIN files"
                            + " ON xmp.id_files = files.id"
                            + " WHERE xmp.id_photoshop_country IS NOT NULL");
        NOT_NULL_SQL_OF.put("photoshop_credit",
                            "SELECT files.filename FROM xmp INNER JOIN files"
                            + " ON xmp.id_files = files.id"
                            + " WHERE xmp.id_photoshop_credit IS NOT NULL");
        NOT_NULL_SQL_OF.put("photoshop_source",
                            "SELECT files.filename FROM xmp INNER JOIN files"
                            + " ON xmp.id_files = files.id"
                            + " WHERE xmp.id_photoshop_source IS NOT NULL");
        NOT_NULL_SQL_OF.put("photoshop_state",
                            "SELECT files.filename FROM xmp INNER JOIN files"
                            + " ON xmp.id_files = files.id"
                            + " WHERE xmp.id_photoshop_state IS NOT NULL");
        NOT_NULL_SQL_OF.put("dc_subjects",
                            "SELECT files.filename FROM files"
                            + " INNER JOIN xmp ON files.id = xmp.id_files"
                            + " WHERE xmp.id IN"
                            + " (SELECT id_xmp FROM xmp_dc_subject)");
        DELETE_SQL_OF.put("dc_creator",
                          "DELETE FROM dc_creator WHERE creator = ?");
        DELETE_SQL_OF.put("dc_rights",
                          "DELETE FROM dc_rights WHERE rights = ?");
        DELETE_SQL_OF.put(
            "iptc4xmpcore_location",
            "DELETE FROM iptc4xmpcore_location WHERE location = ?");
        DELETE_SQL_OF.put("photoshop_authorsposition",
                          "DELETE FROM photoshop_authorsposition"
                          + " WHERE authorsposition = ?");
        DELETE_SQL_OF.put(
            "photoshop_captionwriter",
            "DELETE FROM photoshop_captionwriter WHERE captionwriter = ?");
        DELETE_SQL_OF.put("photoshop_city",
                          "DELETE FROM photoshop_city WHERE city = ?");
        DELETE_SQL_OF.put("photoshop_country",
                          "DELETE FROM photoshop_country WHERE country = ?");
        DELETE_SQL_OF.put("photoshop_credit",
                          "DELETE FROM photoshop_credit WHERE credit = ?");
        DELETE_SQL_OF.put("photoshop_source",
                          "DELETE FROM photoshop_source WHERE source = ?");
        DELETE_SQL_OF.put("photoshop_state",
                          "DELETE FROM photoshop_state WHERE state = ?");
    }

    private Join() {}

    /**
     * Type of a SQL join.
     */
    public enum Type {
        INNER("INNER"), LEFT("LEFT");

        private final String string;

        private Type(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    public static String getUnjoinedFilesSqlWhere(String tablename) {
        assert tablename.equals("exif") || tablename.equals("xmp") : tablename;

        return "WHERE files.id NOT IN ("
               + "SELECT files.id FROM files INNER JOIN " + tablename + " ON "
               + tablename + ".id_files = files.id)";
    }

    public static String getDeleteSql(String tablename) {
        if (tablename == null) {
            throw new NullPointerException("tablename == null");
        }

        if (!DELETE_SQL_OF.containsKey(tablename)) {
            throw new IllegalArgumentException("Table not defined: "
                                               + tablename);
        }

        return DELETE_SQL_OF.get(tablename);
    }

    public static String getNotNullSqlOf(String joinTablename) {
        if (joinTablename == null) {
            throw new NullPointerException("joinTablename == null");
        }

        if (!NOT_NULL_SQL_OF.containsKey(joinTablename)) {
            throw new IllegalArgumentException("Table not defined: "
                                               + joinTablename);
        }

        return NOT_NULL_SQL_OF.get(joinTablename);
    }

    public static String getNullSqlOf(String joinTablename) {
        if (joinTablename == null) {
            throw new NullPointerException("joinTablename == null");
        }

        if (!NULL_SQL_OF.containsKey(joinTablename)) {
            throw new IllegalArgumentException("Table not defined: "
                                               + joinTablename);
        }

        return NULL_SQL_OF.get(joinTablename);
    }

    public static String getJoinToFiles(String tablename, Type type) {
        if (tablename == null) {
            throw new NullPointerException("tablename == null");
        }

        if (!JOIN_FROM_FILES.containsKey(tablename)) {
            throw new IllegalArgumentException("Unkown table: " + tablename);
        }

        return JOIN_FROM_FILES.get(tablename).replace("\\JOIN_TYPE\\",
                                   type.string);
    }
}
