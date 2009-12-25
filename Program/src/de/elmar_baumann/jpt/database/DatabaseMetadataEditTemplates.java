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
package de.elmar_baumann.jpt.database;

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.data.MetadataEditTemplate;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreCountrycode;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopAuthorsposition;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCaptionwriter;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCity;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopInstructions;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopState;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopTransmissionReference;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpRating;
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
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-21
 */
public class DatabaseMetadataEditTemplates extends Database {

    private static final String                        DELIM_REPEATABLE_STRINGS = "\t";
    public static final  DatabaseMetadataEditTemplates INSTANCE                 = new DatabaseMetadataEditTemplates();

    private DatabaseMetadataEditTemplates() {
    }

    /**
     * Fügt ein neues Metadaten-Edit-Template ein. Existiert das Template
     * bereits, werden seine Daten aktualisiert.
     *
     * @param  template  Template
     * @return           true bei Erfolg
     */
    public boolean insertMetadataEditTemplate(MetadataEditTemplate template) {

        if (existsMetadataEditTemplate(template.getName())) {
            return updateMetadataEditTemplate(template);
        }
        boolean inserted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt =
                    connection.prepareStatement(
                    "INSERT INTO metadata_edit_templates" + // NOI18N
                    " (name" +                            // NOI18N --  1 --
                    ", dcSubjects" +                      // NOI18N --  2 --
                    ", dcTitle" +                         // NOI18N --  3 --
                    ", photoshopHeadline" +               // NOI18N --  4 --
                    ", dcDescription" +                   // NOI18N --  5 --
                    ", photoshopCaptionwriter" +          // NOI18N --  6 --
                    ", iptc4xmpcoreLocation" +            // NOI18N --  7 --
                    ", iptc4xmpcoreCountrycode" +         // NOI18N --  8 --
                    ", dcRights" +                        // NOI18N --  9 --
                    ", dcCreator" +                       // NOI18N -- 10 --
                    ", photoshopAuthorsposition" +        // NOI18N -- 11 --
                    ", photoshopCity" +                   // NOI18N -- 12 --
                    ", photoshopState" +                  // NOI18N -- 13 --
                    ", photoshopCountry" +                // NOI18N -- 14 --
                    ", photoshopTransmissionReference" +  // NOI18N -- 15 --
                    ", photoshopInstructions" +           // NOI18N -- 16 --
                    ", photoshopCredit" +                 // NOI18N -- 17 --
                    ", photoshopSource" +                 // NOI18N -- 18 --
                    ", rating" +                          // NOI18N -- 19 --
                    ")" + // NOI18N
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); // NOI18N
            setMetadataEditTemplate(stmt, template);
            logFiner(stmt);
            stmt.executeUpdate();
            connection.commit();
            inserted = true;
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseMetadataEditTemplates.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return inserted;
    }

