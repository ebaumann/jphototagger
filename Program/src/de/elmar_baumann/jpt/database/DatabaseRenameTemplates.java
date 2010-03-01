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

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.data.RenameTemplate;
import de.elmar_baumann.jpt.event.DatabaseRenameTemplatesEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseRenameTemplatesListener;
import de.elmar_baumann.jpt.event.listener.impl.ListenerSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-03-01
 */
public final class DatabaseRenameTemplates extends Database {

    public static final DatabaseRenameTemplates                          INSTANCE        = new DatabaseRenameTemplates();
    private final       ListenerSupport<DatabaseRenameTemplatesListener> listenerSupport = new ListenerSupport<DatabaseRenameTemplatesListener>();

    private String getInsertSql() { // On updates update getUpdateSql()!
        return "INSERT INTO rename_templates (" +
                "name" +                         //  1
                ", start_number" +               //  2
                ", step_width" +                 //  3
                ", number_count" +               //  4
                ", date_delimiter" +             //  5
                ", format_class_at_begin" +      //  6
                ", delimiter_1" +                //  7
                ", format_class_in_the_middle" + //  8
                ", delimiter_2" +                //  9
                ", format_class_at_end" +        // 10
                ", text_at_begin" +              // 11
                ", text_in_the_middle" +         // 12
                ", text_at_end" +                // 13
                ")" +
                " VALUES (" +
                " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?" +
                ")";
    }

    private void setInsertValues(RenameTemplate template, PreparedStatement stmt) throws SQLException {
        setString(template.getName()                  , stmt,  1);
        setString(template.getStartNumber()           , stmt,  2);
        setString(template.getStepWidth()             , stmt,  3);
        setString(template.getNumberCount()           , stmt,  4);
        setString(template.getDateDelimiter()         , stmt,  5);
        setString(template.getFormatClassAtBegin()    , stmt,  6);
        setString(template.getDelimiter1()            , stmt,  7);
        setString(template.getFormatClassInTheMiddle(), stmt,  8);
        setString(template.getDelimiter2()            , stmt,  9);
        setString(template.getFormatClassAtEnd()      , stmt, 10);
        setString(template.getTextAtBegin()           , stmt, 11);
        setString(template.getTextInTheMiddle()       , stmt, 12);
        setString(template.getTextAtEnd()             , stmt, 13);
    }

