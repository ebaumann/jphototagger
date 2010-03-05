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
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Renderer für Tabellenspaltenbeschreibungen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-13
 * @see     de.elmar_baumann.jpt.database.metadata.Column#getDescription()
 */
public final class ListCellRendererTableColumns extends DefaultListCellRenderer {

    private static final long serialVersionUID = -3987847245199721880L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        FormatterLabelTableColumn.setLabelText(label, (Column) value);
        return label;
    }
}
