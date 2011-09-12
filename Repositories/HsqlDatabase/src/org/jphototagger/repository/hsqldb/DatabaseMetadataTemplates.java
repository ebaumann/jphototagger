package org.jphototagger.repository.hsqldb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.EventBus;
import org.jphototagger.domain.metadata.xmp.XmpDcCreatorMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcDescriptionMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcRightsMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcTitleMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpIptc4XmpCoreDateCreatedMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpIptc4xmpcoreLocationMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopAuthorspositionMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCaptionwriterMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCityMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCountryMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCreditMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopHeadlineMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopInstructionsMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopSourceMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopStateMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopTransmissionReferenceMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpRatingMetaDataValue;
import org.jphototagger.domain.repository.event.metadatatemplates.MetadataTemplateDeletedEvent;
import org.jphototagger.domain.repository.event.metadatatemplates.MetadataTemplateInsertedEvent;
import org.jphototagger.domain.repository.event.metadatatemplates.MetadataTemplateRenamedEvent;
import org.jphototagger.domain.repository.event.metadatatemplates.MetadataTemplateUpdatedEvent;
import org.jphototagger.domain.templates.MetadataTemplate;

/**
 *
 *
 * @author Elmar Baumann
 */
final class DatabaseMetadataTemplates extends Database {

    private static final String DELIM_REPEATABLE_STRINGS = "\t";
    static final DatabaseMetadataTemplates INSTANCE = new DatabaseMetadataTemplates();

    private DatabaseMetadataTemplates() {
    }

    /**
     * Fügt ein neues Metadaten-Edit-Template ein. Existiert das Template
     * bereits, werden seine Daten aktualisiert.
     *
     * @param  template  Template
     * @return           true bei Erfolg
     */
    boolean insertOrUpdateMetadataTemplate(MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        if (existsMetadataTemplate(template.getName())) {
            return updateMetadataTemplate(template);
        }

        boolean inserted = false;
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);

