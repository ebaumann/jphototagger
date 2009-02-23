package de.elmar_baumann.imv.database.metadata.exif;

import de.elmar_baumann.imv.database.metadata.Table;

/**
 * Tabelle <code>exif</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public final class TableExif extends Table {

    public static final TableExif INSTANCE = new TableExif();

    private TableExif() {
        super("exif"); // NOI18N
    }

    @Override
    protected void addColumns() {
        addColumn(ColumnExifId.INSTANCE);
        addColumn(ColumnExifIdFiles.INSTANCE);
        addColumn(ColumnExifDateTimeOriginal.INSTANCE);
        addColumn(ColumnExifFocalLength.INSTANCE);
        addColumn(ColumnExifIsoSpeedRatings.INSTANCE);
        addColumn(ColumnExifRecordingEquipment.INSTANCE);
    }
}
