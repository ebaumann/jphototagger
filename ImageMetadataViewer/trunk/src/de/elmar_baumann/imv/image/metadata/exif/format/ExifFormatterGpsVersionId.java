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

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifByte;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import java.util.Arrays;

/**
 * Formats an EXIF entry of the type {@link ExifTag#GPS_VERSION_ID}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-10
 */
public final class ExifFormatterGpsVersionId extends ExifFormatter {

    public static final ExifFormatterGpsVersionId INSTANCE =
            new ExifFormatterGpsVersionId();

    private ExifFormatterGpsVersionId() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.GPS_VERSION_ID.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry); // NOI18N
        byte[] rawValue = entry.getRawValue();
        assert rawValue.length == 4 : rawValue.length;
        if (rawValue.length != 4)
            return new String(rawValue);
        ExifByte first = new ExifByte(Arrays.copyOfRange(rawValue, 0, 1));
        ExifByte second = new ExifByte(Arrays.copyOfRange(rawValue, 1, 2));
        ExifByte third = new ExifByte(Arrays.copyOfRange(rawValue, 2, 3));
        ExifByte fourth = new ExifByte(Arrays.copyOfRange(rawValue, 3, 4));

        return first.getValue() +
                "." + second.getValue() + // NOI18N
                "." + third.getValue() + // NOI18N
                "." + fourth.getValue(); // NOI18N
    }
}
