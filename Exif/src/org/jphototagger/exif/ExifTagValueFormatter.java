package org.jphototagger.exif;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.exif.formatter.ExifFormatter;
import org.jphototagger.exif.formatter.ExifFormatterFactory;

/**
 * @author Elmar Baumann
 */
public final class ExifTagValueFormatter {

    /**
     * Formatis an EXIF tag value.
     *
     * @param  exifTag tag
     * @return         formatted tag value
     */
    public static String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }
        ExifFormatter formatter = ExifFormatterFactory.get(exifTag);
        if (formatter != null) {
            try {
                return formatter.format(exifTag);
            } catch (Throwable t) {
                Logger.getLogger(ExifTagValueFormatter.class.getName()).log(Level.SEVERE, null, t);
                return "?";
            }
        }
        return exifTag.getStringValue().trim();
    }

    private ExifTagValueFormatter() {
    }
}
