package de.elmar_baumann.imv.database.metadata.exif;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.file.ColumnFilesId;

/**
 * Spalte <code>id_files</code> der Tabelle <code>exif</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-27
 */
public final class ColumnExifIdFiles extends Column {

    public static final ColumnExifIdFiles INSTANCE = new ColumnExifIdFiles();

    private ColumnExifIdFiles() {
        super(
            TableExif.INSTANCE,
            "id_files", // NOI18N
            DataType.BIGINT);

        setIsUnique(true);
        setCanBeNull(false);
        setReferences(ColumnFilesId.INSTANCE);
    }
}