    @SuppressWarnings("unchecked")
    private void setMetadataEditTemplate(
            PreparedStatement stmt, MetadataEditTemplate template) throws
            SQLException {

        stmt.setString(1, template.getName());
        stmt.setBytes(2, template.getValueOfColumn(ColumnXmpDcSubjectsSubject.INSTANCE) == null
                         ? null
                         : fromRepeatable((Collection<String>)template.getValueOfColumn(ColumnXmpDcSubjectsSubject.INSTANCE)).getBytes());
        stmt.setBytes(3, template.getValueOfColumn(ColumnXmpDcTitle.INSTANCE) == null
                         ? null
                         : ((String)template.getValueOfColumn(ColumnXmpDcTitle.INSTANCE)).getBytes());
        stmt.setBytes(4, template.getValueOfColumn(ColumnXmpPhotoshopHeadline.INSTANCE) == null
                         ? null
                         : ((String)template.getValueOfColumn(ColumnXmpPhotoshopHeadline.INSTANCE)).getBytes());
        stmt.setBytes(5, template.getValueOfColumn(ColumnXmpDcDescription.INSTANCE) == null
                         ? null
                         : ((String)template.getValueOfColumn(ColumnXmpDcDescription.INSTANCE)).getBytes());
        stmt.setBytes(6, template.getValueOfColumn(ColumnXmpPhotoshopCaptionwriter.INSTANCE) == null
                         ? null
                         : ((String)template.getValueOfColumn(ColumnXmpPhotoshopCaptionwriter.INSTANCE)).getBytes());
        stmt.setBytes(7, template.getValueOfColumn(ColumnXmpIptc4xmpcoreLocation.INSTANCE) == null
                         ? null
                         : ((String)template.getValueOfColumn(ColumnXmpIptc4xmpcoreLocation.INSTANCE)).getBytes());
        stmt.setBytes(8, template.getValueOfColumn(ColumnXmpIptc4xmpcoreCountrycode.INSTANCE) == null
                         ? null
                         : ((String)template.getValueOfColumn(ColumnXmpIptc4xmpcoreCountrycode.INSTANCE)).getBytes());
        stmt.setBytes(9, template.getValueOfColumn(ColumnXmpDcRights.INSTANCE) == null
                          ? null
                          : ((String)template.getValueOfColumn(ColumnXmpDcRights.INSTANCE)).getBytes());
        stmt.setBytes(10, template.getValueOfColumn(ColumnXmpDcCreator.INSTANCE) == null
                          ? null
                          : ((String)template.getValueOfColumn(ColumnXmpDcCreator.INSTANCE)).getBytes());
        stmt.setBytes(11, template.getValueOfColumn(ColumnXmpPhotoshopAuthorsposition.INSTANCE) == null
                          ? null
                          : ((String)template.getValueOfColumn(ColumnXmpPhotoshopAuthorsposition.INSTANCE)).getBytes());
        stmt.setBytes(12, template.getValueOfColumn(ColumnXmpPhotoshopCity.INSTANCE) == null
                          ? null
                          : ((String)template.getValueOfColumn(ColumnXmpPhotoshopCity.INSTANCE)).getBytes());
        stmt.setBytes(13, template.getValueOfColumn(ColumnXmpPhotoshopState.INSTANCE) == null
                          ? null
                          : ((String)template.getValueOfColumn(ColumnXmpPhotoshopState.INSTANCE)).getBytes());
        stmt.setBytes(14, template.getValueOfColumn(ColumnXmpPhotoshopCountry.INSTANCE) == null
                          ? null
                          : ((String)template.getValueOfColumn(ColumnXmpPhotoshopCountry.INSTANCE)).getBytes());
        stmt.setBytes(15, template.getValueOfColumn(ColumnXmpPhotoshopTransmissionReference.INSTANCE) == null
                          ? null
                          : ((String)template.getValueOfColumn(ColumnXmpPhotoshopTransmissionReference.INSTANCE)).getBytes());
        stmt.setBytes(16, template.getValueOfColumn(ColumnXmpPhotoshopInstructions.INSTANCE) == null
                          ? null
                          : ((String)template.getValueOfColumn(ColumnXmpPhotoshopInstructions.INSTANCE)).getBytes());
        stmt.setBytes(17, template.getValueOfColumn(ColumnXmpPhotoshopCredit.INSTANCE) == null
                          ? null
                          : ((String)template.getValueOfColumn(ColumnXmpPhotoshopCredit.INSTANCE)).getBytes());
        stmt.setBytes(18, template.getValueOfColumn(ColumnXmpPhotoshopSource.INSTANCE) == null
                          ? null
                          : ((String)template.getValueOfColumn(ColumnXmpPhotoshopSource.INSTANCE)).getBytes());
        stmt.setBytes(19, template.getValueOfColumn(ColumnXmpRating.INSTANCE) == null
                          ? null
                          : ((String)template.getValueOfColumn(ColumnXmpRating.INSTANCE)).getBytes());
    }

    private String fromRepeatable(Collection<String> strings) {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (String string : strings) {
            sb.append(index++ == 0 ? "" : DELIM_REPEATABLE_STRINGS);
            sb.append(string);
        }
        return sb.toString();
    }

    private List<String> toRepeatable(String string) {
        List<String>    strings   = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(string, DELIM_REPEATABLE_STRINGS);

        while (tokenizer.hasMoreTokens()) {
            strings.add(tokenizer.nextToken());
        }
        return strings;
    }

