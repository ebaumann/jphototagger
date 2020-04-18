package org.jphototagger.importfiles;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import org.jphototagger.domain.metadata.exif.ExifInfo;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class NameUtil {

    private static final DecimalFormat DAY_MONTH_FORMAT = new DecimalFormat();
    private static final ExifInfo EXIF_INFO = Lookup.getDefault().lookup(ExifInfo.class);

    static {
        DAY_MONTH_FORMAT.setMinimumIntegerDigits(2);
    }

    /**
     * @param file
     * @return based upon the file's date taken/last modified a string in the format "YYYY-MM-DD", e.g. 2012-01-25
     */
    public static String getDateString(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        Calendar calendar = Calendar.getInstance();
        long timeTakenInMillis = EXIF_INFO == null
                ? file.lastModified()
                : EXIF_INFO.getTimeTakenInMillis(file);
        calendar.setTimeInMillis(timeTakenInMillis);
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        String delimiter = "-";
        return String.valueOf(year)
                + delimiter + DAY_MONTH_FORMAT.format(month)
                + delimiter + DAY_MONTH_FORMAT.format(day);
    }

    private NameUtil() {
    }
}
