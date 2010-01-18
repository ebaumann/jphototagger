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
package de.elmar_baumann.jpt.model;

import de.elmar_baumann.jpt.database.metadata.Column;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;

/**
 * Elements are {@link JCheckBox}es with the text of
 * {@link de.elmar_baumann.jpt.database.metadata.Column#getDescription()}.
 *
 * This model is a for usage in a
 * {@link de.elmar_baumann.lib.component.CheckList}. All check boxes mapped
 * to the colums, where the column can be retrieved via the check box index
 * ({@link #getColumnAtIndex(int)}).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 * @see     de.elmar_baumann.lib.component.CheckList
 */
public final class ListModelSelectedColumns extends DefaultListModel {

    private static final long          serialVersionUID = 2895854023882249479L;
    private               List<Column> columns;

    /**
     * Constructor for selected columns.
     *
     * @param columns columns
     */
    public ListModelSelectedColumns(Collection<Column> columns) {
        this.columns = new ArrayList<Column>(columns);
        addElements();
    }

    private void addElements() {
        for (Column column : columns) {
            addElement(new JCheckBox(column.getDescription()));
        }
    }

    /**
     * Returns a column at an check box index.
     *
     * @param index index
     * @return      column or null on invalid indexes
     */
    public Column getColumnAtIndex(int index) {
        return columns.get(index);
    }
}
