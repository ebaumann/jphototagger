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
package de.elmar_baumann.jpt.model;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.MetadataTemplate;
import de.elmar_baumann.jpt.database.DatabaseMetadataTemplates;
import de.elmar_baumann.jpt.event.DatabaseMetadataTemplatesEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseMetadataTemplatesListener;
import de.elmar_baumann.jpt.resource.JptBundle;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/**
 * Elements are instances of {@link MetadataTemplate}s retrieved through
 * {@link DatabaseMetadataTemplates#getAll()}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ComboBoxModelMetadataTemplates
        extends    DefaultComboBoxModel
        implements DatabaseMetadataTemplatesListener
{
    private static final long                      serialVersionUID = 7895253533969078904L;
    private final        DatabaseMetadataTemplates db               = DatabaseMetadataTemplates.INSTANCE;

    public ComboBoxModelMetadataTemplates() {
        addElements();
        db.addListener(this);
    }

    /**
     * Löscht ein Template.
     *
     * @param template  Template
     */
    public void delete(MetadataTemplate template) {
        if (getIndexOf(template) >= 0 && db.delete(template.getName())) {
            removeElement(template);
        } else {
            errorMessage(template.getName(), JptBundle.INSTANCE.getString("ComboBoxModelMetadataTemplates.Error.ParamDelete"));
        }
    }

    /**
     * Fügt ein Template hinzu.
     *
     * @param template  Template
     */
    public void insert(MetadataTemplate template) {
        if (getIndexOf(template) >= 0) return;
        
        if (db.insertOrUpdate(template)) {
            addElement(template);
            setSelectedItem(template);
        } else {
            errorMessage(template.getName(), JptBundle.INSTANCE.getString("ComboBoxModelMetadataTemplates.Error.ParamInsert"));
        }
    }

    /**
     * Aktualisiert die Daten eines Templates.
     *
     * @param template  Template
     */
    public void update(MetadataTemplate template) {
        int index = getIndexOf(template);
        if (index >= 0 && db.update(template)) {
            removeElementAt(index);
            insertElementAt(template, index);
            setSelectedItem(template);
        } else {
            errorMessage(template.getName(), JptBundle.INSTANCE.getString("ComboBoxModelMetadataTemplates.Error.ParamUpdate"));
        }

    }

    /**
     * Benennt ein Template um.
     *
     * @param template  Template
     * @param newName   Neuer Name
     */
    public void rename(MetadataTemplate template, String newName) {
        int index = getIndexOf(template);
        if (index >= 0 && db.updateRename(template.getName(), newName)) {
            template.setName(newName);
            removeElementAt(index);
            insertElementAt(template, index);
            setSelectedItem(template);
        } else {
            errorMessage(template.getName(), JptBundle.INSTANCE.getString("ComboBoxModelMetadataTemplates.Error.ParamRename"));
        }
    }

    private void addElements() {
        List<MetadataTemplate> templates = db.getAll();
        for (MetadataTemplate template : templates) {
            addElement(template);
        }
    }

    private void errorMessage(String name, String cause) {
        MessageDisplayer.error(null, "ComboBoxModelMetadataTemplates.Error.Template", name, cause);
    }

    @Override
    public void actionPerformed(DatabaseMetadataTemplatesEvent evt) {

        if (evt.wasAdded()) {
            addElement(evt.getTemplate());
        } else if (evt.wasDeleted()) {
            removeElement(evt.getTemplate());
        } else if (evt.wasUpdated()) {
            int index = getIndexOf(evt.getOldTemplate());
            if (index >= 0) {
                removeElementAt(index);
                insertElementAt(evt.getTemplate(), index);
                if (index == 0) setSelectedItem(evt.getTemplate());
            }
        }
    }
}
