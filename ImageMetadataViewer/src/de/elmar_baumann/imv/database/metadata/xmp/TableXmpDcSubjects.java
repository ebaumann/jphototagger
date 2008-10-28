package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Table;

/**
 * Tabelle <code>xmp_dc_subjects</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class TableXmpDcSubjects extends Table {

    private static TableXmpDcSubjects instance = new TableXmpDcSubjects();

    public static TableXmpDcSubjects getInstance() {
        return instance;
    }

    private TableXmpDcSubjects() {
        super("xmp_dc_subjects"); // NOI18N
    }

    @Override
    protected void addColumns() {
        addColumn(ColumnXmpDcSubjectsId.getInstance());
        addColumn(ColumnXmpDcSubjectsIdXmp.getInstance());
        addColumn(ColumnXmpDcSubjectsSubject.getInstance());
    }
}
