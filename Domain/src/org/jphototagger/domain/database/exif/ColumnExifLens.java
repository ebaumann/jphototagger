package org.jphototagger.domain.database.exif;

import org.jphototagger.domain.database.Column;

/**
 * Tabellenspalte <code>exif_lenses</code> der Tabelle <code>exif</code>.
 * <ul>
 * <li>EXIF: Maker Note Tag</li>
 * </ul>
 *
 * @author Elmar Baumann
 */
public final class ColumnExifLens extends Column {
    public static final ColumnExifLens INSTANCE = new ColumnExifLens();

    private ColumnExifLens() {
        super("lens", "exif_lenses", DataType.STRING);
        setDescription(Bundle.INSTANCE.getString("ColumnExifLens.Description"));
    }
}
