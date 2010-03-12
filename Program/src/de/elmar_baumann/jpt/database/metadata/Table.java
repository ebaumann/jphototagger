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

package de.elmar_baumann.jpt.database.metadata;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author  Elmar Baumann
 * @version 2008-10-05
 */
public abstract class Table {
    private final Set<Column> columns = new HashSet<Column>();
    private final String       name;

    protected Table(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Table) {
            Table otherTable = (Table) o;

            return name.equals(otherTable.name);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 29 * hash + ((this.name != null)
                            ? this.name.hashCode()
                            : 0);

        return hash;
    }

    protected void addColumn(Column column) {
        column.setTable(this);
        columns.add(column);
    }

    public Set<Column> getColumns() {
        if (columns.isEmpty()) {
            addColumns();
        }

        return new HashSet<Column>(columns);
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean contains(Column column) {
        return columns.contains(column);
    }

    /**
     * Abgeleitete Tabellen sollen ihre Spalten hinzufügen. Dies können sie nicht
     * im Konstruktor, da auch die Spalten Singletons sind, welche die Instanzen
     * der Tabellen aufrufen und es so zu einem Stackoverflow käme.
     */
    protected abstract void addColumns();
}
