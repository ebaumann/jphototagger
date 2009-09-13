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
package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifAscii;
import de.elmar_baumann.imv.image.metadata.exif.ExifFieldValueFormatter;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import java.text.DateFormat;
import java.util.GregorianCalendar;

/**
 * Formats an EXIF entry of the type {@link ExifTag#DATE_TIME_ORIGINAL}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-10
 */
public final class ExifFormatterDateTime extends ExifFormatter {

    public static final ExifFormatterDateTime INSTANCE =
            new ExifFormatterDateTime();

    private ExifFormatterDateTime() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.DATE_TIME_ORIGINAL.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry); // NOI18N
        byte[] rawValue = entry.getRawValue();
        String value = ExifAscii.decode(rawValue).trim();
        if (value.length() >= 18) {
            try {
                int year = Integer.parseInt(value.substring(0, 4));
                int month = Integer.parseInt(value.substring(5, 7));
                int day = Integer.parseInt(value.substring(8, 10));
                int hour = Integer.parseInt(value.substring(11, 13));
                int minute = Integer.parseInt(value.substring(14, 16));
                int second = Integer.parseInt(value.substring(17, 19));
                GregorianCalendar cal = new GregorianCalendar(
                        year, month - 1, day, hour, minute, second);
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL,
                        DateFormat.FULL);
                return df.format(cal.getTime());
            } catch (Exception ex) {
                AppLog.logSevere(ExifFieldValueFormatter.class, ex);
            }
        }
        return value;
    }
}
