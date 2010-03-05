/*
 * JPhotoTagger tags and finds images fast.
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
package de.elmar_baumann.jpt.image.metadata.exif.formatter;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.image.metadata.exif.Ensure;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTagValueFormatter;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTag;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#GPS_DATE_STAMP}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-10
 */
public final class ExifFormatterGpsDateStamp extends ExifFormatter {

    public static final ExifFormatterGpsDateStamp INSTANCE = new ExifFormatterGpsDateStamp();

    private ExifFormatterGpsDateStamp() {
    }

    @Override
    public String format(ExifTag exifTag) {

        Ensure.exifTagId(exifTag, ExifTag.Id.GPS_DATE_STAMP);

        byte[] rawValue  = exifTag.rawValue();
        String rawString = new String(rawValue);

        if (rawString.length() != 11) return rawString;

        try {
            DateFormat df   = new SimpleDateFormat("yyyy:MM:dd");
            Date       date = df.parse(rawString.substring(0, 10));

            return DateFormat.getDateInstance(DateFormat.FULL).format(date);
        } catch (Exception ex) {
            AppLogger.logSevere(ExifTagValueFormatter.class, ex);
        }
        return rawString;
    }
}
