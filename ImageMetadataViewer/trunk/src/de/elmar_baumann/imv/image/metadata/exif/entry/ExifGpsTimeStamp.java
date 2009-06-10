package de.elmar_baumann.imv.image.metadata.exif.entry;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifRational;

/**
 * The time as UTC (Coordinated Universal Time).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/03/17
 */
public final class ExifGpsTimeStamp {

    private ExifRational hours;
    private ExifRational minutes;
    private ExifRational seconds;

    public ExifGpsTimeStamp(ExifRational hours, ExifRational minutes,
            ExifRational seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public ExifRational getHours() {
        return hours;
    }

    public ExifRational getMinutes() {
        return minutes;
    }

    public ExifRational getSeconds() {
        return seconds;
    }
}
