/*
 * @(#)TableCellRendererDatabaseInfoColumns.java    Created on 2008-09-17
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

package de.elmar_baumann.jpt.view.renderer;

import de.elmar_baumann.jpt.database.metadata.Column;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Rendert die Tabellenzellen der Datenbankinformation über die Anzahl der
 * Datensätze bezogen auf eine bestimmte Tabellenspalte.
 *
 * @author  Elmar Baumann
 */
public final class TableCellRendererDatabaseInfoColumns
        implements TableCellRenderer {
    private static final String PADDING_LEFT = "  ";

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel cellLabel = new JLabel();

        if (column == 0) {
            FormatterLabelTableColumn.setLabelText(cellLabel, (Column) value);
        } else {
            cellLabel.setText(PADDING_LEFT + value.toString());
        }

        return cellLabel;
    }
}
