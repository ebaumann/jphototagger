package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>subject</code> der Tabelle <code>xmp_dc_subjects</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public final class ColumnXmpDcSubjectsSubject extends Column {

    public static final ColumnXmpDcSubjectsSubject INSTANCE = new ColumnXmpDcSubjectsSubject();

    private ColumnXmpDcSubjectsSubject() {
        super(
            TableXmpDcSubjects.INSTANCE,
            "subject", // NOI18N
            DataType.STRING);

        setLength(128);
        setDescription(Bundle.getString("ColumnXmpDcSubjectsSubject.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpDcSubjectsSubject.LongerDescription"));
    }
}
