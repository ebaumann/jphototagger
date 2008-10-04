package de.elmar_baumann.imagemetadataviewer.database.metadata.xmp;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>subject</code> der Tabelle <code>xmp_dc_subjects</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public class ColumnXmpDcSubjectsSubject extends Column {

    private static ColumnXmpDcSubjectsSubject instance = new ColumnXmpDcSubjectsSubject();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpDcSubjectsSubject() {
        super(
            TableXmpDcSubjects.getInstance(),
            "subject", // NOI18N
            DataType.String);

        setLength(128);
        setDescription(Bundle.getString("ColumnXmpDcSubjectsSubject.Description"));
    }
}
