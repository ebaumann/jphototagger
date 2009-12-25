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
package de.elmar_baumann.jpt.view.renderer;

import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.selections.ColumnIcons;
import de.elmar_baumann.jpt.model.ComboBoxModelFastSearch;
import de.elmar_baumann.jpt.resource.Bundle;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Renders elements of a {@link ComboBoxModelFastSearch}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-31
 */
public final class ListCellRendererFastSearchColumns extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        if (value instanceof Column) {
            Column column = (Column) value;
            label.setText(column.getDescription());
            label.setIcon(ColumnIcons.getIcon(column));
        } else if (value.equals(ComboBoxModelFastSearch.ALL_DEFINED_COLUMNS)) {
            label.setText(Bundle.getString(
                    "ListCellRendererFastSearchColumns.Text.AllDefinedColumns"));
        } else {
            assert false : "Undefined value: " + value;
        }
        return label;
    }
}
