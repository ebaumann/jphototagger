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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public abstract class Table {

    private final List<Column> columns          = new ArrayList<Column>();
    private final List<Column> referenceColumns = new ArrayList<Column>();
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
        hash = 29 * hash + (this.name != null
                            ? this.name.hashCode()
                            : 0);
        return hash;
    }

    protected void addColumn(Column column) {
        assert !columns.contains(column);
        column.setTable(this);
        columns.add(column);
        if (column.getReferences() != null) {
            referenceColumns.add(column);
        }
    }

    public List<Column> getColumns() {
        if (columns.isEmpty()) {
            addColumns();
        }
        return new ArrayList<Column>(columns);
    }

    /**
     * Liefert alle Spalten, die Spalten einer anderen Tabelle referenzieren.
     *
     * @return Referenzspalten
     */
    public List<Column> getReferenceColumns() {
        if (columns.isEmpty()) {
            addColumns();
        }
        return referenceColumns;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Liefert die Spalten, die eine bestimmte Tabelle referenzieren.
     *
     * @param table Tabelle
     * @return Spalten
     */
    public List<Column> getJoinColumnsFor(Table table) {
        List<Column> joinColumns = new ArrayList<Column>();
        for (Column column : referenceColumns) {
            Column referencedColumn = column.getReferences();
            if (referencedColumn.getTable().equals(table)) {
                joinColumns.add(column);
            }
        }
        return joinColumns;
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
