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

package de.elmar_baumann.jpt.view.renderer;

import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.selections.TableIcons;

import javax.swing.JLabel;

/**
 * Renderd eine Tabellenspalte.
 *
 * @author  Elmar Baumann
 * @version 2008-09-17
 */
public final class FormatterLabelTableColumn {

    /**
     * Setzt Icon und Text eines Labels, das eine Tabellenspalte darstellt.
     *
     * @param label   Label
     * @param column  Tabellenspalte
     */
    public static void setLabelText(JLabel label, Column column) {
        label.setIcon(TableIcons.getIcon(column.getTable()));
        label.setText(column.getDescription());
    }

    protected FormatterLabelTableColumn() {}
}
