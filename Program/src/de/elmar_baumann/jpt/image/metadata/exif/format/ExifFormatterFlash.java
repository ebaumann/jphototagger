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
package de.elmar_baumann.jpt.image.metadata.exif.format;

import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifAscii;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTag;
import de.elmar_baumann.jpt.image.metadata.exif.IdfEntryProxy;
import de.elmar_baumann.lib.lang.Util;

/**
 * Formats an EXIF entry of the type {@link ExifTag#FLASH}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-10
 */
public final class ExifFormatterFlash extends ExifFormatter {

    public static final ExifFormatterFlash INSTANCE = new ExifFormatterFlash();

    private ExifFormatterFlash() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.FLASH.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry); // NOI18N
        byte[] rawValue = entry.getRawValue();
        if (rawValue != null && rawValue.length >= 1) {
            boolean[] bitsByte1 = Util.getBits(rawValue[0]);
            boolean fired = bitsByte1[0];
            boolean hasFlash = !bitsByte1[5];
            if (!hasFlash) {
                return TRANSLATION.translate("FlashNone"); // NOI18N
            }
            return fired
                   ? TRANSLATION.translate("FlashFired") // NOI18N
                   : TRANSLATION.translate("FlashNotFired"); // NOI18N
        }
        return ExifAscii.decode(rawValue);
    }
}
