/*
 * @(#)IndexOfColumn.java    Created on 2008-11-06
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

package org.jphototagger.program.app.update.tables;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class IndexOfColumn {
    private final String  tableName;
    private final String  columnName;
    private final String  indexName;
    private final boolean unique;

    public IndexOfColumn(String tableName, String columnName, String indexName,
                  boolean unique) {
        this.tableName  = tableName;
        this.columnName = columnName;
        this.indexName  = indexName;
        this.unique     = unique;
    }

    public String getSql() {
        return "CREATE" + (unique
                           ? " UNIQUE INDEX "
                           : " INDEX ") + indexName + " ON " + tableName + " ("
                                        + columnName + ")";
    }
}
