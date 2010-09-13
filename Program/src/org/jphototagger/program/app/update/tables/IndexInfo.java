/*
 * @(#)IndexInfo.java    Created on 2009-09-11
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Information about an table index.
 *
 * @author  Elmar Baumann
 */
public class IndexInfo {
    private final String       tableName;
    private final String       indexName;
    private final boolean      unique;
    private final List<String> columnNames = new ArrayList<String>();

    public IndexInfo(boolean unique, String indexName, String tableName,
                     String columnName, String... columnNames) {
        this.unique    = unique;
        this.indexName = indexName;
        this.tableName = tableName;
        this.columnNames.add(columnName);
        this.columnNames.addAll(Arrays.asList(columnNames));
    }

    public String sql() {
        return "CREATE" + (unique
                           ? " UNIQUE"
                           : "") + " INDEX " + indexName + " ON " + tableName
                                 + getColumnsClause();
    }

    private String getColumnsClause() {
        StringBuilder sb = new StringBuilder(" (");
        int           i  = 0;

        for (String columnName : columnNames) {
            sb.append((i++ == 0)
                      ? ""
                      : ", ").append(columnName);
        }

        sb.append(")");

        return sb.toString();
    }
}
