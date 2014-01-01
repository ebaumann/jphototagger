package org.jphototagger.domain.metadata.exif;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.lib.util.Bundle;

/**
 * Tabellenspalte <code>exif_lenses</code> der Tabelle <code>exif</code>.
 * <ul>
 * <li>EXIF: Maker Note Tag</li>
 * </ul>
 *
 * @author Elmar Baumann
 */
public final class ExifLensMetaDataValue extends MetaDataValue {

    public static final ExifLensMetaDataValue INSTANCE = new ExifLensMetaDataValue();

    private ExifLensMetaDataValue() {
        super("lens", "exif_lenses", ValueType.STRING);
        setValueLength(125);
        setDescription(Bundle.getString(ExifLensMetaDataValue.class, "ExifLensMetaDataValue.Description"));
    }
}
