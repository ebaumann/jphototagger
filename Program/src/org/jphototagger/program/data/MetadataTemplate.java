/*
 * @(#)MetadataTemplate.java    Created on 2008-09-22
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
import org.jphototagger.program.database.metadata.xmp.XmpColumns;

import java.util.HashMap;
import java.util.Set;

/**
 * Holds the data of a metadata edit template.
 *
 * @author Elmar Baumann
 */
public final class MetadataTemplate {
    private String                        name;
    private final HashMap<Column, Object> fieldOfColumn = new HashMap<Column,
                                                              Object>();

    /**
     * Returns the template's name.
     *
     * @return Name oder null if not defined
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Liefert, ob ein Name für das Template enthalten ist. Dieser ist
     * Identifikator.
     *
     * @return true, wenn ein Name vorhanden ist
     */
    public boolean hasName() {
        return (name != null) &&!name.trim().isEmpty();
    }

    /**
     * Returns a value of a XMP column.
     *
     * @param  column column
     * @return        value or null if the column has no value
     */
    public Object getValueOfColumn(Column column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        return fieldOfColumn.get(column);
    }

    /**
     * Setzt den Wert einer XMP-Spalte.
     *
     * @param column  Spalte
     * @param data    Wert
     */
    public void setValueOfColumn(Column column, Object data) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        if (data == null) {
            fieldOfColumn.remove(column);
        } else {
            fieldOfColumn.put(column, data);
        }
    }

    @Override

    // Never change this implementation (will be used to find model items)!
    public String toString() {
        return (name == null)
               ? ""
               : name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final MetadataTemplate other = (MetadataTemplate) obj;

        if ((this.name == null) ||!this.name.equals(other.name)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 67 * hash + ((this.name != null)
                            ? this.name.hashCode()
                            : 0);

        return hash;
    }

    public void setXmp(Xmp xmp) {
        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        for (Column column : XmpColumns.get()) {
            fieldOfColumn.put(column, xmp.getValue(column));
        }
    }

    public Set<Column> getColumns() {
        return fieldOfColumn.keySet();
    }
}
