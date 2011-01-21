package org.jphototagger.program.database.metadata.xmp;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Column.DataType;
import org.jphototagger.program.resource.JptBundle;

/**
 * Spalte <code>subject</code> der Tabelle <code>xmp_dc_subject</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpDcSubjectsSubject extends Column {
    public static final ColumnXmpDcSubjectsSubject INSTANCE =
        new ColumnXmpDcSubjectsSubject();

    private ColumnXmpDcSubjectsSubject() {
        super("subject", "dc_subjects", DataType.STRING);
        setLength(64);
        setDescription(
            JptBundle.INSTANCE.getString(
                "ColumnXmpDcSubjectsSubject.Description"));
        setLongerDescription(
            JptBundle.INSTANCE.getString(
                "ColumnXmpDcSubjectsSubject.LongerDescription"));
    }
}
