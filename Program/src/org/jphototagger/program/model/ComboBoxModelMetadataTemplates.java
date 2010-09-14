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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.model;

import java.awt.EventQueue;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.MetadataTemplate;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseMetadataTemplates;
import org.jphototagger.program.event.listener
    .DatabaseMetadataTemplatesListener;
import org.jphototagger.program.resource.JptBundle;

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
    private final DatabaseMetadataTemplates db =
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
    public void delete(final MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

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
    public void insert(final MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

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
    public void update(final MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

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
    public void rename(final MetadataTemplate template, final String newName) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        if (newName == null) {
            throw new NullPointerException("newName == null");
        }

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

    private void updateTemplate(MetadataTemplate template) {
        int index = indexOfTemplate(template.getName());

        if (index >= 0) {
            fireContentsChanged(this, index, index);
        }
    }

    private void renameTemplate(String fromName, String toName) {
        int index = indexOfTemplate(fromName);

        if (index >= 0) {
            MetadataTemplate template = (MetadataTemplate) getElementAt(index);

            template.setName(toName);
            fireContentsChanged(this, index, index);
        }
    }

    private void addElements() {
        if (!ConnectionPool.INSTANCE.isInit()) {
            return;
        }

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
    public void templateDeleted(final MetadataTemplate template) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                removeElement(template);
            }
        });
    }

    @Override
    public void templateInserted(final MetadataTemplate template) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                addElement(template);
            }
        });
    }

    @Override
    public void templateUpdated(final MetadataTemplate oldTemplate,
                                MetadataTemplate updatedTemplate) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateTemplate(oldTemplate);
                }
        });
    }

    @Override
    public void templateRenamed(final String fromName, final String toName) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                renameTemplate(fromName, toName);
                }
        });
    }
}
