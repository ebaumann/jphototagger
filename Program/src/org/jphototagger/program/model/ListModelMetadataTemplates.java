/*
 * @(#)ListModelMetadataTemplates.java    Created on 2010-01-05
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

package org.jphototagger.program.model;

import org.jphototagger.program.data.MetadataTemplate;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseMetadataTemplates;
import org.jphototagger.program.event.listener
    .DatabaseMetadataTemplatesListener;

import javax.swing.DefaultListModel;

/**
 * Elements are {@link MetadataTemplate}s retrieved through
 * {@link DatabaseMetadataTemplates#getAll()}.
 *
 * @author  Elmar Baumann
 */
public final class ListModelMetadataTemplates extends DefaultListModel
        implements DatabaseMetadataTemplatesListener {
    private static final long serialVersionUID = -1726658041913008196L;

    public ListModelMetadataTemplates() {
        addElements();
        DatabaseMetadataTemplates.INSTANCE.addListener(this);
    }

    private void addElements() {
        if (!ConnectionPool.INSTANCE.isInit()) {
            return;
        }

        for (MetadataTemplate t : DatabaseMetadataTemplates.INSTANCE.getAll()) {
            addElement(t);
        }
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
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        removeElement(template);
    }

    @Override
    public void templateInserted(MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        addElement(template);
    }

    @Override
    public void templateUpdated(MetadataTemplate oldTemplate,
                                MetadataTemplate updatedTemplate) {
        if (oldTemplate == null) {
            throw new NullPointerException("oldTemplate == null");
        }

        if (updatedTemplate == null) {
            throw new NullPointerException("updatedTemplate == null");
        }

        int index = indexOfTemplate(oldTemplate.getName());

        if (index >= 0) {
            fireContentsChanged(this, index, index);
        }
    }

    @Override
    public void templateRenamed(String fromName, String toName) {
        if (fromName == null) {
            throw new NullPointerException("fromName == null");
        }

        if (toName == null) {
            throw new NullPointerException("toName == null");
        }

        int index = indexOfTemplate(fromName);

        if (index >= 0) {
            MetadataTemplate template = (MetadataTemplate) getElementAt(index);

            template.setName(toName);
            fireContentsChanged(this, index, index);
        }
    }
}
