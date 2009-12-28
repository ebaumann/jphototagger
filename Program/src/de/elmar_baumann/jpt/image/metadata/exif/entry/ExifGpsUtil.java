/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.image.metadata.exif.entry;

import de.elmar_baumann.jpt.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTag;
import de.elmar_baumann.jpt.image.metadata.exif.IfdEntryProxy;
import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifDatatypeUtil;
import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifRational;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-03-31
 */
public final class ExifGpsUtil {

    public static double degrees(ExifDegrees degrees) {
        return ExifDatatypeUtil.toDouble(degrees.degrees()) +
               ExifDatatypeUtil.toDouble(degrees.minutes()) / 60 +
               ExifDatatypeUtil.toDouble(degrees.seconds()) / 3600;
    }

    public static double secondsOfMinutes(ExifRational minutes) {

        double doubleMinutes = ExifDatatypeUtil.toDouble(minutes);
        double integerMinutes = ExifDatatypeUtil.toLong(minutes);

        return (doubleMinutes - integerMinutes) * 60;
    }

    public static String degreesToString(ExifDegrees degrees) {

        MessageFormat msg = new MessageFormat("{0}° {1}'' {2}''''");
        double        deg = ExifDatatypeUtil.toDouble(degrees.degrees());
        double        min = ExifDatatypeUtil.toDouble(degrees.minutes());
        double        sec = ExifDatatypeUtil.toDouble(degrees.seconds());

        if (sec == 0) {
            min = ExifDatatypeUtil.toLong(degrees.minutes());
            sec = ExifGpsUtil.secondsOfMinutes(degrees.minutes());
        }

        DecimalFormat dfDegMin = new DecimalFormat("#");
        DecimalFormat dfSec    = new DecimalFormat("#.##");

        Object[] params = {dfDegMin.format(deg), dfDegMin.format(min), dfSec.format(sec)};

        return msg.format(params);
    }

    public static String googleMapsUrl(ExifGpsLongitude longitude, ExifGpsLatitude latitude) {

        MessageFormat msg = new MessageFormat("http://maps.google.com/maps?q={0},{1}&spn=0.001,0.001&t=k&hl=de");
        DecimalFormat df  = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);

        df.applyPattern("#.########");

        double latititudeValue = degrees(latitude.degrees());
        double longitudeValue  = degrees(longitude.degrees());

        Object[] params = {df.format(latititudeValue), df.format(longitudeValue)};

        return msg.format(params);
    }

    public static ExifGpsMetadata gpsMetadata(List<IfdEntryProxy> entries) {

        ExifGpsMetadata data = new ExifGpsMetadata();

        setGpsLatitude (data, entries);
        setGpsLongitude(data, entries);
        setGpsAltitude    (data, entries);

        return data;
    }

    private static void setGpsAltitude(ExifGpsMetadata data, List<IfdEntryProxy> entries) {

        IfdEntryProxy entryAltitudeRef = ExifMetadata.getEntry(entries, ExifTag.GPS_ALTITUDE_REF.tagId());
        IfdEntryProxy entryAltitude    = ExifMetadata.getEntry(entries, ExifTag.GPS_ALTITUDE.tagId());

        if (entryAltitudeRef != null && entryAltitude != null) {
            data.setAltitude(new ExifGpsAltitude(
                    entryAltitudeRef.rawValue(),
                    entryAltitude   .rawValue(),
                    entryAltitude   .byteOrder()));
        }
    }

    private static void setGpsLatitude(ExifGpsMetadata data, List<IfdEntryProxy> entries) {

        IfdEntryProxy entryLatitudeRef = ExifMetadata.getEntry(entries, ExifTag.GPS_LATITUDE_REF.tagId());
        IfdEntryProxy entryLatitude    = ExifMetadata.getEntry(entries, ExifTag.GPS_LATITUDE.tagId());

        if (entryLatitudeRef != null && entryLatitude != null) {
            data.setLatitude(new ExifGpsLatitude(
                    entryLatitudeRef.rawValue(),
                    entryLatitude   .rawValue(),
                    entryLatitude   .byteOrder()));
        }
    }

    private static void setGpsLongitude(ExifGpsMetadata data, List<IfdEntryProxy> entries) {

        IfdEntryProxy entryLongitudeRef = ExifMetadata.getEntry(entries, ExifTag.GPS_LONGITUDE_REF.tagId());
        IfdEntryProxy entryLongitude    = ExifMetadata.getEntry(entries, ExifTag.GPS_LONGITUDE.tagId());

        if (entryLongitudeRef != null && entryLongitude != null) {
            data.setLongitude(new ExifGpsLongitude(
                    entryLongitudeRef.rawValue(),
                    entryLongitude    .rawValue(),
                    entryLongitude    .byteOrder()));
        }
    }

    private ExifGpsUtil() {
    }
}
