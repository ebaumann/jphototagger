/*
 * @(#)ExifFormatter.java    Created on 2009-06-10
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

package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.ExifMetadata.IfdType;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import org.jphototagger.program.resource.Translation;

import java.util.HashMap;
import java.util.Map;

/**
 * Formats EXIF metadata.
 *
 * @author  Elmar Baumann
 */
public abstract class ExifFormatter {
    private static final Map<IfdType, Translation> TRANSLATION_OF_IFD =
        new HashMap<IfdType, Translation>();

    static {
        TRANSLATION_OF_IFD.put(
            IfdType.EXIF, new Translation("ExifExifIfdFieldValueTranslations"));
    }

    /**
     * Formats an EXIF tag.
     *
     * @param  exifTag EXIF tag
     * @return         string with formatted entry data
     * @throws         IllegalArgumentException if the entry has the wrong type
     */
    public abstract String format(ExifTag exifTag);

    protected String translate(IfdType ifdType, String propertyKey) {
        Translation translation = TRANSLATION_OF_IFD.get(ifdType);

        return (translation == null)
               ? "?"
               : translation.translate(propertyKey);
    }
}
