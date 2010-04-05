/*
 * @(#)ParamStatement.java    Created on 2008-10-05
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

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author  Elmar Baumann, Tobias Stening
 */
public final class ParamStatement {
    private String       sql;
    private List<String> values = new ArrayList<String>();
    private boolean      query;

    public ParamStatement() {}

    public ParamStatement(ParamStatement stmt) {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        set(stmt);
    }

    public void set(ParamStatement stmt) {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        sql    = stmt.sql;
        values = new ArrayList<String>(stmt.values);
        query  = stmt.query;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     *
     * @return parameter values or empty list
     */
    public List<String> getValues() {
        return new ArrayList<String>(values);
    }

    /**
     *
     * @param values parameter values
     */
    public void setValues(List<String> values) {
        this.values = (values == null)
                      ? new ArrayList<String>()
                      : new ArrayList<String>(values);
    }

    public boolean hasValues() {
        return !values.isEmpty();
    }

    public boolean isQuery() {
        return query;
    }

    public void setQuery(boolean query) {
        this.query = query;
    }
}
