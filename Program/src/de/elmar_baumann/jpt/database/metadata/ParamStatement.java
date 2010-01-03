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
package de.elmar_baumann.jpt.database.metadata;

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

    private String sql;
    private Object[] values;
    private boolean isQuery;
    private String name;

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
        this.values = values == null
                      ? null
                      : Arrays.copyOf(values, values.length);
        this.isQuery = isQuery;
        this.name = name;
    }

    /**
     * Liefert den SQL-String.
     *
     * @return SQL-String
     */
    public String getSql() {
        return sql;
    }

    /**
     * Setzt den SQL-String.
     *
     * @param sql SQL-String. Default: null.
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * Liefert die Parameterwerte.
     *
     * @return Parameterwerte oder null, wenn es keine gibt
     */
    public Object[] getValues() {
        return values == null
               ? null
               : Arrays.copyOf(values, values.length);
    }

    /**
     * Setzt die Parameterwerte.
     *
     * @param values Parameterwerte. Default: null.
     */
    public void setValues(Object[] values) {
        if (values == null) {
            this.values = null;
        } else {
            this.values = Arrays.copyOf(values, values.length);
        }
    }

    /**
     * Liefert die Werte als Liste.
     *
     * @return Werte als Stringarray
     */
    public List<String> getValuesAsStringList() {
        if (values == null) return new ArrayList<String>();
        List<String> array = new ArrayList<String>();
        for (int index = 0; index < values.length; index++) {
            array.add(values[index].toString());
        }
        return array;
    }

    /**
     * Liefert, ob das Statement eine Abfrage ist.
     *
     * @return true, wenn das Statement eine Abfrage ist
     */
    public boolean isQuery() {
        return isQuery;
    }

    /**
     * Setzt, ob das Statement eine Abfrage ist.
     *
     * @param isQuery true, wenn das Statement eine Abfrage ist.
     *     Default: true.
     */
    public void setIsQuery(boolean isQuery) {
        this.isQuery = isQuery;
    }

    /**
     * Liefert den Namen des Statements.
     *
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Setzt den Namen des Statements.
     *
     * @param name Name. Default: null.
     */
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
