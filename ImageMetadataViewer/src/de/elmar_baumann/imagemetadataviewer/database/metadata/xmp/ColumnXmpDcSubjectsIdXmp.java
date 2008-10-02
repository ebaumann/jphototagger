package de.elmar_baumann.imagemetadataviewer.database.metadata.xmp;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id_xmp</code> der Tabelle <code>xmp_dc_subjects</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnXmpDcSubjectsIdXmp extends Column {

    private static ColumnXmpDcSubjectsIdXmp instance = new ColumnXmpDcSubjectsIdXmp();

    public static ColumnXmpDcSubjectsIdXmp getInstance() {
        return instance;
    }

    private ColumnXmpDcSubjectsIdXmp() {
        super(
            TableXmpDcSubjects.getInstance(),
            "id_xmp", // NOI18N
            DataType.integer);

        setCanBeNull(false);
        setReferences(ColumnXmpId.getInstance());

    }
}
