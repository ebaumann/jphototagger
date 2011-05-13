package org.jphototagger.program.database;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.data.MetadataTemplate;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcCreator;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcDescription;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcRights;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcTitle;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpIptc4XmpCoreDateCreated;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopAuthorsposition;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCaptionwriter;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCity;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopInstructions;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopSource;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopState;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopTransmissionReference;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpRating;
import org.jphototagger.program.event.listener.DatabaseMetadataTemplatesListener;
import org.jphototagger.program.event.listener.impl.ListenerSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class DatabaseMetadataTemplates extends Database {
    private static final String DELIM_REPEATABLE_STRINGS = "\t";
    public static final DatabaseMetadataTemplates INSTANCE = new DatabaseMetadataTemplates();
    private final ListenerSupport<DatabaseMetadataTemplatesListener> ls =
        new ListenerSupport<DatabaseMetadataTemplatesListener>();

    private DatabaseMetadataTemplates() {}

    /**
     * Fügt ein neues Metadaten-Edit-Template ein. Existiert das Template
     * bereits, werden seine Daten aktualisiert.
     *
     * @param  template  Template
     * @return           true bei Erfolg
     */
    public boolean insertOrUpdate(MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        if (exists(template.getName())) {
            return update(template);
        }

        boolean inserted = false;
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);

            String sql = "INSERT INTO metadata_edit_templates ("
                         + "name"    // --  1 --
                         + ", dcSubjects"    // --  2 --
                         + ", dcTitle"    // --  3 --
                         + ", photoshopHeadline"    // --  4 --
                         + ", dcDescription"    // --  5 --
                         + ", photoshopCaptionwriter"    // --  6 --
                         + ", iptc4xmpcoreLocation"    // --  7 --
                         + ", dcRights"    // --  8 --
                         + ", dcCreator"    // --  9 --
                         + ", photoshopAuthorsposition"    // -- 10 --
                         + ", photoshopCity"    // -- 11 --
                         + ", photoshopState"    // -- 12 --
                         + ", photoshopCountry"    // -- 13 --
                         + ", photoshopTransmissionReference"    // -- 14 --
                         + ", photoshopInstructions"    // -- 15 --
                         + ", photoshopCredit"    // -- 16 --
                         + ", photoshopSource"    // -- 17 --
                         + ", rating"    // -- 18 --
                         + ", iptc4xmpcore_datecreated"    // -- 19 --
                         + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?" + ", ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            stmt = con.prepareStatement(sql);
            set(stmt, template);
            logFiner(stmt);
            stmt.executeUpdate();
            con.commit();
            inserted = true;
            notifyInserted(template);
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseMetadataTemplates.class, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return inserted;
    }

    @SuppressWarnings("unchecked")
    private void set(PreparedStatement stmt, MetadataTemplate template) throws SQLException {
        stmt.setString(1, template.getName());
        stmt.setBytes(2, (template.getValueOfColumn(ColumnXmpDcSubjectsSubject.INSTANCE) == null)
                         ? null
                         : fromRepeatable(
                             (Collection<String>) template.getValueOfColumn(
                                 ColumnXmpDcSubjectsSubject.INSTANCE)).getBytes());
        stmt.setBytes(3, (template.getValueOfColumn(ColumnXmpDcTitle.INSTANCE) == null)
                         ? null
                         : ((String) template.getValueOfColumn(ColumnXmpDcTitle.INSTANCE)).getBytes());
        stmt.setBytes(4, (template.getValueOfColumn(ColumnXmpPhotoshopHeadline.INSTANCE) == null)
                         ? null
                         : ((String) template.getValueOfColumn(ColumnXmpPhotoshopHeadline.INSTANCE)).getBytes());
        stmt.setBytes(5, (template.getValueOfColumn(ColumnXmpDcDescription.INSTANCE) == null)
                         ? null
                         : ((String) template.getValueOfColumn(ColumnXmpDcDescription.INSTANCE)).getBytes());
        stmt.setBytes(6, (template.getValueOfColumn(ColumnXmpPhotoshopCaptionwriter.INSTANCE) == null)
                         ? null
                         : ((String) template.getValueOfColumn(ColumnXmpPhotoshopCaptionwriter.INSTANCE)).getBytes());
        stmt.setBytes(7, (template.getValueOfColumn(ColumnXmpIptc4xmpcoreLocation.INSTANCE) == null)
                         ? null
                         : ((String) template.getValueOfColumn(ColumnXmpIptc4xmpcoreLocation.INSTANCE)).getBytes());
        stmt.setBytes(8, (template.getValueOfColumn(ColumnXmpDcRights.INSTANCE) == null)
                         ? null
                         : ((String) template.getValueOfColumn(ColumnXmpDcRights.INSTANCE)).getBytes());
        stmt.setBytes(9, (template.getValueOfColumn(ColumnXmpDcCreator.INSTANCE) == null)
                         ? null
                         : ((String) template.getValueOfColumn(ColumnXmpDcCreator.INSTANCE)).getBytes());
        stmt.setBytes(10, (template.getValueOfColumn(ColumnXmpPhotoshopAuthorsposition.INSTANCE) == null)
                          ? null
                          : ((String) template.getValueOfColumn(
                              ColumnXmpPhotoshopAuthorsposition.INSTANCE)).getBytes());
        stmt.setBytes(11, (template.getValueOfColumn(ColumnXmpPhotoshopCity.INSTANCE) == null)
                          ? null
                          : ((String) template.getValueOfColumn(ColumnXmpPhotoshopCity.INSTANCE)).getBytes());
        stmt.setBytes(12, (template.getValueOfColumn(ColumnXmpPhotoshopState.INSTANCE) == null)
                          ? null
                          : ((String) template.getValueOfColumn(ColumnXmpPhotoshopState.INSTANCE)).getBytes());
        stmt.setBytes(13, (template.getValueOfColumn(ColumnXmpPhotoshopCountry.INSTANCE) == null)
                          ? null
                          : ((String) template.getValueOfColumn(ColumnXmpPhotoshopCountry.INSTANCE)).getBytes());
        stmt.setBytes(14, (template.getValueOfColumn(ColumnXmpPhotoshopTransmissionReference.INSTANCE) == null)
                          ? null
                          : ((String) template.getValueOfColumn(
                              ColumnXmpPhotoshopTransmissionReference.INSTANCE)).getBytes());
        stmt.setBytes(15, (template.getValueOfColumn(ColumnXmpPhotoshopInstructions.INSTANCE) == null)
                          ? null
                          : ((String) template.getValueOfColumn(ColumnXmpPhotoshopInstructions.INSTANCE)).getBytes());
        stmt.setBytes(16, (template.getValueOfColumn(ColumnXmpPhotoshopCredit.INSTANCE) == null)
                          ? null
                          : ((String) template.getValueOfColumn(ColumnXmpPhotoshopCredit.INSTANCE)).getBytes());
        stmt.setBytes(17, (template.getValueOfColumn(ColumnXmpPhotoshopSource.INSTANCE) == null)
                          ? null
                          : ((String) template.getValueOfColumn(ColumnXmpPhotoshopSource.INSTANCE)).getBytes());
        stmt.setBytes(18, (template.getValueOfColumn(ColumnXmpRating.INSTANCE) == null)
                          ? null
                          : ((String) template.getValueOfColumn(ColumnXmpRating.INSTANCE)).getBytes());
        stmt.setBytes(19, (template.getValueOfColumn(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE) == null)
                          ? null
                          : ((String) template.getValueOfColumn(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE)).getBytes());
    }

    private String fromRepeatable(Collection<String> strings) {
        StringBuilder sb = new StringBuilder();
        int index = 0;

        for (String string : strings) {
            sb.append((index++ == 0)
                      ? ""
                      : DELIM_REPEATABLE_STRINGS);
            sb.append(string);
        }

        return sb.toString();
    }

    private List<String> toRepeatable(String string) {
        List<String> strings = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(string, DELIM_REPEATABLE_STRINGS);

        while (tokenizer.hasMoreTokens()) {
            strings.add(tokenizer.nextToken());
        }

        return strings;
    }

    /**
     * Returns a template with a specific name.
     *
     * @param  name template name
     * @return      template or null if no template has that name or on errors
     */
    public MetadataTemplate find(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }

        MetadataTemplate template = null;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            String sql = getSelectForSetValues() + " WHERE name = ?";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, name);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                template = new MetadataTemplate();
                setValues(template, rs);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseMetadataTemplates.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return template;
    }

    /**
     * Liefert alle Metadaten-Edit-Templates.
     *
     * @return Templates
     */
    public List<MetadataTemplate> getAll() {
        List<MetadataTemplate> templates = new ArrayList<MetadataTemplate>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            stmt = con.createStatement();

            String sql = getSelectForSetValues() + " WHERE name IS NOT NULL";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                MetadataTemplate template = new MetadataTemplate();

                setValues(template, rs);
                templates.add(template);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseMetadataTemplates.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return templates;
    }

    private String getSelectForSetValues() {
        return "SELECT name"    // --  1 --
               + ", dcSubjects"    // --  2 --
               + ", dcTitle"    // --  3 --
               + ", photoshopHeadline"    // --  4 --
               + ", dcDescription"    // --  5 --
               + ", photoshopCaptionwriter"    // --  6 --
               + ", iptc4xmpcoreLocation"    // --  7 --
               + ", dcRights"    // --  8 --
               + ", dcCreator"    // --  9 --
               + ", photoshopAuthorsposition"    // -- 10 --
               + ", photoshopCity"    // -- 11 --
               + ", photoshopState"    // -- 12 --
               + ", photoshopCountry"    // -- 13 --
               + ", photoshopTransmissionReference"    // -- 14 --
               + ", photoshopInstructions"    // -- 15 --
               + ", photoshopCredit"    // -- 16 --
               + ", photoshopSource"    // -- 17 --
               + ", rating"    // -- 18 --
               + ", iptc4xmpcore_datecreated"    // -- 19 --
               + " FROM metadata_edit_templates"
        ;
    }

    private void setValues(MetadataTemplate template, ResultSet rs) throws SQLException {
        byte[] bytes;

        template.setName(rs.getString(1));
        bytes = rs.getBytes(2);
        template.setValueOfColumn(ColumnXmpDcSubjectsSubject.INSTANCE, rs.wasNull()
                ? null
                : toRepeatable(new String(bytes)));
        bytes = rs.getBytes(3);
        template.setValueOfColumn(ColumnXmpDcTitle.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(4);
        template.setValueOfColumn(ColumnXmpPhotoshopHeadline.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(5);
        template.setValueOfColumn(ColumnXmpDcDescription.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(6);
        template.setValueOfColumn(ColumnXmpPhotoshopCaptionwriter.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(7);
        template.setValueOfColumn(ColumnXmpIptc4xmpcoreLocation.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(8);
        template.setValueOfColumn(ColumnXmpDcRights.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(9);
        template.setValueOfColumn(ColumnXmpDcCreator.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(10);
        template.setValueOfColumn(ColumnXmpPhotoshopAuthorsposition.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(11);
        template.setValueOfColumn(ColumnXmpPhotoshopCity.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(12);
        template.setValueOfColumn(ColumnXmpPhotoshopState.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(13);
        template.setValueOfColumn(ColumnXmpPhotoshopCountry.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(14);
        template.setValueOfColumn(ColumnXmpPhotoshopTransmissionReference.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(15);
        template.setValueOfColumn(ColumnXmpPhotoshopInstructions.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(16);
        template.setValueOfColumn(ColumnXmpPhotoshopCredit.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(17);
        template.setValueOfColumn(ColumnXmpPhotoshopSource.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(18);
        template.setValueOfColumn(ColumnXmpRating.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(19);
        template.setValueOfColumn(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
    }

    /**
     * Aktualisiert ein Metadaten-Edit-Template.
     *
     * @param  template  Template
     * @return true bei Erfolg
     */
    public boolean update(MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        boolean updated = false;
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            String sql = "UPDATE metadata_edit_templates" + " SET name = ?"    // --  1 --
                         + ", dcSubjects = ?"    // --  2 --
                         + ", dcTitle = ?"    // --  3 --
                         + ", photoshopHeadline = ?"    // --  4 --
                         + ", dcDescription = ?"    // --  5 --
                         + ", photoshopCaptionwriter = ?"    // --  6 --
                         + ", iptc4xmpcoreLocation = ?"    // --  7 --
                         + ", dcRights = ?"    // --  8 --
                         + ", dcCreator = ?"    // --  9 --
                         + ", photoshopAuthorsposition = ?"    // -- 10 --
                         + ", photoshopCity = ?"    // -- 11 --
                         + ", photoshopState = ?"    // -- 12 --
                         + ", photoshopCountry = ?"    // -- 13 --
                         + ", photoshopTransmissionReference = ?"    // -- 14 --
                         + ", photoshopInstructions = ?"    // -- 15 --
                         + ", photoshopCredit = ?"    // -- 16 --
                         + ", photoshopSource = ?"    // -- 17 --
                         + ", rating = ?"    // -- 18 --
                         + ", iptc4xmpcore_datecreated = ?"    // -- 19 --
                         + " WHERE name = ?";    // -- 20 --

            con = getConnection();
            con.setAutoCommit(false);

            MetadataTemplate oldTemplate = find(template.getName());

            stmt = con.prepareStatement(sql);
            set(stmt, template);
            stmt.setString(20, template.getName());
            logFiner(stmt);

            int count = stmt.executeUpdate();

            con.commit();
            updated = count > 0;

            if (updated) {
                notifyUpdated(oldTemplate, template);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseMetadataTemplates.class, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return updated;
    }

    public boolean updateRename(String fromName, String toName) {
        if (toName == null) {
            throw new NullPointerException("toName == null");
        }

        boolean renamed = false;
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("UPDATE metadata_edit_templates SET name = ? WHERE name = ?");
            stmt.setString(1, toName);
            stmt.setString(2, fromName);
            logFiner(stmt);

            int count = stmt.executeUpdate();

            con.commit();
            renamed = count > 0;

            if (renamed) {
                notifyRenamed(fromName, toName);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseMetadataTemplates.class, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return renamed;
    }

    /**
     * Löscht ein Metadaten-Edit-Template.
     *
     * @param  name  Name des Templates
     * @return true bei Erfolg
     */
    public boolean delete(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }

        boolean deleted = false;
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);

            MetadataTemplate template = find(name);

            stmt = con.prepareStatement("DELETE FROM metadata_edit_templates WHERE name = ?");
            stmt.setString(1, name);
            logFiner(stmt);

            int count = stmt.executeUpdate();

            con.commit();
            deleted = count > 0;

            if (deleted) {
                notifyDelted(template);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseMetadataTemplates.class, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return deleted;
    }

    public boolean exists(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }

        boolean exists = false;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            stmt = con.prepareStatement("SELECT COUNT(*) FROM metadata_edit_templates WHERE name = ?");
            stmt.setString(1, name);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseMetadataTemplates.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return exists;
    }

    public void addListener(DatabaseMetadataTemplatesListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.add(listener);
    }

    public void removeListener(DatabaseMetadataTemplatesListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.remove(listener);
    }

    private void notifyDelted(MetadataTemplate template) {
        for (DatabaseMetadataTemplatesListener listener : ls.get()) {
            listener.templateDeleted(template);
        }
    }

    private void notifyInserted(MetadataTemplate template) {
        for (DatabaseMetadataTemplatesListener listener : ls.get()) {
            listener.templateInserted(template);
        }
    }

    private void notifyUpdated(MetadataTemplate oldTemplate, MetadataTemplate updatedTemplate) {
        for (DatabaseMetadataTemplatesListener listener : ls.get()) {
            listener.templateUpdated(oldTemplate, updatedTemplate);
        }
    }

    private void notifyRenamed(String fromName, String toName) {
        for (DatabaseMetadataTemplatesListener listener : ls.get()) {
            listener.templateRenamed(fromName, toName);
        }
    }
}
