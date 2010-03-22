/*
 * @(#)ExifTagValueFormatter.java    Created on 2008-08-31
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

package org.jphototagger.program.image.metadata.exif;

import org.jphototagger.program.image.metadata.exif.formatter.ExifFormatter;
import org.jphototagger.program.image.metadata.exif.formatter.ExifFormatterFactory;

/**
 *
 * @author  Elmar Baumann
 */
public final class ExifTagValueFormatter {

    /**
     * Formatis an EXIF tag value.
     *
     * @param  exifTag tag
     * @return         formatted tag value
     */
    public static String format(ExifTag exifTag) {
        ExifFormatter formatter = ExifFormatterFactory.get(exifTag);

        if (formatter != null) {
            return formatter.format(exifTag);
        }

        return exifTag.stringValue().trim();
    }

    private ExifTagValueFormatter() {}
}
