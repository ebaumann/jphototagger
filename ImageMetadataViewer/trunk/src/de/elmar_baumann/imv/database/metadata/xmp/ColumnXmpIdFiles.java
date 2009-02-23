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

    public static final ColumnXmpIdFiles INSTANCE = new ColumnXmpIdFiles();

    private ColumnXmpIdFiles() {
        super(
            TableXmp.INSTANCE,
            "id_files", // NOI18N
            DataType.BIGINT);

        setIsUnique(true);
        setCanBeNull(false);
        setReferences(ColumnFilesId.INSTANCE);
    }
}
