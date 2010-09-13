/*
 * @(#)ListCellRendererFastSearchColumns.java    Created on 2009-08-31
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

package org.jphototagger.program.view.renderer;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.selections.ColumnIcons;
import org.jphototagger.program.model.ComboBoxModelFastSearch;
import org.jphototagger.program.resource.JptBundle;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Renders elements of a {@link ComboBoxModelFastSearch}.
 *
 * @author  Elmar Baumann
 */
public final class ListCellRendererFastSearchColumns
        extends DefaultListCellRenderer {
    private static final long serialVersionUID = 8142413010742459250L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value,
                           index, isSelected, cellHasFocus);

        if (value instanceof Column) {
            Column column = (Column) value;

            label.setText(column.getDescription());
            label.setIcon(ColumnIcons.getIcon(column));
        } else if ((value != null)
                   && value.equals(
                       ComboBoxModelFastSearch.ALL_DEFINED_COLUMNS)) {
            label.setText(
                JptBundle.INSTANCE.getString(
                    "ListCellRendererFastSearchColumns.Text.AllDefinedColumns"));
            label.setIcon(null);
        }

        return label;
    }
}
