package org.jphototagger.program.image.metadata.exif.tag;

import org.jphototagger.program.image.metadata.exif.datatype.ExifRational;
import java.text.MessageFormat;

/**
 * The time as UTC (Coordinated Universal Time).
 *
 * @author Elmar Baumann
 */
public final class ExifGpsTimeStamp {
    private ExifRational hours;
    private ExifRational minutes;
    private ExifRational seconds;

    public ExifGpsTimeStamp(ExifRational hours, ExifRational minutes, ExifRational seconds) {
        if (hours == null) {
            throw new NullPointerException("hours == null");
        }

        if (minutes == null) {
            throw new NullPointerException("minutes == null");
        }

        if (seconds == null) {
            throw new NullPointerException("seconds == null");
        }

        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public ExifRational hours() {
        return hours;
    }

    public ExifRational minutes() {
        return minutes;
    }

    public ExifRational seconds() {
        return seconds;
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0}:{1}:{2}", hours, minutes, seconds);
    }
}
