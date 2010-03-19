/*
 * @(#)ExifFormatterDateTime.java    Created on 2009-06-10
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

package de.elmar_baumann.jpt.image.metadata.exif.formatter;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifAscii;
import de.elmar_baumann.jpt.image.metadata.exif.Ensure;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTag;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTagValueFormatter;

import java.text.DateFormat;

import java.util.GregorianCalendar;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#DATE_TIME_ORIGINAL}.
 *
 * @author  Elmar Baumann
 */
public final class ExifFormatterDateTime extends ExifFormatter {
    public static final ExifFormatterDateTime INSTANCE =
        new ExifFormatterDateTime();

    private ExifFormatterDateTime() {}

    @Override
    public String format(ExifTag exifTag) {
        Ensure.exifTagId(exifTag, ExifTag.Id.DATE_TIME_ORIGINAL);

        byte[] rawValue = exifTag.rawValue();
        String value    = ExifAscii.decode(rawValue).trim();

        if (value.length() >= 18) {
            try {
                int               year   = Integer.parseInt(value.substring(0,
                                               4));
                int               month  = Integer.parseInt(value.substring(5,
                                               7));
                int               day    = Integer.parseInt(value.substring(8,
                                               10));
                int               hour   = Integer.parseInt(value.substring(11,
                                               13));
                int               minute = Integer.parseInt(value.substring(14,
                                               16));
                int               second = Integer.parseInt(value.substring(17,
                                               19));
                GregorianCalendar cal    = new GregorianCalendar(year,
                                               month - 1, day, hour, minute,
                                               second);
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL,
                                    DateFormat.FULL);

                return df.format(cal.getTime());
            } catch (Exception ex) {
                AppLogger.logSevere(ExifTagValueFormatter.class, ex);
            }
        }

        return value;
    }
}
