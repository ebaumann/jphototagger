package org.jphototagger.domain.metadata.xmp;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.lib.util.Bundle;

/**
 * Spalte <code>subject</code> der Tabelle <code>xmp_dc_subject</code>.
 *
 * @author Elmar Baumann
 */
public final class XmpDcSubjectsSubjectMetaDataValue extends MetaDataValue {

    public static final XmpDcSubjectsSubjectMetaDataValue INSTANCE = new XmpDcSubjectsSubjectMetaDataValue();

    private XmpDcSubjectsSubjectMetaDataValue() {
        super("subject", "dc_subjects", ValueType.STRING);
        setValueLength(64);
        setDescription(Bundle.getString(XmpDcSubjectsSubjectMetaDataValue.class, "XmpDcSubjectsSubjectMetaDataValue.Description"));
        setLongerDescription(Bundle.getString(XmpDcSubjectsSubjectMetaDataValue.class, "XmpDcSubjectsSubjectMetaDataValue.LongerDescription"));
    }
}
