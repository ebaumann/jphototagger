package org.jphototagger.exif.formatter;

import java.util.EnumMap;
import java.util.Map;
import org.jphototagger.exif.ExifIfdType;
import org.jphototagger.exif.ExifTag;
import org.jphototagger.lib.util.Translation;

/**
 * Formats EXIF metadata.
 *
 * @author Elmar Baumann
 */
public abstract class ExifFormatter {

    private static final Map<ExifIfdType, Translation> TRANSLATION_OF_IFD = new EnumMap<>(ExifIfdType.class);

    static {
        TRANSLATION_OF_IFD.put(ExifIfdType.EXIF, new Translation(ExifFormatter.class, "ExifExifIfdFieldValueTranslations"));
    }

    /**
     * Formats an EXIF tag.
     *
     * @param  exifTag EXIF tag
     * @return         string with formatted entry data
     * @throws         IllegalArgumentException if the entry has the wrong type
     */
    public abstract String format(ExifTag exifTag);

    protected String translate(ExifIfdType ifdType, String propertyKey) {
        if (ifdType == null) {
            throw new NullPointerException("ifdType == null");
        }

        if (propertyKey == null) {
            throw new NullPointerException("propertyKey == null");
        }

        Translation translation = TRANSLATION_OF_IFD.get(ifdType);

        return (translation == null)
                ? "?"
                : translation.translate(propertyKey);
    }
}
