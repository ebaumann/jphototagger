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
 * @author  Elmar Baumann
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
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?" +
                ")";
    }

    private void setValues(RenameTemplate template, PreparedStatement stmt) throws SQLException {
        setString   (template.getName()                  , stmt,  1);
        setInt      (template.getStartNumber()           , stmt,  2);
        setInt      (template.getStepWidth()             , stmt,  3);
        setInt      (template.getNumberCount()           , stmt,  4);
        setString   (template.getDateDelimiter()         , stmt,  5);
        setClassname(template.getFormatClassAtBegin()    , stmt,  6);
        setString   (template.getDelimiter1()            , stmt,  7);
        setClassname(template.getFormatClassInTheMiddle(), stmt,  8);
        setString   (template.getDelimiter2()            , stmt,  9);
        setClassname(template.getFormatClassAtEnd()      , stmt, 10);
        setString   (template.getTextAtBegin()           , stmt, 11);
        setString   (template.getTextInTheMiddle()       , stmt, 12);
        setString   (template.getTextAtEnd()             , stmt, 13);
    }

    public boolean insert(RenameTemplate template) {
        assert template.getId() == null : template.getId();
        boolean           inserted   = false;
        Connection        connection = null;
        PreparedStatement stmt       = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            stmt = connection.prepareStatement(getInsertSql());
            setValues(template, stmt);
            logFiner(stmt);
            int count = stmt.executeUpdate();
            connection.commit();
            inserted = count == 1;
            if (inserted) {
                template.setId(getId(template.getName()));
                notifyListeners(DatabaseRenameTemplatesEvent.Type.TEMPLATE_INSERTED, template);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseRenameTemplates.class, ex);
            rollback(connection);
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
                " WHERE id = ?";                     // 14
    }

    public boolean update(RenameTemplate template) {
        Connection        connection = null;
        PreparedStatement stmt       = null;
        int               count      = 0;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            stmt = connection.prepareStatement(getUpdateSql());
            setValues(template, stmt);
            stmt.setLong(14, template.getId());
            logFiner(stmt);
            count = stmt.executeUpdate();
            connection.commit();
            if (count == 1) {
                notifyListeners(DatabaseRenameTemplatesEvent.Type.TEMPLATE_UPDATED, template);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseRenameTemplates.class, ex);
            rollback(connection);
        } finally {
            close(stmt);
            free(connection);
        }
        return count == 1;
    }

    public int delete(String name) {
        Connection        connection = null;
        PreparedStatement stmt       = null;
        int               count      = 0;
        try {
            RenameTemplate delTemplate = find(name);
            connection = getConnection();
            connection.setAutoCommit(false);
            stmt = connection.prepareStatement(
                                 "DELETE FROM rename_templates WHERE name = ?");
            stmt.setString(1, name);
            logFiner(stmt);
            count = stmt.executeUpdate();
            connection.commit();
            if (count == 1) {
                notifyListeners(DatabaseRenameTemplatesEvent.Type.TEMPLATE_DELETED, delTemplate);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFavorites.class, ex);
            rollback(connection);
        } finally {
            close(stmt);
            free(connection);
        }
        return count;
    }
    
    private String getGetAllSql() {
        return "SELECT" +
                "  id" +                         //  1
                ", name" +                       //  2
                ", start_number" +               //  3
                ", step_width" +                 //  4
                ", number_count" +               //  5
                ", date_delimiter" +             //  6
                ", format_class_at_begin" +      //  7
                ", delimiter_1" +                //  8
                ", format_class_in_the_middle" + //  9
                ", delimiter_2" +                // 10
                ", format_class_at_end" +        // 11
                ", text_at_begin" +              // 12
                ", text_in_the_middle" +         // 13
                ", text_at_end" +                // 14
                " FROM rename_templates" +
                " ORDER BY name ASC";
    }
    
    private RenameTemplate getTemplate(ResultSet rs) throws SQLException {
        RenameTemplate template = new RenameTemplate();
        template.setId                    (getLong         (rs, 1));
        template.setName                  (getString       (rs,  2));
        template.setStartNumber           (getInt          (rs,  3));
        template.setStepWidth             (getInt          (rs,  4));
        template.setNumberCount           (getInt          (rs,  5));
        template.setDateDelimiter         (getString       (rs,  6));
        template.setFormatClassAtBegin    (getClassFromName(rs,  7));
        template.setDelimiter1            (getString       (rs,  8));
        template.setFormatClassInTheMiddle(getClassFromName(rs,  9));
        template.setDelimiter2            (getString       (rs, 10));
        template.setFormatClassAtEnd      (getClassFromName(rs, 11));
        template.setTextAtBegin           (getString       (rs, 12));
        template.setTextInTheMiddle       (getString       (rs, 13));
        template.setTextAtEnd             (getString       (rs, 14));
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
                "  id" +                         //  1
                ", name" +                       //  2
                ", start_number" +               //  3
                ", step_width" +                 //  4
                ", number_count" +               //  5
                ", date_delimiter" +             //  6
                ", format_class_at_begin" +      //  7
                ", delimiter_1" +                //  8
                ", format_class_in_the_middle" + //  9
                ", delimiter_2" +                // 10
                ", format_class_at_end" +        // 11
                ", text_at_begin" +              // 12
                ", text_in_the_middle" +         // 13
                ", text_at_end" +                // 14
                " FROM rename_templates" +
                " WHERE name = ?" +              // 15
                " ORDER BY name ASC";
    }

    /**
     * Finds a template by its name.
     *
     * @param  name name
     * @return      template or null if not found
     */
    public RenameTemplate find(String name) {
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
                template = getTemplate(rs);
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

    private long getId(String name) throws SQLException {
        Connection        connection = null;
        PreparedStatement stmt       = null;
        ResultSet         rs         = null;
        long              id         = -1;
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(
                             "SELECT id FROM rename_templates WHERE name = ?");
            stmt.setString(1, name);
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFavorites.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return id;
    }

    public void addListener(DatabaseRenameTemplatesListener listener) {
        listenerSupport.add(listener);
    }

    public void removeListener(DatabaseRenameTemplatesListener listener) {
        listenerSupport.remove(listener);
    }

    private void notifyListeners(
            DatabaseRenameTemplatesEvent.Type type,
            RenameTemplate                    template
            ) {
        DatabaseRenameTemplatesEvent         evt       = new DatabaseRenameTemplatesEvent(type, template);
        Set<DatabaseRenameTemplatesListener> listeners = listenerSupport.get();

        synchronized (listeners) {
            for (DatabaseRenameTemplatesListener listener : listeners) {
                listener.actionPerformed(evt);
            }
        }
    }

    private DatabaseRenameTemplates() {
    }
}
