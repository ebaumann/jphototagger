package org.jphototagger.domain.metadata;

import java.text.DateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.InputVerifier;
import javax.swing.text.DefaultFormatterFactory;

import org.jphototagger.lib.swing.inputverifier.AlwaysTrueInputVerifier;
import org.jphototagger.lib.swing.inputverifier.DateInputVerifier;
import org.jphototagger.lib.swing.inputverifier.MaxLengthInputVerifier;
import org.jphototagger.lib.swing.inputverifier.NumberInputVerifier;

/**
 * @author Elmar Baumann
 */
public class MetaDataValue {

    private final ValueType valueType;
    private String description;
    private int valueLength;
    private String longerDescription;
    private final String valueName;
    private final String category;

    protected MetaDataValue(String valueName, String category, ValueType valueType) {
        if (valueName == null) {
            throw new NullPointerException("valueName == null");
        }

        if (category == null) {
            throw new NullPointerException("category == null");
        }

        if (valueType == null) {
            throw new NullPointerException("valueType == null");
        }

        this.valueName = valueName;
        this.category = category;
        this.valueType = valueType;
    }

    /**
     * Data type of this value.
     */
    public enum ValueType {

        /** Java type <code>byte[]</code> */
        BINARY(AlwaysTrueInputVerifier.INSTANCE),
        /** Java type <code>java.sql.Date</code> */
        DATE(new DateInputVerifier("yyyy-MM-dd")),
        /** Java type <code>int</code> */
        INTEGER(NumberInputVerifier.INSTANCE),
        /** Java type <code>long</code> */
        BIGINT(NumberInputVerifier.INSTANCE),
        /** Java type <code>double</code> */
        REAL(NumberInputVerifier.INSTANCE),
        /** Java type <code>short</code> */
        SMALLINT(NumberInputVerifier.INSTANCE),
        /** Java type <code>java.lang.String</code> */
        STRING(AlwaysTrueInputVerifier.INSTANCE);
        private final InputVerifier defaultInputVerifier;

        private ValueType(InputVerifier inputVerifier) {
            this.defaultInputVerifier = inputVerifier;
        }

        /**
         * Converts a string into an appropriate value type.
         *
         * @param  string string
         * @return        value type
         */
        public Object parseString(String string) {
            switch (this) {
                case BINARY:
                    return string.getBytes();

                case DATE:
                    return string2date(string);

                case INTEGER:
                    return Integer.parseInt(string);

                case BIGINT:
                    return Long.parseLong(string);

                case REAL:
                    return Double.parseDouble(string);

                case SMALLINT:
                    return Short.parseShort(string);

                case STRING:
                    return string;

                default:
                    throw new IllegalArgumentException("Not handled type: " + this);
            }
        }

        private Object string2date(String s) {
            try {
                return new java.sql.Date(DateFormat.getInstance().parse(s).getTime());
            } catch (Exception ex) {
                Logger.getLogger(MetaDataValue.class.getName()).log(Level.SEVERE, null, ex);
            }

            return s;
        }
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        String desc = (description == null)
                ? ""
                : description.trim();

        return desc.isEmpty()
                ? valueName
                : desc;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof MetaDataValue)) {
            return false;
        }

        MetaDataValue other = (MetaDataValue) object;

        return valueName.equals(other.valueName) && category.equals(other.category);
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 83 * hash + ((category != null)
                ? category.hashCode()
                : 0);
        hash = 83 * hash + ((valueName != null)
                ? valueName.hashCode()
                : 0);

        return hash;
    }

    /**
     * Returns the maximum length of this value.
     *
     * @return maximum length of this value
     */
    public int getValueLength() {
        return valueLength;
    }

    /**
     * Sets the maximum length of this value.
     *
     * @param length maximum length. Default: 0.
     */
    protected void setValueLength(int length) {
        this.valueLength = length;
    }

    public String getValueName() {
        return valueName;
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

    public ValueType getValueType() {
        return valueType;
    }

    /**
     * Returns an appropriate input verifier for a text component.
     *
     * @return this class returns an unsophisticated input verifier dependend
     *         on the data type
     */
    public InputVerifier getInputVerifier() {
        return valueType.equals(ValueType.STRING)
                ? new MaxLengthInputVerifier(valueLength)
                : valueType.defaultInputVerifier;
    }

    public DefaultFormatterFactory getFormatterFactory() {
        return MetaDataValueFormatterFactory.getFormatterFactory(this);
    }
}
