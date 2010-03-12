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

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.data.MetadataTemplate;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpIptc4XmpCoreDateCreated;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopAuthorsposition;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopCaptionwriter;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCity;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopInstructions;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopState;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopTransmissionReference;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpRating;
import de.elmar_baumann.jpt.event.DatabaseMetadataTemplatesEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseMetadataTemplatesListener;
import de.elmar_baumann.jpt.event.listener.impl.ListenerSupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2008-10-21
 */
public class DatabaseMetadataTemplates extends Database {
    private static final String                   DELIM_REPEATABLE_STRINGS =
        "\t";
    public static final DatabaseMetadataTemplates INSTANCE                 =
        new DatabaseMetadataTemplates();
    private final ListenerSupport<DatabaseMetadataTemplatesListener> listenerSupport =
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
        if (exists(template.getName())) {
            return update(template);
        }

        boolean           inserted   = false;
        Connection        connection = null;
        PreparedStatement stmt       = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            stmt = connection.prepareStatement(
                "INSERT INTO metadata_edit_templates" + " (name" +    // --  1 --
                ", dcSubjects" +    // --  2 --
                    ", dcTitle" +    // --  3 --
                        ", photoshopHeadline" +    // --  4 --
                            ", dcDescription" +    // --  5 --
                                ", photoshopCaptionwriter" +    // --  6 --
                                    ", iptc4xmpcoreLocation" +    // --  7 --
                                            ", dcRights" +    // --  8 --
                                            ", dcCreator" +    // -- 9 --
                                            ", photoshopAuthorsposition" +    // -- 10 --
                                            ", photoshopCity" +    // -- 11 --
                                            ", photoshopState" +    // -- 12 --
                                            ", photoshopCountry" +    // -- 13 --
                                            ", photoshopTransmissionReference" +    // -- 14 --
                                            ", photoshopInstructions" +       // -- 15 --
                                            ", photoshopCredit" +             // -- 16 --
                                            ", photoshopSource" +             // -- 17 --
                                            ", rating" +                      // -- 18 --
                                            ", iptc4xmpcore_datecreated" +    // -- 19 --
                                            ")" + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            set(stmt, template);
            logFiner(stmt);
            stmt.executeUpdate();
            connection.commit();
            inserted = true;
            notifyListeners(
                new DatabaseMetadataTemplatesEvent(
                    DatabaseMetadataTemplatesEvent.Type.ADDED, template, this));
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseMetadataTemplates.class, ex);
            rollback(connection);
        } finally {
            close(stmt);
            free(connection);
        }

        return inserted;
    }

    @SuppressWarnings("unchecked")
    private void set(PreparedStatement stmt, MetadataTemplate template)
            throws SQLException {
        stmt.setString(1, template.getName());
        stmt.setBytes(
            2, (template.getValueOfColumn(ColumnXmpDcSubjectsSubject.INSTANCE)
            == null)
               ? null
               : fromRepeatable(
                   (Collection<String>) template.getValueOfColumn(
                       ColumnXmpDcSubjectsSubject.INSTANCE)).getBytes());
        stmt.setBytes(3, (template.getValueOfColumn(ColumnXmpDcTitle.INSTANCE)
                      == null)
                         ? null
                         : ((String) template.getValueOfColumn(
                             ColumnXmpDcTitle.INSTANCE)).getBytes());
        stmt.setBytes(
            4, (template.getValueOfColumn(ColumnXmpPhotoshopHeadline.INSTANCE)
            == null)
               ? null
               : ((String) template.getValueOfColumn(
                   ColumnXmpPhotoshopHeadline.INSTANCE)).getBytes());
        stmt.setBytes(
            5, (template.getValueOfColumn(ColumnXmpDcDescription.INSTANCE)
            == null)
               ? null
               : ((String) template.getValueOfColumn(
                   ColumnXmpDcDescription.INSTANCE)).getBytes());
        stmt.setBytes(
            6, (template.getValueOfColumn(
                ColumnXmpPhotoshopCaptionwriter.INSTANCE) == null)
               ? null
               : ((String) template.getValueOfColumn(
                   ColumnXmpPhotoshopCaptionwriter.INSTANCE)).getBytes());
        stmt.setBytes(
            7, (template.getValueOfColumn(
                ColumnXmpIptc4xmpcoreLocation.INSTANCE) == null)
               ? null
               : ((String) template.getValueOfColumn(
                   ColumnXmpIptc4xmpcoreLocation.INSTANCE)).getBytes());
        stmt.setBytes(8, (template.getValueOfColumn(ColumnXmpDcRights.INSTANCE)
                      == null)
                         ? null
                         : ((String) template.getValueOfColumn(
                             ColumnXmpDcRights.INSTANCE)).getBytes());
        stmt.setBytes(9,
                      (template.getValueOfColumn(ColumnXmpDcCreator.INSTANCE)
                       == null)
                      ? null
                      : ((String) template.getValueOfColumn(
                          ColumnXmpDcCreator.INSTANCE)).getBytes());
        stmt.setBytes(
            10,
            (template.getValueOfColumn(
                ColumnXmpPhotoshopAuthorsposition.INSTANCE) == null)
            ? null
            : ((String) template.getValueOfColumn(
                ColumnXmpPhotoshopAuthorsposition.INSTANCE)).getBytes());
        stmt.setBytes(
            11,
            (template.getValueOfColumn(ColumnXmpPhotoshopCity.INSTANCE) == null)
            ? null
            : ((String) template.getValueOfColumn(
                ColumnXmpPhotoshopCity.INSTANCE)).getBytes());
        stmt.setBytes(
            12,
            (template.getValueOfColumn(ColumnXmpPhotoshopState.INSTANCE)
             == null)
            ? null
            : ((String) template.getValueOfColumn(
                ColumnXmpPhotoshopState.INSTANCE)).getBytes());
        stmt.setBytes(
            13,
            (template.getValueOfColumn(ColumnXmpPhotoshopCountry.INSTANCE)
             == null)
            ? null
            : ((String) template.getValueOfColumn(
                ColumnXmpPhotoshopCountry.INSTANCE)).getBytes());
        stmt.setBytes(
            14,
            (template.getValueOfColumn(
                ColumnXmpPhotoshopTransmissionReference.INSTANCE) == null)
            ? null
            : ((String) template.getValueOfColumn(
                ColumnXmpPhotoshopTransmissionReference.INSTANCE)).getBytes());
        stmt.setBytes(
            15,
            (template.getValueOfColumn(ColumnXmpPhotoshopInstructions.INSTANCE)
             == null)
            ? null
            : ((String) template.getValueOfColumn(
                ColumnXmpPhotoshopInstructions.INSTANCE)).getBytes());
        stmt.setBytes(
            16,
            (template.getValueOfColumn(ColumnXmpPhotoshopCredit.INSTANCE)
             == null)
            ? null
            : ((String) template.getValueOfColumn(
                ColumnXmpPhotoshopCredit.INSTANCE)).getBytes());
        stmt.setBytes(
            17,
            (template.getValueOfColumn(ColumnXmpPhotoshopSource.INSTANCE)
             == null)
            ? null
            : ((String) template.getValueOfColumn(
                ColumnXmpPhotoshopSource.INSTANCE)).getBytes());
        stmt.setBytes(18,
                      (template.getValueOfColumn(ColumnXmpRating.INSTANCE)
                       == null)
                      ? null
                      : ((String) template.getValueOfColumn(
                          ColumnXmpRating.INSTANCE)).getBytes());
        stmt.setBytes(
            19,
            (template.getValueOfColumn(
                ColumnXmpIptc4XmpCoreDateCreated.INSTANCE) == null)
            ? null
            : ((String) template.getValueOfColumn(
                ColumnXmpIptc4XmpCoreDateCreated.INSTANCE)).getBytes());
    }

    private String fromRepeatable(Collection<String> strings) {
        StringBuilder sb    = new StringBuilder();
        int           index = 0;

        for (String string : strings) {
            sb.append((index++ == 0)
                      ? ""
                      : DELIM_REPEATABLE_STRINGS);
            sb.append(string);
        }

        return sb.toString();
    }

    private List<String> toRepeatable(String string) {
        List<String>    strings   = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(string,
                                        DELIM_REPEATABLE_STRINGS);

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
        MetadataTemplate  template   = null;
        Connection        connection = null;
        PreparedStatement stmt       = null;
        ResultSet         rs         = null;

        try {
            connection = getConnection();

            String sql = getSelectForSetValues() + " WHERE name = ?";

            stmt = connection.prepareStatement(sql);
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
            free(connection);
        }

        return template;
    }

    /**
     * Liefert alle Metadaten-Edit-Templates.
     *
     * @return Templates
     */
    public List<MetadataTemplate> getAll() {
        List<MetadataTemplate> templates  = new ArrayList<MetadataTemplate>();
        Connection             connection = null;
        Statement              stmt       = null;
        ResultSet              rs         = null;

        try {
            connection = getConnection();
            stmt       = connection.createStatement();

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
            free(connection);
        }

        return templates;
    }

    private String getSelectForSetValues() {
        return "SELECT" + " name" +                                             // --  1 --
            ", dcSubjects" +                                                    // --  2 --
                ", dcTitle" +                                                   // --  3 --
                    ", photoshopHeadline" +                                     // --  4 --
                        ", dcDescription" +                                     // --  5 --
                            ", photoshopCaptionwriter" +                        // --  6 --
                                ", iptc4xmpcoreLocation" +                      // --  7 --
                                        ", dcRights" +                          // --  8 --
                                        ", dcCreator" +                         // --  9 --
                                        ", photoshopAuthorsposition" +          // -- 10 --
                                        ", photoshopCity" +                     // -- 11 --
                                        ", photoshopState" +                    // -- 12 --
                                        ", photoshopCountry" +                  // -- 13 --
                                        ", photoshopTransmissionReference" +    // -- 14 --
                                        ", photoshopInstructions" +             // -- 15 --
                                        ", photoshopCredit" +                   // -- 16 --
                                        ", photoshopSource" +                   // -- 17 --
                                        ", rating" +                            // -- 18 --
                                        ", iptc4xmpcore_datecreated" +          // -- 19 --
                                        " FROM metadata_edit_templates"
        ;
    }

    private void setValues(MetadataTemplate template, ResultSet rs)
            throws SQLException {
        byte[] bytes;

        template.setName(rs.getString(1));
        bytes = rs.getBytes(2);
        template.setValueOfColumn(ColumnXmpDcSubjectsSubject.INSTANCE,
                                  rs.wasNull()
                                  ? null
                                  : toRepeatable(new String(bytes)));
        bytes = rs.getBytes(3);
        template.setValueOfColumn(ColumnXmpDcTitle.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(4);
        template.setValueOfColumn(ColumnXmpPhotoshopHeadline.INSTANCE,
                                  rs.wasNull()
                                  ? null
                                  : new String(bytes));
        bytes = rs.getBytes(5);
        template.setValueOfColumn(ColumnXmpDcDescription.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(6);
        template.setValueOfColumn(ColumnXmpPhotoshopCaptionwriter.INSTANCE,
                                  rs.wasNull()
                                  ? null
                                  : new String(bytes));
        bytes = rs.getBytes(7);
        template.setValueOfColumn(ColumnXmpIptc4xmpcoreLocation.INSTANCE,
                                  rs.wasNull()
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
        template.setValueOfColumn(ColumnXmpPhotoshopAuthorsposition.INSTANCE,
                                  rs.wasNull()
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
        template.setValueOfColumn(ColumnXmpPhotoshopCountry.INSTANCE,
                                  rs.wasNull()
                                  ? null
                                  : new String(bytes));
        bytes = rs.getBytes(14);
        template.setValueOfColumn(
            ColumnXmpPhotoshopTransmissionReference.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(15);
        template.setValueOfColumn(ColumnXmpPhotoshopInstructions.INSTANCE,
                                  rs.wasNull()
                                  ? null
                                  : new String(bytes));
        bytes = rs.getBytes(16);
        template.setValueOfColumn(ColumnXmpPhotoshopCredit.INSTANCE,
                                  rs.wasNull()
                                  ? null
                                  : new String(bytes));
        bytes = rs.getBytes(17);
        template.setValueOfColumn(ColumnXmpPhotoshopSource.INSTANCE,
                                  rs.wasNull()
                                  ? null
                                  : new String(bytes));
        bytes = rs.getBytes(18);
        template.setValueOfColumn(ColumnXmpRating.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(19);
        template.setValueOfColumn(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE,
                                  rs.wasNull()
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
        boolean           updated    = false;
        Connection        connection = null;
        PreparedStatement stmt       = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            MetadataTemplate oldTemplate = find(template.getName());

            stmt = connection.prepareStatement("UPDATE metadata_edit_templates"
                                               + " SET name = ?" +    // --  1 --
                ", dcSubjects = ?" +    // --  2 --
                    ", dcTitle = ?" +    // --  3 --
                        ", photoshopHeadline = ?" +    // --  4 --
                            ", dcDescription = ?" +    // --  5 --
                                ", photoshopCaptionwriter = ?" +    // --  6 --
                                    ", iptc4xmpcoreLocation = ?" +    // --  7 --
                                            ", dcRights = ?" +    // --  8 --
                                            ", dcCreator = ?" +    // -- 9 --
                                            ", photoshopAuthorsposition = ?" +    // -- 10 --
                                            ", photoshopCity = ?" +    // -- 11 --
                                            ", photoshopState = ?" +    // -- 12 --
                                            ", photoshopCountry = ?" +    // -- 13 --
                                            ", photoshopTransmissionReference = ?" +    // -- 14 --
                                            ", photoshopInstructions = ?" +    // -- 15 --
                                            ", photoshopCredit = ?" +    // -- 16 --
                                            ", photoshopSource = ?" +    // -- 17 --
                                            ", rating = ?" +    // -- 18 --
                                            ", iptc4xmpcore_datecreated = ?" +    // -- 19 --
                                            " WHERE name = ?");    // -- 20 --
            set(stmt, template);
            stmt.setString(20, template.getName());
            logFiner(stmt);

            int count = stmt.executeUpdate();

            connection.commit();
            updated = count > 0;

            if (updated) {
                notifyListeners(
                    new DatabaseMetadataTemplatesEvent(
                        DatabaseMetadataTemplatesEvent.Type.UPDATED, template,
                        oldTemplate, this));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseMetadataTemplates.class, ex);
            rollback(connection);
        } finally {
            close(stmt);
            free(connection);
        }

        return updated;
    }

    /**
     * Benennt ein Metadaten-Edit-Template um.
     *
     * @param  oldName  Alter Name
     * @param  newName  Neuer Name
     * @return true bei Erfolg
     */
    public boolean updateRename(String oldName, String newName) {
        boolean           renamed    = false;
        Connection        connection = null;
        PreparedStatement stmt       = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            MetadataTemplate oldTemplate = find(oldName);

            stmt = connection.prepareStatement(
                "UPDATE metadata_edit_templates SET name = ? WHERE name = ?");
            stmt.setString(1, newName);
            stmt.setString(2, oldName);
            logFiner(stmt);

            int count = stmt.executeUpdate();

            connection.commit();
            renamed = count > 0;

            if (renamed) {
                MetadataTemplate newTemplate = find(newName);

                notifyListeners(
                    new DatabaseMetadataTemplatesEvent(
                        DatabaseMetadataTemplatesEvent.Type.UPDATED,
                        newTemplate, oldTemplate, this));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseMetadataTemplates.class, ex);
            rollback(connection);
        } finally {
            close(stmt);
            free(connection);
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
        boolean           deleted    = false;
        Connection        connection = null;
        PreparedStatement stmt       = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            MetadataTemplate template = find(name);

            stmt = connection.prepareStatement(
                "DELETE FROM metadata_edit_templates WHERE name = ?");
            stmt.setString(1, name);
            logFiner(stmt);

            int count = stmt.executeUpdate();

            connection.commit();
            deleted = count > 0;

            if (deleted) {
                notifyListeners(
                    new DatabaseMetadataTemplatesEvent(
                        DatabaseMetadataTemplatesEvent.Type.DELETED, template,
                        this));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseMetadataTemplates.class, ex);
            rollback(connection);
        } finally {
            close(stmt);
            free(connection);
        }

        return deleted;
    }

    public boolean exists(String name) {
        boolean           exists     = false;
        Connection        connection = null;
        PreparedStatement stmt       = null;
        ResultSet         rs         = null;

        try {
            connection = getConnection();
            stmt       = connection.prepareStatement("SELECT COUNT(*)"
                    + " FROM metadata_edit_templates" + " WHERE name = ?");
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
            free(connection);
        }

        return exists;
    }

    public void addListener(DatabaseMetadataTemplatesListener listener) {
        listenerSupport.add(listener);
    }

    public void removeListener(DatabaseMetadataTemplatesListener listener) {
        listenerSupport.remove(listener);
    }

    private void notifyListeners(DatabaseMetadataTemplatesEvent evt) {
        Set<DatabaseMetadataTemplatesListener> listeners =
            listenerSupport.get();

        synchronized (listeners) {
            for (DatabaseMetadataTemplatesListener listener : listeners) {
                listener.actionPerformed(evt);
            }
        }
    }
}