    public boolean insert(RenameTemplate template) {
        boolean           inserted   = false;
        Connection        connection = null;
        PreparedStatement stmt       = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            stmt = connection.prepareStatement(getInsertSql());
            setInsertValues(template, stmt);
            logFiner(stmt);
            int count = stmt.executeUpdate();
            connection.commit();
            inserted = count > 0;
            if (inserted) {
                notifyListeners(DatabaseRenameTemplatesEvent.Type.TEMPLATE_INSERTED, template, null);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseRenameTemplates.class, ex);
        } finally {
            close(stmt);
            free(connection);
        }
        return inserted;
    }

    private String getUpdateSql() {
        return "UPDATE rename_templates SET " +
                "name = ?" +                         //  1
                ", start_number = ?" +               //  2
                ", step_width = ?" +                 //  3
                ", number_count = ?" +               //  4
                ", date_delimiter = ?" +             //  5
                ", format_class_at_begin = ?" +      //  6
                ", delimiter_1 = ?" +                //  7
                ", format_class_in_the_middle = ?" + //  8
                ", delimiter_2 = ?" +                //  9
                ", format_class_at_end = ?" +        // 10
                ", text_at_begin = ?" +              // 11
                ", text_in_the_middle = ?" +         // 12
                ", text_at_end = ?" +                // 13
                " WHERE name = ?";                   // 14
    }

    public boolean update(String name, RenameTemplate updatedTemplate) {
        boolean           updated    = false;
        Connection        connection = null;
        PreparedStatement stmt       = null;
        try {
            RenameTemplate oldTemplate = find(name);
            connection = getConnection();
            connection.setAutoCommit(false);
            stmt = connection.prepareStatement(getUpdateSql());
            setInsertValues(updatedTemplate, stmt); // Same parameters in same order
            setString(name, stmt,  14);
            logFiner(stmt);
            int count = stmt.executeUpdate();
            connection.commit();
            updated = count > 0;
            if (updated) {
                notifyListeners(DatabaseRenameTemplatesEvent.Type.TEMPLATE_UPDATED, updatedTemplate, oldTemplate);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseRenameTemplates.class, ex);
        } finally {
            close(stmt);
            free(connection);
        }
        return updated;
    }

    public int delete(String name) {
        Connection        connection = null;
        PreparedStatement stmt       = null;
        int               count      = 0;
        try {
            RenameTemplate oldTemplate = find(name);
            connection = getConnection();
            stmt = connection.prepareStatement(
                                 "DELETE FROM rename_templates WHERE name = ?");
            stmt.setString(1, name);
            logFiner(stmt);
            count = stmt.executeUpdate();
            if (count > 0) {
                notifyListeners(DatabaseRenameTemplatesEvent.Type.TEMPLATE_DELETED, oldTemplate, null);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFavorites.class, ex);
        } finally {
            close(stmt);
            free(connection);
        }
        return count;
    }
    
    private String getGetAllSql() {
        return "SELECT " +
                "name" +                         //  1
                ", start_number" +               //  2
                ", step_width" +                 //  3
                ", number_count" +               //  4
                ", date_delimiter" +             //  5
                ", format_class_at_begin" +      //  6
                ", delimiter_1" +                //  7
                ", format_class_in_the_middle" + //  8
                ", delimiter_2" +                //  9
                ", format_class_at_end" +        // 10
                ", text_at_begin" +              // 11
                ", text_in_the_middle" +         // 12
                ", text_at_end" +                // 13
                " FROM rename_templates" +
                " ORDER BY name ASC";
    }
    
    private RenameTemplate getTemplate(ResultSet rs) throws SQLException {
        RenameTemplate template = new RenameTemplate();
        template.setName                  (getString       (rs,  1));
        template.setStartNumber           (getInt          (rs,  2));
        template.setStepWidth             (getInt          (rs,  3));
        template.setNumberCount           (getInt          (rs,  4));
        template.setDateDelimiter         (getString       (rs,  5));
        template.setFormatClassAtBegin    (getClassFromName(rs,  6));
        template.setDelimiter1            (getString       (rs,  7));
        template.setFormatClassInTheMiddle(getClassFromName(rs,  8));
        template.setDelimiter2            (getString       (rs,  9));
        template.setFormatClassAtEnd      (getClassFromName(rs, 10));
        template.setTextAtBegin           (getString       (rs, 11));
        template.setTextInTheMiddle       (getString       (rs, 12));
        template.setTextAtEnd             (getString       (rs, 13));
        return template;
    }

    public Set<RenameTemplate> getAll() {
        Set<RenameTemplate> templates  = new LinkedHashSet<RenameTemplate>();
        Connection          connection = null;
        Statement           stmt       = null;
        ResultSet           rs         = null;
        try {
            connection = getConnection();
            stmt = connection.createStatement();
            String sql = getGetAllSql();
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                templates.add(getTemplate(rs));
            }
        } catch (Exception ex) {
            templates.clear();
            AppLogger.logSevere(DatabaseFavorites.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return templates;
    }

    private String getGetSql() {
        return "SELECT " +
                "name" +                         //  1
                ", start_number" +               //  2
                ", step_width" +                 //  3
                ", number_count" +               //  4
                ", date_delimiter" +             //  5
                ", format_class_at_begin" +      //  6
                ", delimiter_1" +                //  7
                ", format_class_in_the_middle" + //  8
                ", delimiter_2" +                //  9
                ", format_class_at_end" +        // 10
                ", text_at_begin" +              // 11
                ", text_in_the_middle" +         // 12
                ", text_at_end" +                // 13
                " FROM rename_templates" +
                " WHERE name = ?" +              // 14
                " ORDER BY name ASC";
    }

    private RenameTemplate find(String name) {
        RenameTemplate    template   = null;
        Connection        connection = null;
        PreparedStatement stmt       = null;
        ResultSet         rs         = null;
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(getGetSql());
            stmt.setString(1, name);
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                template = getTemplate(rs); // Same parameters in same order
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFavorites.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return template;
    }

    public boolean exists(String name) {
        boolean           exists     = false;
        Connection        connection = null;
        PreparedStatement stmt       = null;
        ResultSet         rs         = null;
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(
                        "SELECT COUNT(*) FROM rename_templates WHERE name = ?");
            stmt.setString(1, name);
            logFinest(stmt);
            rs = stmt.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            exists = count > 0;
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFavorites.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return exists;
    }

    public void addListener(DatabaseRenameTemplatesListener listener) {
        listenerSupport.add(listener);
    }

    public void removeListener(DatabaseRenameTemplatesListener listener) {
        listenerSupport.remove(listener);
    }

    private void notifyListeners(
            DatabaseRenameTemplatesEvent.Type type,
            RenameTemplate                    template,
            RenameTemplate                    oldTemplate
            ) {
        DatabaseRenameTemplatesEvent         evt       = new DatabaseRenameTemplatesEvent(type, template);
        Set<DatabaseRenameTemplatesListener> listeners = listenerSupport.get();

        if (oldTemplate != null) {
            evt.setOldTemplate(oldTemplate);
        }

        synchronized (listeners) {
            for (DatabaseRenameTemplatesListener listener : listeners) {
                listener.actionPerformed(evt);
            }
        }
    }

    private DatabaseRenameTemplates() {
    }
}
