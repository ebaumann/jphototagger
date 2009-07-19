package de.elmar_baumann.imv.image.metadata.exif;

import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatter;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterFactory;

/**
 * Formatiert EXIF-Werte.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-31
 */
public final class ExifFieldValueFormatter {

    /**
     * Formatis an exif entry.
     * 
     * @param  entry  entry
     * @return entry formatted
     */
    public static String format(IdfEntryProxy entry) {
        ExifFormatter formatter = ExifFormatterFactory.get(entry.getTag());
        if (formatter != null) {
            return formatter.format(entry);
        }
        return entry.toString().trim();
    }

    private ExifFieldValueFormatter() {
    }
}
