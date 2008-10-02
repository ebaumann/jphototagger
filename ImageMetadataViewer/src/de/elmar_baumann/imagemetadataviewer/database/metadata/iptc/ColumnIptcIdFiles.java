package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.file.ColumnFilesId;

/**
 * Spalte <code>id_files</code> der Tabelle <code>iptc</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnIptcIdFiles extends Column {

    private static ColumnIptcIdFiles instance = new ColumnIptcIdFiles();

    public static ColumnIptcIdFiles getInstance() {
        return instance;
    }

    private ColumnIptcIdFiles() {
        super(
            TableIptc.getInstance(),
            "id_files", // NOI18N
            DataType.integer);

        setIsUnique(true);
        setCanBeNull(false);
        setReferences(ColumnFilesId.getInstance());
    }
}
