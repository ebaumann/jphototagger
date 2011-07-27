package org.jphototagger.program.database.metadata.exif;

import org.jphototagger.domain.Column;
import org.jphototagger.program.resource.JptBundle;

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
        setDescription(JptBundle.INSTANCE.getString("ColumnExifLens.Description"));
    }
}
