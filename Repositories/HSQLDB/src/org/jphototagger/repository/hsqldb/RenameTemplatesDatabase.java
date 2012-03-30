package org.jphototagger.repository.hsqldb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.EventBus;

import org.jphototagger.domain.repository.event.renametemplates.RenameTemplateDeletedEvent;
import org.jphototagger.domain.repository.event.renametemplates.RenameTemplateInsertedEvent;
import org.jphototagger.domain.repository.event.renametemplates.RenameTemplateUpdatedEvent;
import org.jphototagger.domain.templates.RenameTemplate;

/**
 * @author Elmar Baumann
 */
final class RenameTemplatesDatabase extends Database {

    static final RenameTemplatesDatabase INSTANCE = new RenameTemplatesDatabase();
    private static final Logger LOGGER = Logger.getLogger(RenameTemplatesDatabase.class.getName());

    private RenameTemplatesDatabase() {
    }

    private String getInsertSql() {    // On updates updateRenameTemplate getUpdateSql()!
        return "INSERT INTO rename_templates (name" // 1
                + ", start_number" // 2
                + ", step_width" // 3
                + ", number_count" // 4
                + ", date_delimiter" // 5
                + ", format_class_at_begin" // 6
                + ", delimiter_1" // 7
                + ", format_class_in_the_middle" // 8
                + ", delimiter_2" // 9
                + ", format_class_at_end" // 10
                + ", text_at_begin" // 11
                + ", text_in_the_middle" // 12
                + ", text_at_end" // 13
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    private void setValues(RenameTemplate template, PreparedStatement stmt) throws SQLException {
        setString(template.getName(), stmt, 1);
        setInt(template.getStartNumber(), stmt, 2);
        setInt(template.getStepWidth(), stmt, 3);
        setInt(template.getNumberCount(), stmt, 4);
        setString(template.getDateDelimiter(), stmt, 5);
        setClassname(template.getFormatClassAtBegin(), stmt, 6);
        setString(template.getDelimiter1(), stmt, 7);
        setClassname(template.getFormatClassInTheMiddle(), stmt, 8);
        setString(template.getDelimiter2(), stmt, 9);
        setClassname(template.getFormatClassAtEnd(), stmt, 10);
        setString(template.getTextAtBegin(), stmt, 11);
        setString(template.getTextInTheMiddle(), stmt, 12);
        setString(template.getTextAtEnd(), stmt, 13);
    }

    boolean insertRenameTemplate(RenameTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }
        boolean inserted = false;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(getInsertSql());
            setValues(template, stmt);
            LOGGER.log(Level.FINER, stmt.toString());
            int count = stmt.executeUpdate();
            con.commit();
            inserted = count == 1;
            if (inserted) {
                template.setId(getId(template.getName()));
                notifyInserted(template);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return inserted;
    }

    private String getUpdateSql() {
        return "UPDATE rename_templates SET name = ?" // 1
                + ", start_number = ?" // 2
                + ", step_width = ?" // 3
                + ", number_count = ?" // 4
                + ", date_delimiter = ?" // 5
                + ", format_class_at_begin = ?" // 6
                + ", delimiter_1 = ?" // 7
                + ", format_class_in_the_middle = ?" // 8
                + ", delimiter_2 = ?" // 9
                + ", format_class_at_end = ?" // 10
                + ", text_at_begin = ?" // 11
                + ", text_in_the_middle = ?" // 12
                + ", text_at_end = ?" // 13
                + " WHERE id = ?";    // 14
    }

    boolean updateRenameTemplate(RenameTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }
        Connection con = null;
        PreparedStatement stmt = null;
        int count = 0;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(getUpdateSql());
            setValues(template, stmt);
            stmt.setLong(14, template.getId());
            LOGGER.log(Level.FINER, stmt.toString());
            count = stmt.executeUpdate();
            con.commit();
            if (count == 1) {
                notifyUpdated(template);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return count == 1;
    }

    int deleteRenameTemplate(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        Connection con = null;
        PreparedStatement stmt = null;
        int count = 0;
        try {
            RenameTemplate delTemplate = findRenameTemplate(name);
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("DELETE FROM rename_templates WHERE name = ?");
            stmt.setString(1, name);
            LOGGER.log(Level.FINER, stmt.toString());
            count = stmt.executeUpdate();
            con.commit();
            if (count == 1) {
                notifyDeleted(delTemplate);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return count;
    }

    private String getGetAllSql() {
        return "SELECT  id" // 1
                + ", name" // 2
                + ", start_number" // 3
                + ", step_width" // 4
                + ", number_count" // 5
                + ", date_delimiter" // 6
                + ", format_class_at_begin" // 7
                + ", delimiter_1" // 8
                + ", format_class_in_the_middle" // 9
                + ", delimiter_2" // 10
                + ", format_class_at_end" // 11
                + ", text_at_begin" // 12
                + ", text_in_the_middle" // 13
                + ", text_at_end" // 14
                + " FROM rename_templates ORDER BY name ASC";
    }

    private RenameTemplate getTemplate(ResultSet rs) throws SQLException {
        RenameTemplate template = new RenameTemplate();

        template.setId(getLong(rs, 1));
        template.setName(getString(rs, 2));
        template.setStartNumber(getInt(rs, 3));
        template.setStepWidth(getInt(rs, 4));
        template.setNumberCount(getInt(rs, 5));
        template.setDateDelimiter(getString(rs, 6));
        template.setFormatClassAtBegin(getClassFromName(rs, 7));
        template.setDelimiter1(getString(rs, 8));
        template.setFormatClassInTheMiddle(getClassFromName(rs, 9));
        template.setDelimiter2(getString(rs, 10));
        template.setFormatClassAtEnd(getClassFromName(rs, 11));
        template.setTextAtBegin(getString(rs, 12));
        template.setTextInTheMiddle(getString(rs, 13));
        template.setTextAtEnd(getString(rs, 14));

        return template;
    }

    Set<RenameTemplate> getAllRenameTemplates() {
        Set<RenameTemplate> templates = new LinkedHashSet<RenameTemplate>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.createStatement();
            String sql = getGetAllSql();
            LOGGER.log(Level.FINEST, sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                templates.add(getTemplate(rs));
            }
        } catch (Exception ex) {
            templates.clear();
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return templates;
    }

    private String getGetSql() {
        return "SELECT id" // 1
                + ", name" // 2
                + ", start_number" // 3
                + ", step_width" // 4
                + ", number_count" // 5
                + ", date_delimiter" // 6
                + ", format_class_at_begin" // 7
                + ", delimiter_1" // 8
                + ", format_class_in_the_middle" // 9
                + ", delimiter_2" // 10
                + ", format_class_at_end" // 11
                + ", text_at_begin" // 12
                + ", text_in_the_middle" // 13
                + ", text_at_end" // 14
                + " FROM rename_templates WHERE name = ?" // 15
                + " ORDER BY name ASC";
    }

    /**
     * Finds a template by its name.
     *
     * @param  name name
     * @return      template or null if not found
     */
    RenameTemplate findRenameTemplate(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        RenameTemplate template = null;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement(getGetSql());
            stmt.setString(1, name);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                template = getTemplate(rs);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return template;
    }

    boolean existsRenameTemplate(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        boolean exists = false;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement("SELECT COUNT(*) FROM rename_templates WHERE name = ?");
            stmt.setString(1, name);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            exists = count > 0;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return exists;
    }

    private long getId(String name) throws SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        long id = -1;
        try {
            con = getConnection();
            stmt = con.prepareStatement("SELECT id FROM rename_templates WHERE name = ?");
            stmt.setString(1, name);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return id;
    }

    private void notifyDeleted(RenameTemplate template) {
        EventBus.publish(new RenameTemplateDeletedEvent(this, template));
    }

    private void notifyInserted(RenameTemplate template) {
        EventBus.publish(new RenameTemplateInsertedEvent(this, template));
    }

    private void notifyUpdated(RenameTemplate template) {
        EventBus.publish(new RenameTemplateUpdatedEvent(this, template));
    }
}
