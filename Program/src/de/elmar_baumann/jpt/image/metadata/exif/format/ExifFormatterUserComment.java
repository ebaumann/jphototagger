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

import de.elmar_baumann.jpt.image.metadata.exif.ExifTag;
import de.elmar_baumann.jpt.image.metadata.exif.IdfEntryProxy;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Formats an EXIF entry of the type {@link ExifTag#USER_COMMENT}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-10
 */
public final class ExifFormatterUserComment extends ExifFormatter {

    public static final ExifFormatterUserComment INSTANCE =
            new ExifFormatterUserComment();
    private static final byte[] CODE_ASCII = {0x41, 0x53, 0x43, 0x49, 0x49, 0x00,
        0x00, 0x00};
    private static final byte[] CODE_JIS = {0x4A, 0x49, 0x53, 0x00, 0x00, 0x00,
        0x00, 0x00};
    private static final byte[] CODE_UNICODE = {0x55, 0x4E, 0x49, 0x43, 0x4F,
        0x44, 0x45, 0x00};
    private static final byte[] CODE_UNDEFINED = {0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
        0x0, 0x0};

    private enum CharCode {

        ASCII, JIS, UNICODE, UNDEFINED, UNREGOGNIZED
    }

    private ExifFormatterUserComment() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.USER_COMMENT.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry); // NOI18N
        byte[] rawValue = entry.getRawValue();
        if (rawValue.length <= 8) return ""; // NOI18N
        CharCode charCode = getEncoding(rawValue);
        byte[] rawComment = Arrays.copyOfRange(rawValue, 8, rawValue.length);
        if (charCode.equals(CharCode.ASCII))
            return new String(rawComment, Charset.forName("US-ASCII")); // NOI18N
        if (charCode.equals(CharCode.JIS))
            return new String(rawComment, Charset.forName("JISAutoDetect")); // NOI18N
        if (charCode.equals(CharCode.UNICODE))
            return new String(rawComment, Charset.forName("UTF-8")); // NOI18N
        return ""; // NOI18N
    }

    private static CharCode getEncoding(byte[] rawValue) {
        assert rawValue.length >= 8 : rawValue.length;
        byte[] code = Arrays.copyOf(rawValue, 8);
        if (Arrays.equals(code, CODE_ASCII)) return CharCode.ASCII;
        if (Arrays.equals(code, CODE_JIS)) return CharCode.JIS;
        if (Arrays.equals(code, CODE_UNICODE)) return CharCode.UNICODE;
        if (Arrays.equals(code, CODE_UNDEFINED)) return CharCode.UNDEFINED;
        return CharCode.UNREGOGNIZED;
    }
}
