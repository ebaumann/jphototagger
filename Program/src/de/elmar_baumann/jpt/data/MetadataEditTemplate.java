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

import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.Table;
import de.elmar_baumann.jpt.database.metadata.xmp.XmpTables;
import java.util.HashMap;

/**
 * Holds the data of a metadata edit template.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-22
 */
public final class MetadataEditTemplate {

    private String                        name;
    private final HashMap<Column, Object> fieldOfColumn = new HashMap<Column, Object>();

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
        return name != null && !name.trim().isEmpty();
    }

    /**
     * Returns a value of a XMP column.
     *
     * @param  column column
     * @return        value or null if the column has no value
     */
    public Object getValueOfColumn(Column column) {
        return fieldOfColumn.get(column);
    }

    /**
     * Setzt den Wert einer XMP-Spalte.
     *
     * @param column  Spalte
     * @param data    Wert
     */
    @SuppressWarnings("unchecked")
    public void setValueOfColumn(Column column, Object data) {
        fieldOfColumn.put(column, data);
    }

    @Override
    public String toString() {
        return name == null ? "" : name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MetadataEditTemplate other = (MetadataEditTemplate) obj;
        if (this.name == null || !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.name != null
                            ? this.name.hashCode()
                            : 0);
        return hash;
    }

    public void setXmp(Xmp xmp) {
        for (Table xmpTable : XmpTables.get()) {
            for (Column column : xmpTable.getColumns()) {
                if (!column.isPrimaryKey() && ! column.isForeignKey()) {
                    fieldOfColumn.put(column, xmp.getValue(column));
                }
            }
        }
    }
}
