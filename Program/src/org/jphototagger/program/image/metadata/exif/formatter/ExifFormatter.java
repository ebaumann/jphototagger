package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.ExifMetadata.IfdType;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import org.jphototagger.program.resource.Translation;
import java.util.EnumMap;
import java.util.Map;

/**
 * Formats EXIF metadata.
 *
 * @author Elmar Baumann
 */
public abstract class ExifFormatter {
    private static final Map<IfdType, Translation> TRANSLATION_OF_IFD = new EnumMap<IfdType,
                                                                            Translation>(IfdType.class);

    static {
        TRANSLATION_OF_IFD.put(IfdType.EXIF, new Translation("ExifExifIfdFieldValueTranslations"));
    }

    /**
     * Formats an EXIF tag.
     *
     * @param  exifTag EXIF tag
     * @return         string with formatted entry data
     * @throws         IllegalArgumentException if the entry has the wrong type
     */
    public abstract String format(ExifTag exifTag);

    protected String translate(IfdType ifdType, String propertyKey) {
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
