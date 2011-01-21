package org.jphototagger.program.database.metadata;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.lib.inputverifier.InputVerifierAlwaysTrue;
import org.jphototagger.lib.inputverifier.InputVerifierDate;
import org.jphototagger.lib.inputverifier.InputVerifierMaxLength;
import org.jphototagger.lib.inputverifier.InputVerifierNumber;

import java.text.DateFormat;

import javax.swing.InputVerifier;

/**
 * Database column containing metadata with the user acts on.
 *
 * @author Elmar Baumann
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
        if (description == null) {
            throw new NullPointerException("description == null");
        }

        this.description = description;
    }

    /**
     * Sets a longer description e.g. for label and tooltip texts.
     *
     * @param description longer description
     */
    protected void setLongerDescription(String description) {
        if (description == null) {
            throw new NullPointerException("description == null");
        }

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
