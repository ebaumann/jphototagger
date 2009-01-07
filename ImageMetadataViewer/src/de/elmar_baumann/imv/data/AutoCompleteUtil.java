package de.elmar_baumann.imv.data;

import de.elmar_baumann.imv.database.metadata.Column;
import java.util.List;

/**
 * Util class for XMP data.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/06
 */
public final class AutoCompleteUtil {

    /**
     * Adds XMP data of a specific column to autocomplete data.
     * 
     * @param xmp           XMP data
     * @param column        column
     * @param autoComplete  autocomplete data
     */
    public static void addData(Xmp xmp, Column column, AutoCompleteData autoComplete) {
        Object o = xmp.getValue(column);
        if (o != null) {
            if (o instanceof String) {
                String string = (String) o;
                autoComplete.addString(string);
            }
            if (o instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> list = (List) o;
                for (String string : list) {
                    autoComplete.addString(string);
                }
            }
        }
    }

    private AutoCompleteUtil() {}
}
