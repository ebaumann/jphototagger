/*
 * @(#)ComboBoxModelMetadataTemplates.java    Created on 2008-10-05
 *
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
import de.elmar_baumann.jpt.event.listener.DatabaseMetadataTemplatesListener;
import de.elmar_baumann.jpt.resource.JptBundle;

import java.util.List;

import javax.swing.DefaultComboBoxModel;

/**
 * Elements are instances of {@link MetadataTemplate}s retrieved through
 * {@link DatabaseMetadataTemplates#getAll()}.
 *
 * @author  Elmar Baumann, Tobias Stening
 */
public final class ComboBoxModelMetadataTemplates extends DefaultComboBoxModel
        implements DatabaseMetadataTemplatesListener {
    private static final long               serialVersionUID =
        7895253533969078904L;
    private final DatabaseMetadataTemplates db               =
        DatabaseMetadataTemplates.INSTANCE;

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
        if ((getIndexOf(template) >= 0) && db.delete(template.getName())) {
            removeElement(template);
        } else {
            errorMessage(
                template.getName(),
                JptBundle.INSTANCE.getString(
                    "ComboBoxModelMetadataTemplates.Error.ParamDelete"));
        }
    }

    /**
     * Fügt ein Template hinzu.
     *
     * @param template  Template
     */
    public void insert(MetadataTemplate template) {
        if (getIndexOf(template) >= 0) {
            return;
        }

        if (db.insertOrUpdate(template)) {
            addElement(template);
            setSelectedItem(template);
        } else {
            errorMessage(
                template.getName(),
                JptBundle.INSTANCE.getString(
                    "ComboBoxModelMetadataTemplates.Error.ParamInsert"));
        }
    }

    /**
     * Aktualisiert die Daten eines Templates.
     *
     * @param template  Template
     */
    public void update(MetadataTemplate template) {
        int index = getIndexOf(template);

        if ((index >= 0) && db.update(template)) {
            removeElementAt(index);
            insertElementAt(template, index);
            setSelectedItem(template);
        } else {
            errorMessage(
                template.getName(),
                JptBundle.INSTANCE.getString(
                    "ComboBoxModelMetadataTemplates.Error.ParamUpdate"));
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

        if ((index >= 0) && db.updateRename(template.getName(), newName)) {
            template.setName(newName);
            removeElementAt(index);
            insertElementAt(template, index);
            setSelectedItem(template);
        } else {
            errorMessage(
                template.getName(),
                JptBundle.INSTANCE.getString(
                    "ComboBoxModelMetadataTemplates.Error.ParamRename"));
        }
    }

    private void addElements() {
        List<MetadataTemplate> templates = db.getAll();

        for (MetadataTemplate template : templates) {
            addElement(template);
        }
    }

    private void errorMessage(String name, String cause) {
        MessageDisplayer.error(null,
                               "ComboBoxModelMetadataTemplates.Error.Template",
                               name, cause);
    }

    private int indexOfTemplate(String name) {
        int size = getSize();

        for (int i = 0; i < size; i++) {
            Object o = getElementAt(i);

            if (o instanceof MetadataTemplate) {
                if (((MetadataTemplate) o).getName().equals(name)) {
                    return i;
                }
            }
        }

        return -1;
    }

    @Override
    public void templateDeleted(MetadataTemplate template) {
        removeElement(template);
    }

    @Override
    public void templateInserted(MetadataTemplate template) {
        addElement(template);
    }

    @Override
    public void templateUpdated(MetadataTemplate oldTemplate,
                                MetadataTemplate updatedTemplate) {
        int index = indexOfTemplate(oldTemplate.getName());

        if (index >= 0) {
            fireContentsChanged(this, index, index);
        }
    }

    @Override
    public void templateRenamed(String oldName, String newName) {
        int index = indexOfTemplate(oldName);

        if (index >= 0) {
            MetadataTemplate template = (MetadataTemplate) getElementAt(index);

            template.setName(newName);
            fireContentsChanged(this, index, index);
        }
    }
}
