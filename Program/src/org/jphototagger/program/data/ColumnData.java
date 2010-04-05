/*
 * @(#)ColumnData.java    Created on 2010-03-22
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

package org.jphototagger.program.data;

import org.jphototagger.program.database.metadata.Column;

/**
 * Data of a column.
 *
 * @author Elmar Baumann
 */
public final class ColumnData {
    private final Column column;
    private final Object data;

    public ColumnData(Column column, Object data) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        this.column = column;
        this.data   = data;
    }

    public Column getColumn() {
        return column;
    }

    public Object getData() {
        return data;
    }
}
