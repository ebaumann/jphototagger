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

package de.elmar_baumann.jpt.app.update.tables;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2008-11-06
 */
final class ColumnInfo {
    private final String        tableName;
    private final String        columnName;
    private final String        dataType;
    private final IndexOfColumn index;

    public ColumnInfo(String tableName, String columnName, String dataType,
                      IndexOfColumn index) {
        this.tableName  = tableName;
        this.columnName = columnName;
        this.dataType   = dataType;
        this.index      = index;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @return the columnName
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @return the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * @return the index
     */
    public IndexOfColumn getIndex() {
        return index;
    }
}
