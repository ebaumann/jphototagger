/*
 * @(#)Column.java    2007-07-29
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

package de.elmar_baumann.jpt.database.metadata;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.lib.componentutil.InputVerifierAlwaysTrue;
import de.elmar_baumann.lib.componentutil.InputVerifierDate;
import de.elmar_baumann.lib.componentutil.InputVerifierMaxLength;
import de.elmar_baumann.lib.componentutil.InputVerifierNumber;

import java.text.DateFormat;

import javax.swing.InputVerifier;

/**
 * Database column containing metadata with the user acts on.
 *
 * @author  Elmar Baumann
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
        if (name == null) {
            throw new NullPointerException("name == null");
        }

        if (tablename == null) {
            throw new NullPointerException("tablename == null");
        }

        if (dataType == null) {
            throw new NullPointerException("dataType == null");
        }

        this.name      = name;
        this.tablename = tablename;
        this.dataType  = dataType;
    }

    /**
     * Data type of this column.
     */
    public enum DataType {

        /** Java type <code>byte[]</code> */
        BINARY(InputVerifierAlwaysTrue.INSTANCE),

        /** Java type <code>java.sql.Date</code> */
        DATE(new InputVerifierDate("yyyy-MM-dd")),

        /** Java type <code>int</code> */
        INTEGER(InputVerifierNumber.INSTANCE),

        /** Java type <code>long</code> */
        BIGINT(InputVerifierNumber.INSTANCE),

        /** Java type <code>double</code> */
        REAL(InputVerifierNumber.INSTANCE),

        /** Java type <code>short</code> */
        SMALLINT(InputVerifierNumber.INSTANCE),

        /** Java type <code>java.lang.String</code> */
        STRING(InputVerifierAlwaysTrue.INSTANCE)
        ;

        private final InputVerifier defaultInputVerifier;

        private DataType(InputVerifier inputVerifier) {
            this.defaultInputVerifier = inputVerifier;
        }

        /**
         * Converts a string into the data type of this column.
         *
         * @param  string string
         * @return        data type
         */
        public Object parseString(String string) {
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
                throw new IllegalArgumentException("Not handled type: " + this);
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
        String desc = (description == null)
                      ? ""
                      : description.trim();

        return desc.isEmpty()
               ? name
               : desc;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Column) {
            Column other = (Column) o;

            return tablename.equals(other.tablename) && name.equals(other.name);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 83 * hash + ((tablename != null)
                            ? tablename.hashCode()
                            : 0);
        hash = 83 * hash + ((name != null)
                            ? name.hashCode()
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

    /**
     * Returns the description.
     *
     * @return description or null if not set
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the longer description.
     *
     * @return longer description or null if not set
     */
    public String getLongerDescription() {
        return longerDescription;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets a longer description e.g. for label and tooltip texts.
     *
     * @param description longer description
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
     * @return this class returns an unsophisticated input verifier dependend
     *         on the data type
     */
    public InputVerifier getInputVerifier() {
        return dataType.equals(DataType.STRING)
               ? new InputVerifierMaxLength(length)
               : dataType.defaultInputVerifier;
    }
}
