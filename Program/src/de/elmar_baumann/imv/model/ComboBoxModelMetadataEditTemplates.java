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
package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.MetadataEditTemplate;
import de.elmar_baumann.imv.database.DatabaseMetadataEditTemplates;
import de.elmar_baumann.imv.resource.Bundle;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/**
 * Model mit
 * {@link de.elmar_baumann.imv.data.MetadataEditTemplate}-Objekten.
 * Führt alle Änderungen zuerst in der Datenbank aus und bei Erfolg im Model
 * selbst.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ComboBoxModelMetadataEditTemplates extends DefaultComboBoxModel {

    private final DatabaseMetadataEditTemplates db =
            DatabaseMetadataEditTemplates.INSTANCE;

    public ComboBoxModelMetadataEditTemplates() {
        addElements();
    }

    /**
     * Löscht ein Template.
     * 
     * @param template  Template
     */
    public void deleteMetadataEditTemplate(MetadataEditTemplate template) {
        if (getIndexOf(template) >= 0 &&
                db.deleteMetadataEditTemplate(template.getName())) {
            removeElement(template);
        } else {
            errorMessage(template.getName(),
                    Bundle.getString(
                    "ComboBoxModelMetadataEditTemplates.Error.ParamDelete")); // NOI18N
        }
    }

    /**
     * Fügt ein Template hinzu.
     * 
     * @param template  Template
     */
    public void insertTemplate(MetadataEditTemplate template) {
        if (getIndexOf(template) >= 0) {
            return;
        }
        if (db.insertMetadataEditTemplate(template)) {
            addElement(template);
            setSelectedItem(template);
        } else {
            errorMessage(template.getName(),
                    Bundle.getString(
                    "ComboBoxModelMetadataEditTemplates.Error.ParamInsert")); // NOI18N
        }
    }

    /**
     * Aktualisiert die Daten eines Templates.
     * 
     * @param template  Template
     */
    public void updateTemplate(MetadataEditTemplate template) {
        int index = getIndexOf(template);
        if (index >= 0 && db.updateMetadataEditTemplate(template)) {
            removeElementAt(index);
            insertElementAt(template, index);
            setSelectedItem(template);
        } else {
            errorMessage(template.getName(),
                    Bundle.getString(
                    "ComboBoxModelMetadataEditTemplates.Error.ParamUpdate")); // NOI18N
        }

    }

    /**
     * Benennt ein Template um.
     * 
     * @param template  Template
     * @param newName   Neuer Name
     */
    public void renameTemplate(MetadataEditTemplate template, String newName) {
        int index = getIndexOf(template);
        if (index >= 0 &&
                db.updateRenameMetadataEditTemplate(template.getName(), newName)) {
            template.setName(newName);
            removeElementAt(index);
            insertElementAt(template, index);
            setSelectedItem(template);
        } else {
            errorMessage(template.getName(),
                    Bundle.getString(
                    "ComboBoxModelMetadataEditTemplates.Error.ParamRename")); // NOI18N
        }
    }

    private void addElements() {
        List<MetadataEditTemplate> templates = db.getMetadataEditTemplates();
        for (MetadataEditTemplate template : templates) {
            addElement(template);
        }
    }

    private void errorMessage(String name, String cause) {
        MessageDisplayer.error(null,
                "ComboBoxModelMetadataEditTemplates.Error.Template", // NOI18N
                name, cause);
    }
}
