package org.jphototagger.program.database.metadata;

import java.util.HashMap;
import java.util.Map;

/**
 * SQL-Joins.
 *
 * @author Elmar Baumann
 */
public final class Join {
    private static final Map<String, String> JOIN_FROM_FILES = new HashMap<String, String>();
    private static final Map<String, String> NULL_SQL_OF = new HashMap<String, String>();
    private static final Map<String, String> NOT_NULL_SQL_OF = new HashMap<String, String>();
    private static final Map<String, String> DELETE_SQL_OF = new HashMap<String, String>();

    static {
        JOIN_FROM_FILES.put("files", "");
        JOIN_FROM_FILES.put("dc_subjects",
                            " \\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_file"
                            + " INNER JOIN xmp_dc_subject ON xmp.id = xmp_dc_subject.id_xmp"
                            + " INNER JOIN dc_subjects" + " ON xmp_dc_subject.id_dc_subject = dc_subjects.id");
        JOIN_FROM_FILES.put("exif", " \\JOIN_TYPE\\ JOIN exif" + " ON files.id = exif.id_file");
        JOIN_FROM_FILES.put("xmp", " \\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_file");
        JOIN_FROM_FILES.put("exif_recording_equipment",
                            " \\JOIN_TYPE\\ JOIN exif" + " ON files.id = exif.id_file"
                            + " INNER JOIN exif_recording_equipment" + " ON exif.id_exif_recording_equipment"
                            + " = exif_recording_equipment.id");
        JOIN_FROM_FILES.put("exif_lenses",
                            " \\JOIN_TYPE\\ JOIN exif" + " ON files.id = exif.id_file" + " INNER JOIN exif_lenses"
                            + " ON exif.id_exif_lens = exif_lenses.id");
        JOIN_FROM_FILES.put("dc_creators",
                            " \\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_file" + " INNER JOIN dc_creators"
                            + " ON xmp.id_dc_creator = dc_creators.id");
        JOIN_FROM_FILES.put("dc_rights",
                            " \\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_file" + " INNER JOIN dc_rights"
                            + " ON xmp.id_dc_rights = dc_rights.id");
        JOIN_FROM_FILES.put("iptc4xmpcore_locations",
                            " \\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_file" + " INNER JOIN iptc4xmpcore_locations"
                            + " ON xmp.id_iptc4xmpcore_location" + " = iptc4xmpcore_locations.id");
        JOIN_FROM_FILES.put("photoshop_authorspositions",
                            " \\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_file"
                            + " INNER JOIN photoshop_authorspositions" + " ON xmp.id_photoshop_authorsposition"
                            + " = photoshop_authorspositions.id");
        JOIN_FROM_FILES.put("photoshop_captionwriters",
                            " \\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_file"
                            + " INNER JOIN photoshop_captionwriters" + " ON xmp.id_photoshop_captionwriter"
                            + " = photoshop_captionwriters.id");
        JOIN_FROM_FILES.put("photoshop_cities",
                            " \\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_file" + " INNER JOIN photoshop_cities"
                            + " ON xmp.id_photoshop_city = photoshop_cities.id");
        JOIN_FROM_FILES.put("photoshop_countries",
                            " \\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_file" + " INNER JOIN photoshop_countries"
                            + " ON xmp.id_photoshop_country" + " = photoshop_countries.id");
        JOIN_FROM_FILES.put("photoshop_credits",
                            " \\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_file" + " INNER JOIN photoshop_credits"
                            + " ON xmp.id_photoshop_credit" + " = photoshop_credits.id");
        JOIN_FROM_FILES.put("photoshop_sources",
                            " \\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_file" + " INNER JOIN photoshop_sources"
                            + " ON xmp.id_photoshop_source" + " = photoshop_sources.id");
        JOIN_FROM_FILES.put("photoshop_states",
                            " \\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_file" + " INNER JOIN photoshop_states"
                            + " ON xmp.id_photoshop_state = photoshop_states.id");
        NULL_SQL_OF.put("exif_recording_equipment",
                        "SELECT files.filename FROM exif INNER JOIN files" + " ON exif.id_file = files.id"
                        + " WHERE exif.id_exif_recording_equipment IS NULL"
                        + " UNION SELECT files.filename FROM files " + getUnjoinedFilesSqlWhere("exif"));
        NULL_SQL_OF.put("exif_lenses",
                        "SELECT files.filename FROM exif INNER JOIN files"
                        + " ON exif.id_file = files.id WHERE exif.id_exif_lens IS NULL"
                        + " UNION SELECT files.filename FROM files " + getUnjoinedFilesSqlWhere("exif"));
        NULL_SQL_OF.put("dc_creators",
                        "SELECT files.filename FROM xmp INNER JOIN files"
                        + " ON xmp.id_file = files.id WHERE xmp.id_dc_creator IS NULL"
                        + " UNION SELECT files.filename FROM files " + getUnjoinedFilesSqlWhere("xmp"));
        NULL_SQL_OF.put("dc_rights",
                        "SELECT files.filename FROM xmp INNER JOIN files"
                        + " ON xmp.id_file = files.id WHERE xmp.id_dc_rights IS NULL"
                        + " UNION SELECT files.filename FROM files " + getUnjoinedFilesSqlWhere("xmp"));
        NULL_SQL_OF.put("iptc4xmpcore_locations",
                        "SELECT files.filename FROM xmp INNER JOIN files" + " ON xmp.id_file = files.id"
                        + " WHERE xmp.id_iptc4xmpcore_location IS NULL" + " UNION SELECT files.filename FROM files "
                        + getUnjoinedFilesSqlWhere("xmp"));
        NULL_SQL_OF.put("photoshop_authorspositions",
                        "SELECT files.filename FROM xmp INNER JOIN files" + " ON xmp.id_file = files.id"
                        + " WHERE xmp.id_photoshop_authorsposition IS NULL"
                        + " UNION SELECT files.filename FROM files " + getUnjoinedFilesSqlWhere("xmp"));
        NULL_SQL_OF.put("photoshop_captionwriters",
                        "SELECT files.filename FROM xmp INNER JOIN files" + " ON xmp.id_file = files.id"
                        + " WHERE xmp.id_photoshop_captionwriter IS NULL" + " UNION SELECT files.filename FROM files "
                        + getUnjoinedFilesSqlWhere("xmp"));
        NULL_SQL_OF.put("photoshop_cities",
                        "SELECT files.filename FROM xmp INNER JOIN files" + " ON xmp.id_file = files.id"
                        + " WHERE xmp.id_photoshop_city IS NULL" + " UNION SELECT files.filename FROM files "
                        + getUnjoinedFilesSqlWhere("xmp"));
        NULL_SQL_OF.put("photoshop_countries",
                        "SELECT files.filename FROM xmp INNER JOIN files" + " ON xmp.id_file = files.id"
                        + " WHERE xmp.id_photoshop_country IS NULL" + " UNION SELECT files.filename FROM files "
                        + getUnjoinedFilesSqlWhere("xmp"));
        NULL_SQL_OF.put("photoshop_credits",
                        "SELECT files.filename FROM xmp INNER JOIN files" + " ON xmp.id_file = files.id"
                        + " WHERE xmp.id_photoshop_credit IS NULL" + " UNION SELECT files.filename FROM files "
                        + getUnjoinedFilesSqlWhere("xmp"));
        NULL_SQL_OF.put("photoshop_sources",
                        "SELECT files.filename FROM xmp INNER JOIN files" + " ON xmp.id_file = files.id"
                        + " WHERE xmp.id_photoshop_source IS NULL" + " UNION SELECT files.filename FROM files "
                        + getUnjoinedFilesSqlWhere("xmp"));
        NULL_SQL_OF.put("photoshop_states",
                        "SELECT files.filename FROM xmp INNER JOIN files" + " ON xmp.id_file = files.id"
                        + " WHERE xmp.id_photoshop_state IS NULL" + " UNION SELECT files.filename FROM files "
                        + getUnjoinedFilesSqlWhere("xmp"));
        NULL_SQL_OF.put("dc_subjects",
                        "SELECT files.filename FROM files" + " INNER JOIN xmp ON files.id = xmp.id_file"
                        + " WHERE xmp.id NOT IN" + " (SELECT id_xmp FROM xmp_dc_subject)"
                        + " UNION SELECT files.filename FROM files " + getUnjoinedFilesSqlWhere("xmp"));
        NOT_NULL_SQL_OF.put("exif_recording_equipment",
                            "SELECT files.filename FROM exif INNER JOIN files" + " ON exif.id_file = files.id"
                            + " WHERE exif.id_exif_recording_equipment IS NOT NULL");
        NOT_NULL_SQL_OF.put("exif_lenses",
                            "SELECT files.filename FROM exif INNER JOIN files" + " ON exif.id_file = files.id"
                            + " WHERE exif.id_exif_lens IS NOT NULL");
        NOT_NULL_SQL_OF.put("dc_creators",
                            "SELECT files.filename FROM xmp INNER JOIN files" + " ON xmp.id_file = files.id"
                            + " WHERE xmp.id_dc_creator IS NOT NULL");
        NOT_NULL_SQL_OF.put("dc_rights",
                            "SELECT files.filename FROM xmp INNER JOIN files" + " ON xmp.id_file = files.id"
                            + " WHERE xmp.id_dc_rights IS NOT NULL");
        NOT_NULL_SQL_OF.put("iptc4xmpcore_locations",
                            "SELECT files.filename FROM xmp INNER JOIN files" + " ON xmp.id_file = files.id"
                            + " WHERE xmp.id_iptc4xmpcore_location IS NOT NULL");
        NOT_NULL_SQL_OF.put("photoshop_authorspositions",
                            "SELECT files.filename FROM xmp INNER JOIN files" + " ON xmp.id_file = files.id"
                            + " WHERE xmp.id_photoshop_authorsposition IS NOT NULL");
        NOT_NULL_SQL_OF.put("photoshop_captionwriters",
                            "SELECT files.filename FROM xmp INNER JOIN files" + " ON xmp.id_file = files.id"
                            + " WHERE xmp.id_photoshop_captionwriter IS NOT NULL");
        NOT_NULL_SQL_OF.put("photoshop_cities",
                            "SELECT files.filename FROM xmp INNER JOIN files" + " ON xmp.id_file = files.id"
                            + " WHERE xmp.id_photoshop_city IS NOT NULL");
        NOT_NULL_SQL_OF.put("photoshop_countries",
                            "SELECT files.filename FROM xmp INNER JOIN files" + " ON xmp.id_file = files.id"
                            + " WHERE xmp.id_photoshop_country IS NOT NULL");
        NOT_NULL_SQL_OF.put("photoshop_credits",
                            "SELECT files.filename FROM xmp INNER JOIN files" + " ON xmp.id_file = files.id"
                            + " WHERE xmp.id_photoshop_credit IS NOT NULL");
        NOT_NULL_SQL_OF.put("photoshop_sources",
                            "SELECT files.filename FROM xmp INNER JOIN files" + " ON xmp.id_file = files.id"
                            + " WHERE xmp.id_photoshop_source IS NOT NULL");
        NOT_NULL_SQL_OF.put("photoshop_states",
                            "SELECT files.filename FROM xmp INNER JOIN files" + " ON xmp.id_file = files.id"
                            + " WHERE xmp.id_photoshop_state IS NOT NULL");
        NOT_NULL_SQL_OF.put("dc_subjects",
                            "SELECT files.filename FROM files" + " INNER JOIN xmp ON files.id = xmp.id_file"
                            + " WHERE xmp.id IN" + " (SELECT id_xmp FROM xmp_dc_subject)");
        DELETE_SQL_OF.put("dc_creators", "DELETE FROM dc_creators WHERE creator = ?");
        DELETE_SQL_OF.put("dc_rights", "DELETE FROM dc_rights WHERE rights = ?");
        DELETE_SQL_OF.put("iptc4xmpcore_locations", "DELETE FROM iptc4xmpcore_locations WHERE location = ?");
        DELETE_SQL_OF.put("photoshop_authorspositions",
                          "DELETE FROM photoshop_authorspositions" + " WHERE authorsposition = ?");
        DELETE_SQL_OF.put("photoshop_captionwriters", "DELETE FROM photoshop_captionwriters WHERE captionwriter = ?");
        DELETE_SQL_OF.put("photoshop_cities", "DELETE FROM photoshop_cities WHERE city = ?");
        DELETE_SQL_OF.put("photoshop_countries", "DELETE FROM photoshop_countries WHERE country = ?");
        DELETE_SQL_OF.put("photoshop_credits", "DELETE FROM photoshop_credits WHERE credit = ?");
        DELETE_SQL_OF.put("photoshop_sources", "DELETE FROM photoshop_sources WHERE source = ?");
        DELETE_SQL_OF.put("photoshop_states", "DELETE FROM photoshop_states WHERE state = ?");
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
        if (tablename == null) {
            throw new NullPointerException("tablename == null");
        }

        assert tablename.equals("exif") || tablename.equals("xmp") : tablename;

        return "WHERE files.id NOT IN (" + "SELECT files.id FROM files INNER JOIN " + tablename + " ON " + tablename
               + ".id_file = files.id)";
    }

    public static String getDeleteSql(String tablename) {
        if (tablename == null) {
            throw new NullPointerException("tablename == null");
        }

        if (!DELETE_SQL_OF.containsKey(tablename)) {
            throw new IllegalArgumentException("Table not defined: " + tablename);
        }

        return DELETE_SQL_OF.get(tablename);
    }

    public static String getNotNullSqlOf(String joinTablename) {
        if (joinTablename == null) {
            throw new NullPointerException("joinTablename == null");
        }

        if (!NOT_NULL_SQL_OF.containsKey(joinTablename)) {
            throw new IllegalArgumentException("Table not defined: " + joinTablename);
        }

        return NOT_NULL_SQL_OF.get(joinTablename);
    }

    public static String getNullSqlOf(String joinTablename) {
        if (joinTablename == null) {
            throw new NullPointerException("joinTablename == null");
        }

        if (!NULL_SQL_OF.containsKey(joinTablename)) {
            throw new IllegalArgumentException("Table not defined: " + joinTablename);
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

        return JOIN_FROM_FILES.get(tablename).replace("\\JOIN_TYPE\\", type.string);
    }
}
