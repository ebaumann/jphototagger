/*
 * @(#)ComboBoxModelRenameTemplates.java    Created on 2010-03-01
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
import org.jphototagger.program.data.RenameTemplate;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseRenameTemplates;
import org.jphototagger.program.event.listener.DatabaseRenameTemplatesListener;

import javax.swing.DefaultComboBoxModel;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class ComboBoxModelRenameTemplates extends DefaultComboBoxModel
        implements DatabaseRenameTemplatesListener {
    private static final long serialVersionUID = -5081726761734936168L;

    public ComboBoxModelRenameTemplates() {
        addElements();
        DatabaseRenameTemplates.INSTANCE.addListener(this);
    }

    private void addElements() {
        if (!ConnectionPool.INSTANCE.isInit()) {
            return;
        }

        for (RenameTemplate template :
                DatabaseRenameTemplates.INSTANCE.getAll()) {
            addElement(template);
        }
    }

    private void updateTemplate(RenameTemplate template) {
        int index = getIndexOf(template);

        if (index >= 0) {
            ((RenameTemplate) getElementAt(index)).set(template);
            fireContentsChanged(this, index, index);
        }
    }

    private void insertTemplate(RenameTemplate template) {
        addElement(template);
        setSelectedItem(template);
    }

    private void deleteTemplate(RenameTemplate template) {
        removeElement(template);
    }

            @Override
    public void templateDeleted(final RenameTemplate template) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                deleteTemplate(template);
            }
        });
    }

    @Override
    public void templateInserted(final RenameTemplate template) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                insertTemplate(template);
            }
        });
    }

    @Override
    public void templateUpdated(final RenameTemplate template) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateTemplate(template);
                }
        });
    }
}