    /**
     * Liefert alle Metadaten-Edit-Templates.
     *
     * @return Templates
     */
    public List<MetadataEditTemplate> getMetadataEditTemplates() {
        List<MetadataEditTemplate> templates =
                new ArrayList<MetadataEditTemplate>();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql =
                    "SELECT" + // NOI18N
                    " name" +                             // NOI18N --  1 --
                    ", dcSubjects" +                      // NOI18N --  2 --
                    ", dcTitle" +                         // NOI18N --  3 --
                    ", photoshopHeadline" +               // NOI18N --  4 --
                    ", dcDescription" +                   // NOI18N --  5 --
                    ", photoshopCaptionwriter" +          // NOI18N --  6 --
                    ", iptc4xmpcoreLocation" +            // NOI18N --  7 --
                    ", iptc4xmpcoreCountrycode" +         // NOI18N --  8 --
                    ", dcRights" +                        // NOI18N --  9 --
                    ", dcCreator" +                       // NOI18N -- 10 --
                    ", photoshopAuthorsposition" +        // NOI18N -- 11 --
                    ", photoshopCity" +                   // NOI18N -- 12 --
                    ", photoshopState" +                  // NOI18N -- 13 --
                    ", photoshopCountry" +                // NOI18N -- 14 --
                    ", photoshopTransmissionReference" +  // NOI18N -- 15 --
                    ", photoshopInstructions" +           // NOI18N -- 16 --
                    ", photoshopCredit" +                 // NOI18N -- 17 --
                    ", photoshopSource" +                 // NOI18N -- 18 --
                    ", rating" +                          // NOI18N -- 19 --
                    " FROM metadata_edit_templates" + // NOI18N
                    " WHERE name IS NOT NULL"; // NOI18N
            logFinest(sql);
            ResultSet rs   = stmt.executeQuery(sql);
            byte[]    bytes;
            while (rs.next()) {
                MetadataEditTemplate template = new MetadataEditTemplate();
                template.setName(rs.getString(1));
                bytes = rs.getBytes(2);
                template.setValueOfColumn(ColumnXmpDcSubjectsSubject.INSTANCE, rs.wasNull() ? null : toRepeatable(new String(bytes)));
                bytes = rs.getBytes(3);
                template.setValueOfColumn(ColumnXmpDcTitle.INSTANCE, rs.wasNull() ? null : new String(bytes));
                bytes = rs.getBytes(4);
                template.setValueOfColumn(ColumnXmpPhotoshopHeadline.INSTANCE, rs.wasNull() ? null : new String(bytes));
                bytes = rs.getBytes(5);
                template.setValueOfColumn(ColumnXmpDcDescription.INSTANCE, rs.wasNull() ? null : new String(bytes));
                bytes = rs.getBytes(6);
                template.setValueOfColumn(ColumnXmpPhotoshopCaptionwriter.INSTANCE, rs.wasNull() ? null : new String(bytes));
                bytes = rs.getBytes(7);
                template.setValueOfColumn(ColumnXmpIptc4xmpcoreLocation.INSTANCE, rs.wasNull() ? null : new String(bytes));
                bytes = rs.getBytes(8);
                template.setValueOfColumn(ColumnXmpIptc4xmpcoreCountrycode.INSTANCE, rs.wasNull() ? null : new String(bytes));
                bytes = rs.getBytes(9);
                template.setValueOfColumn(ColumnXmpDcRights.INSTANCE, rs.wasNull() ? null : new String(bytes));
                bytes = rs.getBytes(10);
                template.setValueOfColumn(ColumnXmpDcCreator.INSTANCE, rs.wasNull() ? null : new String(bytes));
                bytes = rs.getBytes(11);
                template.setValueOfColumn(ColumnXmpPhotoshopAuthorsposition.INSTANCE, rs.wasNull() ? null : new String(bytes));
                bytes = rs.getBytes(12);
                template.setValueOfColumn(ColumnXmpPhotoshopCity.INSTANCE, rs.wasNull() ? null : new String(bytes));
                bytes = rs.getBytes(13);
                template.setValueOfColumn(ColumnXmpPhotoshopState.INSTANCE, rs.wasNull() ? null : new String(bytes));
                bytes = rs.getBytes(14);
                template.setValueOfColumn(ColumnXmpPhotoshopCountry.INSTANCE, rs.wasNull() ? null : new String(bytes));
                bytes = rs.getBytes(15);
                template.setValueOfColumn(ColumnXmpPhotoshopTransmissionReference.INSTANCE, rs.wasNull() ? null : new String(bytes));
                bytes = rs.getBytes(16);
                template.setValueOfColumn(ColumnXmpPhotoshopInstructions.INSTANCE, rs.wasNull() ? null : new String(bytes));
                bytes = rs.getBytes(17);
                template.setValueOfColumn(ColumnXmpPhotoshopCredit.INSTANCE, rs.wasNull() ? null : new String(bytes));
                bytes = rs.getBytes(18);
                template.setValueOfColumn(ColumnXmpPhotoshopSource.INSTANCE, rs.wasNull() ? null : new String(bytes));
                bytes = rs.getBytes(19);
                template.setValueOfColumn(ColumnXmpRating.INSTANCE, rs.wasNull() ? null : new String(bytes));
                templates.add(template);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseMetadataEditTemplates.class, ex);
        } finally {
            free(connection);
        }
        return templates;
    }

    /**
     * Aktualisiert ein Metadaten-Edit-Template.
     *
     * @param  template  Template
     * @return true bei Erfolg
     */
    public boolean updateMetadataEditTemplate(MetadataEditTemplate template) {

        boolean updated = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE metadata_edit_templates" + // NOI18N
                    " SET name = ?" +                         // NOI18N --  1 --
                    ", dcSubjects = ?" +                      // NOI18N --  2 --
                    ", dcTitle = ?" +                         // NOI18N --  3 --
                    ", photoshopHeadline = ?" +               // NOI18N --  4 --
                    ", dcDescription = ?" +                   // NOI18N --  5 --
                    ", photoshopCaptionwriter = ?" +          // NOI18N --  6 --
                    ", iptc4xmpcoreLocation = ?" +            // NOI18N --  7 --
                    ", iptc4xmpcoreCountrycode = ?" +         // NOI18N --  8 --
                    ", dcRights = ?" +                        // NOI18N --  9 --
                    ", dcCreator = ?" +                       // NOI18N -- 10 --
                    ", photoshopAuthorsposition = ?" +        // NOI18N -- 11 --
                    ", photoshopCity = ?" +                   // NOI18N -- 12 --
                    ", photoshopState = ?" +                  // NOI18N -- 13 --
                    ", photoshopCountry = ?" +                // NOI18N -- 14 --
                    ", photoshopTransmissionReference = ?" +  // NOI18N -- 15 --
                    ", photoshopInstructions = ?" +           // NOI18N -- 16 --
                    ", photoshopCredit = ?" +                 // NOI18N -- 17 --
                    ", photoshopSource = ?" +                 // NOI18N -- 18 --
                    ", rating= ?" +                           // NOI18N -- 19 --
                    " WHERE name = ?");                       // NOI18N -- 20 --
            setMetadataEditTemplate(stmt, template);
            stmt.setString(20, template.getName());
            logFiner(stmt);
            int count = stmt.executeUpdate();
            connection.commit();
            updated = count > 0;
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseMetadataEditTemplates.class, ex);
            rollback(connection);
        } finally {
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
    public boolean updateRenameMetadataEditTemplate(
            String oldName, String newName) {

        boolean renamed = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE metadata_edit_templates" + // NOI18N
                    " SET name = ?" + // NOI18N
                    " WHERE name = ?"); // NOI18N
            stmt.setString(1, newName);
            stmt.setString(2, oldName);
            logFiner(stmt);
            int count = stmt.executeUpdate();
            connection.commit();
            renamed = count > 0;
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseMetadataEditTemplates.class, ex);
            rollback(connection);
        } finally {
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
    public boolean deleteMetadataEditTemplate(String name) {
        boolean deleted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM metadata_edit_templates WHERE name = ?"); // NOI18N
            stmt.setString(1, name);
            logFiner(stmt);
            int count = stmt.executeUpdate();
            connection.commit();
            deleted = count > 0;
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseMetadataEditTemplates.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return deleted;
    }

    public boolean existsMetadataEditTemplate(String name) {
        boolean exists = false;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT COUNT(*)" + // NOI18N
                    " FROM metadata_edit_templates" + // NOI18N
                    " WHERE name = ?"); // NOI18N
            stmt.setString(1, name);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseMetadataEditTemplates.class, ex);
        } finally {
            free(connection);
        }
        return exists;
    }
}
