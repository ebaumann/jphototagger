/*
 * @(#)ExifGpsUtil.java    Created on 2009-03-31
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.image.metadata.exif.tag;

import org.jphototagger.program.image.metadata.exif.datatype.ExifDatatypeUtil;
import org.jphototagger.program.image.metadata.exif.datatype.ExifRational;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import org.jphototagger.program.image.metadata.exif.ExifTags;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import java.util.Locale;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class ExifGpsUtil {
    public static double degrees(ExifDegrees degrees) {
        return ExifDatatypeUtil.toDouble(degrees.degrees())
               + ExifDatatypeUtil.toDouble(degrees.minutes()) / 60
               + ExifDatatypeUtil.toDouble(degrees.seconds()) / 3600;
    }

    public static double secondsOfMinutes(ExifRational minutes) {
        double doubleMinutes  = ExifDatatypeUtil.toDouble(minutes);
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
        Object[]      params   = { dfDegMin.format(deg), dfDegMin.format(min),
                                   dfSec.format(sec) };

        return msg.format(params);
    }

    public static String googleMapsUrl(ExifGpsLongitude longitude,
                                       ExifGpsLatitude latitude) {
        MessageFormat msg =
            new MessageFormat(
                "http://maps.google.com/maps?q={0},{1}&spn=0.001,0.001&t=k&hl=de");
        DecimalFormat df =
            (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);

        df.applyPattern("#.########");

        double   latititudeValue = degrees(latitude.degrees());
        double   longitudeValue  = degrees(longitude.degrees());
        Object[] params          = { df.format(latititudeValue),
                                     df.format(longitudeValue) };

        return msg.format(params);
    }

    public static ExifGpsMetadata gpsMetadata(ExifTags exifTags) {
        ExifGpsMetadata gpsMetaData = new ExifGpsMetadata();

        setGpsLatitude(gpsMetaData, exifTags);
        setGpsLongitude(gpsMetaData, exifTags);
        setGpsAltitude(gpsMetaData, exifTags);

        return gpsMetaData;
    }

    private static void setGpsAltitude(ExifGpsMetadata gpsMetaData,
                                       ExifTags exifTags) {
        ExifTag tagAltitudeRef =
            exifTags.gpsTagById(ExifTag.Id.GPS_ALTITUDE_REF.value());
        ExifTag tagAltitude =
            exifTags.gpsTagById(ExifTag.Id.GPS_ALTITUDE.value());

        if ((tagAltitudeRef != null) && (tagAltitude != null)) {
            gpsMetaData.setAltitude(
                new ExifGpsAltitude(
                    tagAltitudeRef.rawValue(), tagAltitude.rawValue(),
                    tagAltitude.byteOrder()));
        }
    }

    private static void setGpsLatitude(ExifGpsMetadata gpsMetaData,
                                       ExifTags exifTags) {
        ExifTag tagLatitudeRef =
            exifTags.gpsTagById(ExifTag.Id.GPS_LATITUDE_REF.value());
        ExifTag tagLatitude =
            exifTags.gpsTagById(ExifTag.Id.GPS_LATITUDE.value());

        if ((tagLatitudeRef != null) && (tagLatitude != null)) {
            gpsMetaData.setLatitude(
                new ExifGpsLatitude(
                    tagLatitudeRef.rawValue(), tagLatitude.rawValue(),
                    tagLatitude.byteOrder()));
        }
    }

    private static void setGpsLongitude(ExifGpsMetadata gpsMetaData,
            ExifTags exifTags) {
        ExifTag tagLongitudeRef =
            exifTags.gpsTagById(ExifTag.Id.GPS_LONGITUDE_REF.value());
        ExifTag tagLongitude =
            exifTags.gpsTagById(ExifTag.Id.GPS_LONGITUDE.value());

        if ((tagLongitudeRef != null) && (tagLongitude != null)) {
            gpsMetaData.setLongitude(
                new ExifGpsLongitude(
                    tagLongitudeRef.rawValue(), tagLongitude.rawValue(),
                    tagLongitude.byteOrder()));
        }
    }

    private ExifGpsUtil() {}
}
