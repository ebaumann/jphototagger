/*
 * @(#)ComparatorExifFocalLengthDesc.java    Created on 2009-12-16
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

package de.elmar_baumann.jpt.comparator;

import de.elmar_baumann.jpt.data.Exif;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.lib.util.ClassEquality;

import java.io.File;
import java.io.Serializable;

import java.util.Comparator;

/**
 *
 * @author  Elmar Baumann
 */
public final class ComparatorExifFocalLengthDesc extends ClassEquality
        implements Comparator<File>, Serializable {
    private static final long serialVersionUID = 8930101703487566400L;

    @Override
    public int compare(File fileLeft, File fileRight) {
        Exif exifLeft =
            DatabaseImageFiles.INSTANCE.getExifOfImageFile(fileLeft);
        Exif exifRight =
            DatabaseImageFiles.INSTANCE.getExifOfImageFile(fileRight);

        return ((exifLeft == null) && (exifRight == null))
               ? 0
               : ((exifLeft == null) && (exifRight != null))
                 ? 1
                 : ((exifLeft != null) && (exifRight == null))
                   ? -1
                   : (exifRight.getFocalLength() > exifLeft.getFocalLength())
                     ? 1
                     : (exifRight.getFocalLength() == exifLeft.getFocalLength())
                       ? 0
                       : -1;
    }
}
