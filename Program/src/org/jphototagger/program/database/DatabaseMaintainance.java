package org.jphototagger.program.database;

import java.sql.Connection;
import java.sql.Statement;
import static java.text.MessageFormat.format;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.lib.util.Bundle;

import org.jphototagger.lib.dialog.MessageDisplayer;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class DatabaseMaintainance extends Database {

    public static final DatabaseMaintainance INSTANCE = new DatabaseMaintainance();
    private static final Logger LOGGER = Logger.getLogger(DatabaseMaintainance.class.getName());

    private DatabaseMaintainance() {}

    /**
     * Shuts down the database.
     */
    public void shutdown() {
        Connection con = null;
        Statement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.createStatement();
            LOGGER.log(Level.INFO, "Closing the database");
            stmt.executeUpdate("SHUTDOWN");
        } catch (Exception ex) {
            Logger.getLogger(DatabaseMaintainance.class.getName()).log(Level.SEVERE, null, ex);
            String message = Bundle.getString(DatabaseMaintainance.class, "DatabaseMaintainance.Error.Shutdown");

            MessageDisplayer.error(null, message);
        } finally {
            close(stmt);
        }
    }

    /**
     * Komprimiert die Datenbank.
     *
     * @return true, wenn die Datenbank erfolgreich komprimiert wurde
     */
    public boolean compressDatabase() {
        boolean success = false;
        Connection con = null;
        Statement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.createStatement();
            stmt.executeUpdate("CHECKPOINT DEFRAG");
            success = true;
        } catch (Exception ex) {
            Logger.getLogger(DatabaseMaintainance.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(stmt);
            free(con);
        }

        return success;
    }

    private static class Ref1nInfo {
        private final String table;
        private final String refTable;
        private final String refColumn;

        Ref1nInfo(String table, String refTable, String refColumn) {
            this.table = table;
            this.refColumn = refColumn;
            this.refTable = refTable;
        }

        public String getRefColumn() {
            return refColumn;
        }

        public String getRefTable() {
            return refTable;
        }

        public String getTable() {
            return table;
        }
    }


    private static final List<Ref1nInfo> REF_1_N_INFOS = new ArrayList<Ref1nInfo>();

    static {
        REF_1_N_INFOS.add(new Ref1nInfo("dc_creators", "xmp", "id_dc_creator"));
        REF_1_N_INFOS.add(new Ref1nInfo("dc_rights", "xmp", "id_dc_rights"));
        REF_1_N_INFOS.add(new Ref1nInfo("iptc4xmpcore_locations", "xmp", "id_iptc4xmpcore_location"));
        REF_1_N_INFOS.add(new Ref1nInfo("photoshop_authorspositions", "xmp", "id_photoshop_authorsposition"));
        REF_1_N_INFOS.add(new Ref1nInfo("photoshop_captionwriters", "xmp", "id_photoshop_captionwriter"));
        REF_1_N_INFOS.add(new Ref1nInfo("photoshop_cities", "xmp", "id_photoshop_city"));
        REF_1_N_INFOS.add(new Ref1nInfo("photoshop_countries", "xmp", "id_photoshop_country"));
        REF_1_N_INFOS.add(new Ref1nInfo("photoshop_credits", "xmp", "id_photoshop_credit"));
        REF_1_N_INFOS.add(new Ref1nInfo("photoshop_sources", "xmp", "id_photoshop_source"));
        REF_1_N_INFOS.add(new Ref1nInfo("photoshop_states", "xmp", "id_photoshop_state"));
        REF_1_N_INFOS.add(new Ref1nInfo("exif_recording_equipment", "exif", "id_exif_recording_equipment"));
        REF_1_N_INFOS.add(new Ref1nInfo("exif_lenses", "exif", "id_exif_lens"));
    }

    /**
     * Deletes from tables referenced 1:n all records that are not referenced
     * by another table.
     *
     * @return count of deleted records
     */
    public int deleteNotReferenced1n() {
        String sqlTemplate = "DELETE FROM {0} WHERE ID NOT IN (SELECT DISTINCT {1} from {2})";
        Connection con = null;
        Statement stmt = null;
        int deleted = 0;

        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.createStatement();

            for (Ref1nInfo info : REF_1_N_INFOS) {
                String sql = format(sqlTemplate, info.getTable(), info.getRefColumn(), info.getRefTable());

                logFiner(sql);
                deleted += stmt.executeUpdate(sql);
            }
        } catch (Exception ex) {
            Logger.getLogger(DatabaseMaintainance.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(stmt);
            free(con);
        }

        return deleted;
    }
}
