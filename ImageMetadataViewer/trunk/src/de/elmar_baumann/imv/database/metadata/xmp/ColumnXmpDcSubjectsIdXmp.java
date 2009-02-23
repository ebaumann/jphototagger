package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;

/**
 * Spalte <code>id_xmp</code> der Tabelle <code>xmp_dc_subjects</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public final class ColumnXmpDcSubjectsIdXmp extends Column {

    public static final ColumnXmpDcSubjectsIdXmp INSTANCE = new ColumnXmpDcSubjectsIdXmp();

    private ColumnXmpDcSubjectsIdXmp() {
        super(
            TableXmpDcSubjects.INSTANCE,
            "id_xmp", // NOI18N
            DataType.BIGINT);

        setCanBeNull(false);
        setReferences(ColumnXmpId.INSTANCE);

    }
}
