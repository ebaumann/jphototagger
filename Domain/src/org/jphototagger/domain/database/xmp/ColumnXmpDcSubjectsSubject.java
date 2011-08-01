package org.jphototagger.domain.database.xmp;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;
import org.jphototagger.lib.resource.Bundle;

/**
 * Spalte <code>subject</code> der Tabelle <code>xmp_dc_subject</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpDcSubjectsSubject extends Column {

    public static final ColumnXmpDcSubjectsSubject INSTANCE = new ColumnXmpDcSubjectsSubject();

    private ColumnXmpDcSubjectsSubject() {
        super("subject", "dc_subjects", DataType.STRING);
        setLength(64);
        setDescription(Bundle.getString(ColumnXmpDcSubjectsSubject.class, "ColumnXmpDcSubjectsSubject.Description"));
        setLongerDescription(Bundle.getString(ColumnXmpDcSubjectsSubject.class, "ColumnXmpDcSubjectsSubject.LongerDescription"));
    }
}
