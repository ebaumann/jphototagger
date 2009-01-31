package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;

/**
 * Spalte <code>id</code> der Tabelle <code>xmp_dc_subjects</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public final class ColumnXmpDcSubjectsId extends Column {

    private static final ColumnXmpDcSubjectsId instance = new ColumnXmpDcSubjectsId();

    public static ColumnXmpDcSubjectsId getInstance() {
        return instance;
    }

    private ColumnXmpDcSubjectsId() {
        super(
            TableXmpDcSubjects.getInstance(),
            "id", // NOI18N
            DataType.BIGINT);

        setIsPrimaryKey(true);
    }
}
