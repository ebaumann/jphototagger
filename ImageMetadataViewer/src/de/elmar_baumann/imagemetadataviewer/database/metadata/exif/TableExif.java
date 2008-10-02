package de.elmar_baumann.imagemetadataviewer.database.metadata.exif;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Table;

/**
 * Tabelle <code>exif</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class TableExif extends Table {

    private static TableExif instance = new TableExif();

    public static TableExif getInstance() {
        return instance;
    }

    private TableExif() {
        super("exif"); // NOI18N
    }

    @Override
    protected void addColumns() {
        // Reihenfolge NIE verändern, siehe de.elmar_baumann.imagemetadataviewer.database.metadata.AllTables.get()
        addColumn(ColumnExifId.getInstance());
        addColumn(ColumnExifIdFiles.getInstance());
        addColumn(ColumnExifDateTimeOriginal.getInstance());
        addColumn(ColumnExifFocalLength.getInstance());
        addColumn(ColumnExifIsoSpeedRatings.getInstance());
        addColumn(ColumnExifRecordingEquipment.getInstance());
    }
}
