package de.elmar_baumann.imagemetadataviewer.database.metadata.xmp;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id</code> der Tabelle <code>xmp_dc_subjects</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnXmpDcSubjectsId extends Column {

    private static ColumnXmpDcSubjectsId instance = new ColumnXmpDcSubjectsId();

    public static ColumnXmpDcSubjectsId getInstance() {
        return instance;
    }

    private ColumnXmpDcSubjectsId() {
        super(
            TableXmpDcSubjects.getInstance(),
            "id", // NOI18N
            DataType.Bigint);

        setIsPrimaryKey(true);
    }
}
