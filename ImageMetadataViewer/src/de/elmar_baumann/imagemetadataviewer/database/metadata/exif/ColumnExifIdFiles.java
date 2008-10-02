package de.elmar_baumann.imagemetadataviewer.database.metadata.exif;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.file.ColumnFilesId;

/**
 * Spalte <code>id_files</code> der Tabelle <code>exif</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnExifIdFiles extends Column {

    private static ColumnExifIdFiles instance = new ColumnExifIdFiles();

    public static ColumnExifIdFiles getInstance() {
        return instance;
    }

    private ColumnExifIdFiles() {
        super(
            TableExif.getInstance(),
            "id_files", // NOI18N
            DataType.integer);

        setIsUnique(true);
        setCanBeNull(false);
        setReferences(ColumnFilesId.getInstance());
    }
}
