package org.jphototagger.domain.templates;

import java.util.HashMap;
import java.util.Set;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpMetaDataValues;
import org.jphototagger.domain.metadata.xmp.Xmp;

/**
 * Holds the data of a metadata edit template.
 *
 * @author Elmar Baumann
 */
public final class MetadataTemplate {

    private String name;
    private final HashMap<MetaDataValue, Object> fieldOfMetaDataValue = new HashMap<MetaDataValue, Object>();

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
        return (name != null) && !name.trim().isEmpty();
    }

    /**
     *
     * @param  value
     * @return        value or null
     */
    public Object getMetaDataValue(MetaDataValue value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        return fieldOfMetaDataValue.get(value);
    }

    /**
     *
     * @param value  Spalte
     * @param data
     */
    public void setMetaDataValue(MetaDataValue value, Object data) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        if (data == null) {
            fieldOfMetaDataValue.remove(value);
        } else {
            fieldOfMetaDataValue.put(value, data);
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

        if ((this.name == null) || !this.name.equals(other.name)) {
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

        for (MetaDataValue value : XmpMetaDataValues.get()) {
            fieldOfMetaDataValue.put(value, xmp.getValue(value));
        }
    }

    public Set<MetaDataValue> getMetaDataValues() {
        return fieldOfMetaDataValue.keySet();
    }
}
