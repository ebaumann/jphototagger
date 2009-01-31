package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;

/**
 * Spalte <code>id_xmp</code> der Tabelle <code>xmp_dc_subjects</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public final class ColumnXmpDcSubjectsIdXmp extends Column {

    private static final ColumnXmpDcSubjectsIdXmp instance = new ColumnXmpDcSubjectsIdXmp();

    public static ColumnXmpDcSubjectsIdXmp getInstance() {
        return instance;
    }

    private ColumnXmpDcSubjectsIdXmp() {
        super(
            TableXmpDcSubjects.getInstance(),
            "id_xmp", // NOI18N
            DataType.BIGINT);

        setCanBeNull(false);
        setReferences(ColumnXmpId.getInstance());

    }
}
