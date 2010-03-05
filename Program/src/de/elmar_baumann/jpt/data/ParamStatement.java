/*
 * JPhotoTagger tags and finds images fast
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
package de.elmar_baumann.jpt.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parametrisiertes SQL-Statement.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ParamStatement {

    private String   sql;
    private Object[] values;
    private boolean  isQuery;
    private String   name;

    public ParamStatement() {
    }

    /**
     * Konstruktor.
     *
     * @param sql      SQL-Statement-String
     * @param values   Parameterwerte
     * @param isQuery  true, wenn das Statement eine Abfrage ist
     * @param name     Name
     */
    public ParamStatement(
            String sql, Object[] values, boolean isQuery, String name) {

        this.sql = sql;
        this.values = values == null ? null : Arrays.copyOf(values, values.length);
        this.isQuery = isQuery;
        this.name = name;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     *
     * @return parameter values or null
     */
    public Object[] getValues() {
        return values == null
               ? null
               : Arrays.copyOf(values, values.length);
    }

    /**
     *
     * @param values parameter values. Default: null.
     */
    public void setValues(Object[] values) {
        if (values == null) {
            this.values = null;
        } else {
            this.values = Arrays.copyOf(values, values.length);
        }
    }

    public List<String> getValuesAsStringList() {
        if (values == null) return new ArrayList<String>();
        List<String> list = new ArrayList<String>();
        for (int index = 0; index < values.length; index++) {
            list.add(values[index].toString());
        }
        return list;
    }

    public boolean isQuery() {
        return isQuery;
    }

    public void setIsQuery(boolean isQuery) {
        this.isQuery = isQuery;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        String sqlString = sql == null
                           ? "Sql: null"
                           : "Sql: " + sql;
        return sqlString + " Values:" + getValuesString(" ");
    }

    private String getValuesString(String delimiter) {
        if (getValues() == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getValues().length; i++) {
            sb.append(delimiter + getValues()[i].toString());
        }
        return sb.toString();
    }
}
