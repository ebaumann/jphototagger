package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.MetadataEditTemplate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/21
 */
public class DatabaseMetadataEditTemplates extends Database {
    
    public static final DatabaseMetadataEditTemplates INSTANCE = new DatabaseMetadataEditTemplates();
    
    private DatabaseMetadataEditTemplates() {
    }

    /**
     * Fügt ein neues Metadaten-Edit-Template ein. Existiert das Template
     * bereits, werden seine Daten aktualisiert.
     *
     * @param  template  Template
     * @return true bei Erfolg
     */
    public boolean insertMetadataEditTemplate(
        MetadataEditTemplate template) {
        
        if (existsMetadataEditTemplate(template.getName())) {
            return updateMetadataEditTemplate(template);
        }
        boolean inserted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO metadata_edit_templates" + // NOI18N
                " (name" + // NOI18N -- 1 --
                ", dcSubjects" + // NOI18N -- 2 --
                ", dcTitle" + // NOI18N -- 3 --
                ", photoshopHeadline" + // NOI18N -- 4 --
                ", dcDescription" + // NOI18N -- 5 --
                ", photoshopCaptionwriter" + // NOI18N -- 6 --
                ", iptc4xmpcoreLocation" + // NOI18N -- 7 --
                ", iptc4xmpcoreCountrycode" + // NOI18N -- 8 --
                ", photoshopCategory" + // NOI18N -- 9 --
                ", photoshopSupplementalCategories" + // NOI18N -- 10 --
                ", dcRights" + // NOI18N -- 11 --
                ", dcCreator" + // NOI18N -- 12 --
                ", photoshopAuthorsposition" + // NOI18N -- 13 --
                ", photoshopCity" + // NOI18N -- 14 --
                ", photoshopState" + // NOI18N -- 15 --
                ", photoshopCountry" + // NOI18N -- 16 --
                ", photoshopTransmissionReference" + // NOI18N -- 17 --
                ", photoshopInstructions" + // NOI18N -- 18 --
                ", photoshopCredit" + // NOI18N -- 19 --
                ", photoshopSource" + // NOI18N -- 20 --
                ")" + // NOI18N
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); // NOI18N
            setMetadataEditTemplate(stmt, template);
            AppLog.logFiner(DatabaseMetadataEditTemplates.class, stmt.toString());
            stmt.executeUpdate();
            connection.commit();
            inserted = true;
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseMetadataEditTemplates.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return inserted;
    }

    private void setMetadataEditTemplate(
        PreparedStatement stmt, MetadataEditTemplate template) throws SQLException {
        
        stmt.setString(1, template.getName());
        stmt.setBytes(2, template.getDcSubjects() == null 
            ? null : template.getDcSubjects().getBytes());
        stmt.setBytes(3, template.getDcTitle() == null 
            ? null : template.getDcTitle().getBytes());
        stmt.setBytes(4, template.getPhotoshopHeadline() == null 
            ? null : template.getPhotoshopHeadline().getBytes());
        stmt.setBytes(5, template.getDcDescription() == null 
            ? null : template.getDcDescription().getBytes());
        stmt.setBytes(6, template.getPhotoshopCaptionwriter() == null 
            ? null : template.getPhotoshopCaptionwriter().getBytes());
        stmt.setBytes(7, template.getIptc4xmpcoreLocation() == null 
            ? null : template.getIptc4xmpcoreLocation().getBytes());
        stmt.setBytes(8, template.getIptc4xmpcoreCountrycode() == null 
            ? null : template.getIptc4xmpcoreCountrycode().getBytes());
        stmt.setBytes(9, template.getPhotoshopCategory() == null 
            ? null : template.getPhotoshopCategory().getBytes());
        stmt.setBytes(10, template.getPhotoshopSupplementalCategories() == null 
            ? null : template.getPhotoshopSupplementalCategories().getBytes());
        stmt.setBytes(11, template.getDcRights() == null 
            ? null : template.getDcRights().getBytes());
        stmt.setBytes(12, template.getDcCreator() == null 
            ? null : template.getDcCreator().getBytes());
        stmt.setBytes(13, template.getPhotoshopAuthorsposition() == null 
            ? null : template.getPhotoshopAuthorsposition().getBytes());
        stmt.setBytes(14, template.getPhotoshopCity() == null 
            ? null : template.getPhotoshopCity().getBytes());
        stmt.setBytes(15, template.getPhotoshopState() == null 
            ? null : template.getPhotoshopState().getBytes());
        stmt.setBytes(16, template.getPhotoshopCountry() == null 
            ? null : template.getPhotoshopCountry().getBytes());
        stmt.setBytes(17, template.getPhotoshopTransmissionReference() == null 
            ? null : template.getPhotoshopTransmissionReference().getBytes());
        stmt.setBytes(18, template.getPhotoshopInstructions() == null 
            ? null : template.getPhotoshopInstructions().getBytes());
        stmt.setBytes(19, template.getPhotoshopCredit() == null 
            ? null : template.getPhotoshopCredit().getBytes());
        stmt.setBytes(20, template.getPhotoshopSource() == null 
            ? null : template.getPhotoshopSource().getBytes());
    }

    /**
     * Liefert alle Metadaten-Edit-Templates.
     *
     * @return Templates
     */
    public List<MetadataEditTemplate> getMetadataEditTemplates() {
        List<MetadataEditTemplate> templates = new ArrayList<MetadataEditTemplate>();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT" + // NOI18N
                " name" + // NOI18N -- 1 --
                ", dcSubjects" + // NOI18N -- 2 --
                ", dcTitle" + // NOI18N -- 3 --
                ", photoshopHeadline" + // NOI18N -- 4 --
                ", dcDescription" + // NOI18N -- 5 --
                ", photoshopCaptionwriter" + // NOI18N -- 6 --
                ", iptc4xmpcoreLocation" + // NOI18N -- 7 --
                ", iptc4xmpcoreCountrycode" + // NOI18N -- 8 --
                ", photoshopCategory" + // NOI18N -- 9 --
                ", photoshopSupplementalCategories" + // NOI18N -- 10 --
                ", dcRights" + // NOI18N -- 11 --
                ", dcCreator" + // NOI18N -- 12 --
                ", photoshopAuthorsposition" + // NOI18N -- 13 --
                ", photoshopCity" + // NOI18N -- 14 --
                ", photoshopState" + // NOI18N -- 15 --
                ", photoshopCountry" + // NOI18N -- 16 --
                ", photoshopTransmissionReference" + // NOI18N -- 17 --
                ", photoshopInstructions" + // NOI18N -- 18 --
                ", photoshopCredit" + // NOI18N -- 19 --
                ", photoshopSource" + // NOI18N -- 20 --
                " FROM metadata_edit_templates" + // NOI18N
                " WHERE name IS NOT NULL"); // NOI18N
            while (rs.next()) {
                MetadataEditTemplate template = new MetadataEditTemplate();
                template.setName(rs.getString(1));
                template.setDcSubjects(new String(rs.getBytes(2)));
                template.setDcTitle(new String(rs.getBytes(3)));
                template.setPhotoshopHeadline(new String(rs.getBytes(4)));
                template.setDcDescription(new String(rs.getBytes(5)));
                template.setPhotoshopCaptionwriter(new String(rs.getBytes(6)));
                template.setIptc4xmpcoreLocation(new String(rs.getBytes(7)));
                template.setIptc4xmpcoreCountrycode(new String(rs.getBytes(8)));
                template.setPhotoshopCategory(new String(rs.getBytes(9)));
                template.setPhotoshopSupplementalCategories(new String(rs.getBytes(10)));
                template.setDcRights(new String(rs.getBytes(11)));
                template.setDcCreator(new String(rs.getBytes(12)));
                template.setPhotoshopAuthorsposition(new String(rs.getBytes(13)));
                template.setPhotoshopCity(new String(rs.getBytes(14)));
                template.setPhotoshopState(new String(rs.getBytes(15)));
                template.setPhotoshopCountry(new String(rs.getBytes(16)));
                template.setPhotoshopTransmissionReference(new String(rs.getBytes(17)));
                template.setPhotoshopInstructions(new String(rs.getBytes(18)));
                template.setPhotoshopCredit(new String(rs.getBytes(19)));
                template.setPhotoshopSource(new String(rs.getBytes(20)));
                templates.add(template);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseMetadataEditTemplates.class, ex);
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
                " SET name = ?" + // NOI18N -- 1 --
                ", dcSubjects = ?" + // NOI18N -- 2 --
                ", dcTitle = ?" + // NOI18N -- 3 --
                ", photoshopHeadline = ?" + // NOI18N -- 4 --
                ", dcDescription = ?" + // NOI18N -- 5 --
                ", photoshopCaptionwriter = ?" + // NOI18N -- 6 --
                ", iptc4xmpcoreLocation = ?" + // NOI18N -- 7 --
                ", iptc4xmpcoreCountrycode = ?" + // NOI18N -- 8 --
                ", photoshopCategory = ?" + // NOI18N -- 9 --
                ", photoshopSupplementalCategories = ?" + // NOI18N -- 10 --
                ", dcRights = ?" + // NOI18N -- 11 --
                ", dcCreator = ?" + // NOI18N -- 12 --
                ", photoshopAuthorsposition = ?" + // NOI18N -- 13 --
                ", photoshopCity = ?" + // NOI18N -- 14 --
                ", photoshopState = ?" + // NOI18N -- 15 --
                ", photoshopCountry = ?" + // NOI18N -- 16 --
                ", photoshopTransmissionReference = ?" + // NOI18N -- 17 --
                ", photoshopInstructions = ?" + // NOI18N -- 18 --
                ", photoshopCredit = ?" + // NOI18N -- 19 --
                ", photoshopSource = ?" + // NOI18N -- 20 --
                " WHERE name = ?"); // NOI18N -- 21 --
            setMetadataEditTemplate(stmt, template);
            stmt.setString(21, template.getName());
            AppLog.logFiner(DatabaseMetadataEditTemplates.class, stmt.toString());
            int count = stmt.executeUpdate();
            connection.commit();
            updated = count > 0;
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseMetadataEditTemplates.class, ex);
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
            AppLog.logFiner(DatabaseMetadataEditTemplates.class, stmt.toString());
            int count = stmt.executeUpdate();
            connection.commit();
            renamed = count > 0;
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseMetadataEditTemplates.class, ex);
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
            AppLog.logFiner(DatabaseMetadataEditTemplates.class, stmt.toString());
            int count = stmt.executeUpdate();
            connection.commit();
            deleted = count > 0;
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseMetadataEditTemplates.class, ex);
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
            AppLog.logFinest(DatabaseMetadataEditTemplates.class, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseMetadataEditTemplates.class, ex);
        } finally {
            free(connection);
        }
        return exists;
    }

}
