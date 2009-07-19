package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;

/**
 * Spalte <code>id</code> der Tabelle <code>xmp_dc_subjects</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-27
 */
public final class ColumnXmpDcSubjectsId extends Column {

    public static final ColumnXmpDcSubjectsId INSTANCE = new ColumnXmpDcSubjectsId();

    private ColumnXmpDcSubjectsId() {
        super(
            TableXmpDcSubjects.INSTANCE,
            "id", // NOI18N
            DataType.BIGINT);

        setIsPrimaryKey(true);
    }
}
