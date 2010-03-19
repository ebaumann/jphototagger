/*
 * @(#)ComboBoxModelFastSearch.java    Created on 2009-08-31
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

import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.selections.FastSearchColumns;

import javax.swing.DefaultComboBoxModel;

/**
 * Elements are the columns for the fast search - instances of
 * {@link de.elmar_baumann.jpt.database.metadata.Column} - and a string.
 *
 * The elements retrieved through {@link FastSearchColumns#get()}. The string is
 * {@link #ALL_DEFINED_COLUMNS} and means, the fast search shall search
 * in all columns, else only in the selected column.
 *
 * @author  Elmar Baumann
 */
public final class ComboBoxModelFastSearch extends DefaultComboBoxModel {
    public static final String ALL_DEFINED_COLUMNS = "AllDefined";
    private static final long  serialVersionUID    = -705435864208734028L;

    public ComboBoxModelFastSearch() {
        addElements();
    }

    private void addElements() {
        addElement(ALL_DEFINED_COLUMNS);

        for (Column column : FastSearchColumns.get()) {
            addElement(column);
        }
    }
}
