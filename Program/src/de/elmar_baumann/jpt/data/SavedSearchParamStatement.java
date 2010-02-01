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
package de.elmar_baumann.jpt.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Daten eines Objekts der Klasse {@link de.elmar_baumann.jpt.data.ParamStatement}
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-12
 */
public final class SavedSearchParamStatement {

    private String       name;
    private String       sql;
    private List<String> values;
    private boolean      query;

    public SavedSearchParamStatement() {
    }

    public SavedSearchParamStatement(SavedSearchParamStatement other) {
        set(other);
    }

    public void set(SavedSearchParamStatement other) {
        if (other == this) return;

        this.name   = other.name;
        this.sql    = other.sql;
        this.values = other.values == null
                          ? null
                          : new ArrayList<String>(other.values);
        this.query  = other.query;
    }

    /**
     * Liefert den Namen.
     *
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Setzt den Namen.
     *
     * @param name Name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Liefert, ob das Statement eine Abrage ist.
     *
     * @return true bei einer Abfrage, false bei einem Update
     */
    public boolean isQuery() {
        return query;
    }

    /**
     * Setzt, ob das Statement eine Abfrage ist.
     *
     * @param query true bei einer Abfrage, false bei einem Update
     */
    public void setQuery(boolean query) {
        this.query = query;
    }

    /**
     * Liefert das Statement.
     *
     * @return Statement
     */
    public String getSql() {
        return sql;
    }

    /**
     * Setzt das Statement.
     *
     * @param sql Statement
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * Liefert die Werte.
     *
     * @return Werte
     */
    public List<String> getValues() {
        return values == null
               ? null
               : new ArrayList<String>(values);
    }

    /**
     * Setzt die Werte.
     *
     * @param values Werte
     */
    public void setValues(List<String> values) {
        this.values = values == null
                      ? null
                      : new ArrayList<String>(values);
    }

    /**
     * Erzeugt ein parametrisiertes Statement aus den Daten.
     *
     * @return Statement oder null, wenn die Daten ungültig sind
     */
    public ParamStatement createParamStatement() {
        ParamStatement stmt = null;
        if (!sql.isEmpty()) {
            stmt = new ParamStatement();
            stmt.setSql(sql);
            stmt.setIsQuery(query);
            stmt.setName(name);
            if (values != null) {
                stmt.setValues(values.toArray());
            }
        }
        return stmt;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SavedSearchParamStatement other = (SavedSearchParamStatement) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.name != null
                            ? this.name.hashCode()
                            : 0);
        return hash;
    }
}
