package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Table;

/**
 * Tabelle <code>xmp_dc_subjects</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public final class TableXmpDcSubjects extends Table {

    public static final TableXmpDcSubjects INSTANCE = new TableXmpDcSubjects();

    private TableXmpDcSubjects() {
        super("xmp_dc_subjects"); // NOI18N
    }

    @Override
    protected void addColumns() {
        addColumn(ColumnXmpDcSubjectsId.INSTANCE);
        addColumn(ColumnXmpDcSubjectsIdXmp.INSTANCE);
        addColumn(ColumnXmpDcSubjectsSubject.INSTANCE);
    }
}
