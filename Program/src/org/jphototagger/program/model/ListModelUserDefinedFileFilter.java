/*
 * @(#)ListModelUserDefinedFileFilter.java    Created on 2010-03-30
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

import org.jphototagger.program.data.UserDefinedFileFilter;
import org.jphototagger.program.database.DatabaseUserDefinedFileFilters;
import org.jphototagger.program.event.listener
    .DatabaseUserDefinedFileFiltersListener;

import javax.swing.DefaultListModel;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ListModelUserDefinedFileFilter extends DefaultListModel
        implements DatabaseUserDefinedFileFiltersListener {
    private static final long serialVersionUID = 6723254193291648654L;

    public ListModelUserDefinedFileFilter() {
        addElements();
        DatabaseUserDefinedFileFilters.INSTANCE.addListener(this);
    }

    private void addElements() {
        for (UserDefinedFileFilter filter :
                DatabaseUserDefinedFileFilters.INSTANCE.getAll()) {
            addElement(filter);
        }
    }

    @Override
    public void filterInserted(UserDefinedFileFilter filter) {
        addElement(filter);
    }

    @Override
    public void filterDeleted(UserDefinedFileFilter filter) {
        removeElement(filter);
    }

    @Override
    public void filterUpdated(UserDefinedFileFilter filter) {
        int index = indexOf(filter);

        if (index >= 0) {
            ((UserDefinedFileFilter) getElementAt(index)).set(filter);
            fireContentsChanged(this, index, index);
        }
    }
}
