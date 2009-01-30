package de.elmar_baumann.imv.database.metadata.exif;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.file.ColumnFilesId;

/**
 * Spalte <code>id_files</code> der Tabelle <code>exif</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public final class ColumnExifIdFiles extends Column {

    private static final ColumnExifIdFiles instance = new ColumnExifIdFiles();

    public static ColumnExifIdFiles getInstance() {
        return instance;
    }

    private ColumnExifIdFiles() {
        super(
            TableExif.getInstance(),
            "id_files", // NOI18N
            DataType.Bigint);

        setIsUnique(true);
        setCanBeNull(false);
        setReferences(ColumnFilesId.getInstance());
    }
}
