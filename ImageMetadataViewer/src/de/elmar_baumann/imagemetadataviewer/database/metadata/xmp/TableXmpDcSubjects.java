package de.elmar_baumann.imagemetadataviewer.database.metadata.xmp;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Table;

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
        // Reihenfolge NIE verändern, siehe de.elmar_baumann.imagemetadataviewer.database.metadata.AllTables.get()
        addColumn(ColumnXmpDcSubjectsId.getInstance());
        addColumn(ColumnXmpDcSubjectsIdXmp.getInstance());
        addColumn(ColumnXmpDcSubjectsSubject.getInstance());
    }
}
