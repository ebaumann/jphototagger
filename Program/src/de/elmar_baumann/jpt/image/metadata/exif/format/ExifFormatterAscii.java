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
import java.util.ArrayList;
import java.util.List;

/**
 * Formats EXIF metadata fields in ASCII format.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-10
 */
public final class ExifFormatterAscii extends ExifFormatter {

    private static final List<Integer> ASCII_TAGS = new ArrayList<Integer>();
    public static final ExifFormatterAscii INSTANCE = new ExifFormatterAscii();

    private ExifFormatterAscii() {
    }

    static {
        // Ordered alphabetically for faster checks
        // *****************************************************
        // *** Add every new tag ID to ExifFormatterFactory! ***
        // *****************************************************
        ASCII_TAGS.add(ExifTag.ARTIST.getId());
        ASCII_TAGS.add(ExifTag.IMAGE_DESCRIPTION.getId());
        ASCII_TAGS.add(ExifTag.IMAGE_UNIQUE_ID.getId());
        ASCII_TAGS.add(ExifTag.MAKE.getId());
        ASCII_TAGS.add(ExifTag.MODEL.getId());
        ASCII_TAGS.add(ExifTag.SOFTWARE.getId());
        ASCII_TAGS.add(ExifTag.SPECTRAL_SENSITIVITY.getId());
    }

    @Override
    public String format(IdfEntryProxy entry) {
        boolean isAsciiTag = ASCII_TAGS.contains(entry.getTag());
        if (!isAsciiTag) throw new IllegalArgumentException(
                    "Not an ASCII-Tag: " + entry);
        return ExifAscii.decode(entry.getRawValue());
    }
}
