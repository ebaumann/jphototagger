package org.jphototagger.importfiles.subdircreators;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.metadata.exif.ExifInfo;
import org.jphototagger.importfiles.SubdirectoryCreateStrategy;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = SubdirectoryCreateStrategy.class)
public final class YearAndDateSubdirectoryCreateStrategy implements SubdirectoryCreateStrategy {

    private static final DecimalFormat SUBDIR_DAY_MONTH_FORMAT = new DecimalFormat();

    static {
        SUBDIR_DAY_MONTH_FORMAT.setMinimumIntegerDigits(2);
    }

    @Override
    public String createSubdirectoryName(File file) {
        return getDateDirString(file);
    }

    private static String getDateDirString(File file) {
        ExifInfo exifInfo = Lookup.getDefault().lookup(ExifInfo.class);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(exifInfo.getTimeTakenInMillis(file));
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        String delimiter = "-";
        String yearString = String.valueOf(year);
        return yearString + File.separator
                + yearString
                + delimiter + SUBDIR_DAY_MONTH_FORMAT.format(month)
                + delimiter + SUBDIR_DAY_MONTH_FORMAT.format(day);
    }

    @Override
    public String getDisplayName() {
        return Bundle.getString(DateSubdirectoryCreateStrategy.class,
                "YearAndDateSubdirectoryCreateStrategy.Displayname", File.separator);
    }

    @Override
    public int getPosition() {
        return 300;
    }
}
