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

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.lib.componentutil.InputVerifierMaxLength;

import java.text.DateFormat;

import javax.swing.InputVerifier;

/**
 * Database column.
 *
 * @author  Elmar Baumann
 * @version 2007-07-29
 */
public class Column {
    private final DataType dataType;
    private String         description;
    private int            length;
    private String         longerDescription;
    private final String   name;
    private final String   tablename;

    /**
     * Creates an instances.
     *
     * @param name      name of this column
     * @param tablename name of the table of this column
     * @param dataType  data type of this column
     */
    protected Column(String name, String tablename, DataType dataType) {
        this.name     = name;
        this.tablename    = tablename;
        this.dataType = dataType;
    }

    /**
     * Typ der Spaltendaten.
     */
    public enum DataType {

        /** Binärdaten, Java-Typ: byte[] */
        BINARY,

        /** Datum, Java-Typ: java.sql.DATE */
        DATE,

        /** Ganzzahl, Java-Typ: int */
        INTEGER,

        /** Java-Typ: long */
        BIGINT,

        /** Realzahl, Java-Typ: double */
        REAL,

        /** kleine Ganzzahl, Java-Typ: short */
        SMALLINT,

        /** Zeichenkette variabler Länge, Java-Typ: java.lang.STRING */
        STRING
        ;

        public Object fromString(String string) {
            switch (this) {
            case BINARY :
                return string.getBytes();

            case DATE :
                return string2date(string);

            case INTEGER :
                return Integer.parseInt(string);

            case BIGINT :
                return Long.parseLong(string);

            case REAL :
                return Double.parseDouble(string);

            case SMALLINT :
                return Short.parseShort(string);

            case STRING :
                return string;

            default :
                return string;
            }
        }

        private Object string2date(String s) {
            try {
                return new java.sql.Date(
                    DateFormat.getInstance().parse(s).getTime());
            } catch (Exception ex) {
                AppLogger.logSevere(Column.class, ex);
            }

            return s;
        }
    }

    public String getTablename() {
        return tablename;
    }

    @Override
    public String toString() {
        String desc = getDescription();

        if (desc.isEmpty()) {
            return name;
        }

        return desc;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Column) {
            Column other = (Column) o;

            return tablename.equals(other.tablename)
                   && getName().equals(other.getName());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 83 * hash + ((this.tablename != null)
                            ? this.tablename.hashCode()
                            : 0);
        hash = 83 * hash + ((this.getName() != null)
                            ? this.getName().hashCode()
                            : 0);

        return hash;
    }

    /**
     * Returns the maximum length of this column.
     *
     * @return maximum length of this column
     */
    public int getLength() {
        return length;
    }

    /**
     * Sets the maximum length of this column.
     *
     * @param length maximum length. Default: 0.
     */
    protected void setLength(int length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns the longer description.
     *
     * @return description or null if not set
     */
    public String getLongerDescription() {
        return longerDescription;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets a longer description e.g. for tooltip texts.
     *
     * @param description description
     */
    protected void setLongerDescription(String description) {
        longerDescription = description;
    }

    public DataType getDataType() {
        return dataType;
    }

    /**
     * Returns an appropriate input verifier for a text component.
     *
     * @return this class returns an {@link InputVerifierMaxLength} with this
     *         column's length. Specialized classes can return an instance with
     *         a better verification.
     */
    public InputVerifier getInputVerifier() {
        return new InputVerifierMaxLength(getLength());
    }
}
