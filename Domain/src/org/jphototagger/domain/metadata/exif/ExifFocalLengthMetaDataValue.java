package org.jphototagger.domain.metadata.exif;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.lib.util.Bundle;

/**
 * Tabellenspalte <code>exif_focal_length</code> der Tabelle <code>exif</code>.
 * <ul>
 * <li>EXIF: FocalLength</li>
 * <li>EXIF Tag-ID: 37386 (Hex: 920A)</li>
 * </ul>
 *
 * @author Elmar Baumann
 */
public final class ExifFocalLengthMetaDataValue extends MetaDataValue {

    public static final ExifFocalLengthMetaDataValue INSTANCE = new ExifFocalLengthMetaDataValue();

    private ExifFocalLengthMetaDataValue() {
        super("exif_focal_length", "exif", ValueType.REAL);
        setDescription(Bundle.getString(ExifFocalLengthMetaDataValue.class, "ExifFocalLengthMetaDataValue.Description"));
    }
}
