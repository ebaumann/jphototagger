package de.elmar_baumann.imagemetadataviewer.model;

import de.elmar_baumann.imagemetadataviewer.AppSettings;
import de.elmar_baumann.imagemetadataviewer.data.MetaDataEditTemplate;
import de.elmar_baumann.imagemetadataviewer.database.Database;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import java.text.MessageFormat;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

/**
 * Model mit
 * {@link de.elmar_baumann.imagemetadataviewer.data.MetaDataEditTemplate}-Objekten.
 * Führt alle Änderungen zuerst in der Datenbank aus und bei Erfolg im Model
 * selbst.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/22
 */
public class ComboBoxModelMetaDataEditTemplates extends DefaultComboBoxModel {

    private Database db = Database.getInstance();

    public ComboBoxModelMetaDataEditTemplates() {
        addColumns();
    }

    /**
     * Löscht ein Template.
     * 
     * @param template  Template
     */
    public void deleteMetaDataEditTemplate(MetaDataEditTemplate template) {
        if (getIndexOf(template) >= 0 && db.isConnected() &&
            db.deleteMetaDataEditTemplate(template.getName())) {
            removeElement(template);
        } else {
            errorMessage(template.getName(), Bundle.getString("ComboBoxModelMetaDataEditTemplates.ErrorMessage.ParamDelete"));
        }
    }

    /**
     * Fügt ein Template hinzu.
     * 
     * @param template  Template
     */
    public void insertTemplate(MetaDataEditTemplate template) {
        if (getIndexOf(template) >= 0) {
            return;
        }
        if (db.isConnected() && db.insertMetaDataEditTemplate(template)) {
            addElement(template);
            setSelectedItem(template);
        } else {
            errorMessage(template.getName(), Bundle.getString("ComboBoxModelMetaDataEditTemplates.ErrorMessage.ParamInsert"));
        }
    }

    /**
     * Aktualisiert die Daten eines Templates.
     * 
     * @param template  Template
     */
    public void updateTemplate(MetaDataEditTemplate template) {
        int index = getIndexOf(template);
        if (index >= 0 && db.isConnected() && db.updateMetaDataEditTemplate(template)) {
            removeElementAt(index);
            insertElementAt(template, index);
            setSelectedItem(template);
        } else {
            errorMessage(template.getName(), Bundle.getString("ComboBoxModelMetaDataEditTemplates.ErrorMessage.ParamUpdate"));
        }

    }

    /**
     * Benennt ein Template um.
     * 
     * @param template  Template
     * @param newName   Neuer Name
     */
    public void renameTemplate(MetaDataEditTemplate template, String newName) {
        int index = getIndexOf(template);
        if (index >= 0 && db.isConnected() &&
            db.updateRenameMetaDataEditTemplate(template.getName(), newName)) {
            template.setName(newName);
            removeElementAt(index);
            insertElementAt(template, index);
            setSelectedItem(template);
        } else {
            errorMessage(template.getName(), Bundle.getString("ComboBoxModelMetaDataEditTemplates.ErrorMessage.ParamRename"));
        }
    }

    private void addColumns() {
        if (db.isConnected()) {
            Vector<MetaDataEditTemplate> templates = db.getMetaDataEditTemplates();
            for (MetaDataEditTemplate template : templates) {
                addElement(template);
            }
        }
    }

    private void errorMessage(String name, String cause) {
        MessageFormat msg = new MessageFormat(Bundle.getString("ComboBoxModelMetaDataEditTemplates.ErrorMessage.Template"));
        Object[] params = {name, cause};
        JOptionPane.showMessageDialog(
            null,
            msg.format(params),
            Bundle.getString("ComboBoxModelMetaDataEditTemplates.ErrorMessage.Template.Title"),
            JOptionPane.ERROR_MESSAGE,
            AppSettings.getSmallAppIcon());
    }
}