            String sql = "INSERT INTO metadata_edit_templates ("
                    + "name" // --  1 --
                    + ", dcSubjects" // --  2 --
                    + ", dcTitle" // --  3 --
                    + ", photoshopHeadline" // --  4 --
                    + ", dcDescription" // --  5 --
                    + ", photoshopCaptionwriter" // --  6 --
                    + ", iptc4xmpcoreLocation" // --  7 --
                    + ", dcRights" // --  8 --
                    + ", dcCreator" // --  9 --
                    + ", photoshopAuthorsposition" // -- 10 --
                    + ", photoshopCity" // -- 11 --
                    + ", photoshopState" // -- 12 --
                    + ", photoshopCountry" // -- 13 --
                    + ", photoshopTransmissionReference" // -- 14 --
                    + ", photoshopInstructions" // -- 15 --
                    + ", photoshopCredit" // -- 16 --
                    + ", photoshopSource" // -- 17 --
                    + ", rating" // -- 18 --
                    + ", iptc4xmpcore_datecreated" // -- 19 --
                    + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?" + ", ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            stmt = con.prepareStatement(sql);
            set(stmt, template);
            logFiner(stmt);
            stmt.executeUpdate();
            con.commit();
            inserted = true;
            notifyInserted(template);
        } catch (Exception ex) {
            Logger.getLogger(DatabaseMetadataTemplates.class.getName()).log(Level.SEVERE, null, ex);
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
        stmt.setBytes(2, (template.getMetaDataValue(XmpDcSubjectsSubjectMetaDataValue.INSTANCE) == null)
                ? null
                : fromRepeatable(
                (Collection<String>) template.getMetaDataValue(
                XmpDcSubjectsSubjectMetaDataValue.INSTANCE)).getBytes());
        stmt.setBytes(3, (template.getMetaDataValue(XmpDcTitleMetaDataValue.INSTANCE) == null)
                ? null
                : ((String) template.getMetaDataValue(XmpDcTitleMetaDataValue.INSTANCE)).getBytes());
        stmt.setBytes(4, (template.getMetaDataValue(XmpPhotoshopHeadlineMetaDataValue.INSTANCE) == null)
                ? null
                : ((String) template.getMetaDataValue(XmpPhotoshopHeadlineMetaDataValue.INSTANCE)).getBytes());
        stmt.setBytes(5, (template.getMetaDataValue(XmpDcDescriptionMetaDataValue.INSTANCE) == null)
                ? null
                : ((String) template.getMetaDataValue(XmpDcDescriptionMetaDataValue.INSTANCE)).getBytes());
        stmt.setBytes(6, (template.getMetaDataValue(XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE) == null)
                ? null
                : ((String) template.getMetaDataValue(XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE)).getBytes());
        stmt.setBytes(7, (template.getMetaDataValue(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE) == null)
                ? null
                : ((String) template.getMetaDataValue(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE)).getBytes());
        stmt.setBytes(8, (template.getMetaDataValue(XmpDcRightsMetaDataValue.INSTANCE) == null)
                ? null
                : ((String) template.getMetaDataValue(XmpDcRightsMetaDataValue.INSTANCE)).getBytes());
        stmt.setBytes(9, (template.getMetaDataValue(XmpDcCreatorMetaDataValue.INSTANCE) == null)
                ? null
                : ((String) template.getMetaDataValue(XmpDcCreatorMetaDataValue.INSTANCE)).getBytes());
        stmt.setBytes(10, (template.getMetaDataValue(XmpPhotoshopAuthorspositionMetaDataValue.INSTANCE) == null)
                ? null
                : ((String) template.getMetaDataValue(
                XmpPhotoshopAuthorspositionMetaDataValue.INSTANCE)).getBytes());
        stmt.setBytes(11, (template.getMetaDataValue(XmpPhotoshopCityMetaDataValue.INSTANCE) == null)
                ? null
                : ((String) template.getMetaDataValue(XmpPhotoshopCityMetaDataValue.INSTANCE)).getBytes());
        stmt.setBytes(12, (template.getMetaDataValue(XmpPhotoshopStateMetaDataValue.INSTANCE) == null)
                ? null
                : ((String) template.getMetaDataValue(XmpPhotoshopStateMetaDataValue.INSTANCE)).getBytes());
        stmt.setBytes(13, (template.getMetaDataValue(XmpPhotoshopCountryMetaDataValue.INSTANCE) == null)
                ? null
                : ((String) template.getMetaDataValue(XmpPhotoshopCountryMetaDataValue.INSTANCE)).getBytes());
        stmt.setBytes(14, (template.getMetaDataValue(XmpPhotoshopTransmissionReferenceMetaDataValue.INSTANCE) == null)
                ? null
                : ((String) template.getMetaDataValue(
                XmpPhotoshopTransmissionReferenceMetaDataValue.INSTANCE)).getBytes());
        stmt.setBytes(15, (template.getMetaDataValue(XmpPhotoshopInstructionsMetaDataValue.INSTANCE) == null)
                ? null
                : ((String) template.getMetaDataValue(XmpPhotoshopInstructionsMetaDataValue.INSTANCE)).getBytes());
        stmt.setBytes(16, (template.getMetaDataValue(XmpPhotoshopCreditMetaDataValue.INSTANCE) == null)
                ? null
                : ((String) template.getMetaDataValue(XmpPhotoshopCreditMetaDataValue.INSTANCE)).getBytes());
        stmt.setBytes(17, (template.getMetaDataValue(XmpPhotoshopSourceMetaDataValue.INSTANCE) == null)
                ? null
                : ((String) template.getMetaDataValue(XmpPhotoshopSourceMetaDataValue.INSTANCE)).getBytes());
        stmt.setBytes(18, (template.getMetaDataValue(XmpRatingMetaDataValue.INSTANCE) == null)
                ? null
                : ((String) template.getMetaDataValue(XmpRatingMetaDataValue.INSTANCE)).getBytes());
        stmt.setBytes(19, (template.getMetaDataValue(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE) == null)
                ? null
                : ((String) template.getMetaDataValue(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE)).getBytes());
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
    MetadataTemplate findMetadataTemplate(String name) {
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
            Logger.getLogger(DatabaseMetadataTemplates.class.getName()).log(Level.SEVERE, null, ex);
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
    List<MetadataTemplate> getAllMetadataTemplates() {
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
            Logger.getLogger(DatabaseMetadataTemplates.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return templates;
    }

    private String getSelectForSetValues() {
        return "SELECT name" // --  1 --
                + ", dcSubjects" // --  2 --
                + ", dcTitle" // --  3 --
                + ", photoshopHeadline" // --  4 --
                + ", dcDescription" // --  5 --
                + ", photoshopCaptionwriter" // --  6 --
                + ", iptc4xmpcoreLocation" // --  7 --
                + ", dcRights" // --  8 --
                + ", dcCreator" // --  9 --
                + ", photoshopAuthorsposition" // -- 10 --
                + ", photoshopCity" // -- 11 --
                + ", photoshopState" // -- 12 --
                + ", photoshopCountry" // -- 13 --
                + ", photoshopTransmissionReference" // -- 14 --
                + ", photoshopInstructions" // -- 15 --
                + ", photoshopCredit" // -- 16 --
                + ", photoshopSource" // -- 17 --
                + ", rating" // -- 18 --
                + ", iptc4xmpcore_datecreated" // -- 19 --
                + " FROM metadata_edit_templates";
    }

    private void setValues(MetadataTemplate template, ResultSet rs) throws SQLException {
        byte[] bytes;

        template.setName(rs.getString(1));
        bytes = rs.getBytes(2);
        template.setMetaDataValue(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, rs.wasNull()
                ? null
                : toRepeatable(new String(bytes)));
        bytes = rs.getBytes(3);
        template.setMetaDataValue(XmpDcTitleMetaDataValue.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(4);
        template.setMetaDataValue(XmpPhotoshopHeadlineMetaDataValue.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(5);
        template.setMetaDataValue(XmpDcDescriptionMetaDataValue.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(6);
        template.setMetaDataValue(XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(7);
        template.setMetaDataValue(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(8);
        template.setMetaDataValue(XmpDcRightsMetaDataValue.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(9);
        template.setMetaDataValue(XmpDcCreatorMetaDataValue.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(10);
        template.setMetaDataValue(XmpPhotoshopAuthorspositionMetaDataValue.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(11);
        template.setMetaDataValue(XmpPhotoshopCityMetaDataValue.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(12);
        template.setMetaDataValue(XmpPhotoshopStateMetaDataValue.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(13);
        template.setMetaDataValue(XmpPhotoshopCountryMetaDataValue.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(14);
        template.setMetaDataValue(XmpPhotoshopTransmissionReferenceMetaDataValue.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(15);
        template.setMetaDataValue(XmpPhotoshopInstructionsMetaDataValue.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(16);
        template.setMetaDataValue(XmpPhotoshopCreditMetaDataValue.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(17);
        template.setMetaDataValue(XmpPhotoshopSourceMetaDataValue.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(18);
        template.setMetaDataValue(XmpRatingMetaDataValue.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
        bytes = rs.getBytes(19);
        template.setMetaDataValue(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE, rs.wasNull()
                ? null
                : new String(bytes));
    }

    /**
     * Aktualisiert ein Metadaten-Edit-Template.
     *
     * @param  template  Template
     * @return true bei Erfolg
     */
    boolean updateMetadataTemplate(MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        boolean updated = false;
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            String sql = "UPDATE metadata_edit_templates" + " SET name = ?" // --  1 --
                    + ", dcSubjects = ?" // --  2 --
                    + ", dcTitle = ?" // --  3 --
                    + ", photoshopHeadline = ?" // --  4 --
                    + ", dcDescription = ?" // --  5 --
                    + ", photoshopCaptionwriter = ?" // --  6 --
                    + ", iptc4xmpcoreLocation = ?" // --  7 --
                    + ", dcRights = ?" // --  8 --
                    + ", dcCreator = ?" // --  9 --
                    + ", photoshopAuthorsposition = ?" // -- 10 --
                    + ", photoshopCity = ?" // -- 11 --
                    + ", photoshopState = ?" // -- 12 --
                    + ", photoshopCountry = ?" // -- 13 --
                    + ", photoshopTransmissionReference = ?" // -- 14 --
                    + ", photoshopInstructions = ?" // -- 15 --
                    + ", photoshopCredit = ?" // -- 16 --
                    + ", photoshopSource = ?" // -- 17 --
                    + ", rating = ?" // -- 18 --
                    + ", iptc4xmpcore_datecreated = ?" // -- 19 --
                    + " WHERE name = ?";    // -- 20 --

            con = getConnection();
            con.setAutoCommit(false);

            MetadataTemplate oldTemplate = findMetadataTemplate(template.getName());

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
            Logger.getLogger(DatabaseMetadataTemplates.class.getName()).log(Level.SEVERE, null, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return updated;
    }

    boolean updateRenameMetadataTemplate(String fromName, String toName) {
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
            Logger.getLogger(DatabaseMetadataTemplates.class.getName()).log(Level.SEVERE, null, ex);
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
    boolean deleteMetadataTemplate(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }

        boolean deleted = false;
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);

            MetadataTemplate template = findMetadataTemplate(name);

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
            Logger.getLogger(DatabaseMetadataTemplates.class.getName()).log(Level.SEVERE, null, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return deleted;
    }

    boolean existsMetadataTemplate(String name) {
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
            Logger.getLogger(DatabaseMetadataTemplates.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return exists;
    }

    private void notifyDelted(MetadataTemplate template) {
        EventBus.publish(new MetadataTemplateDeletedEvent(this, template));
    }

    private void notifyInserted(MetadataTemplate template) {
        EventBus.publish(new MetadataTemplateInsertedEvent(this, template));
    }

    private void notifyUpdated(MetadataTemplate oldTemplate, MetadataTemplate updatedTemplate) {
        EventBus.publish(new MetadataTemplateUpdatedEvent(this, oldTemplate, updatedTemplate));
    }

    private void notifyRenamed(String fromName, String toName) {
        EventBus.publish(new MetadataTemplateRenamedEvent(this, fromName, toName));
    }
}
