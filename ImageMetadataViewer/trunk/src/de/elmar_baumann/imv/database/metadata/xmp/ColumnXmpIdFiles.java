package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.file.ColumnFilesId;

/**
 * Spalte <code>id_files</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public final class ColumnXmpIdFiles extends Column {

    private static final ColumnXmpIdFiles instance = new ColumnXmpIdFiles();

    public static ColumnXmpIdFiles getInstance() {
        return instance;
    }

    private ColumnXmpIdFiles() {
        super(
            TableXmp.getInstance(),
            "id_files", // NOI18N
            DataType.BIGINT);

        setIsUnique(true);
        setCanBeNull(false);
        setReferences(ColumnFilesId.getInstance());
    }
}
