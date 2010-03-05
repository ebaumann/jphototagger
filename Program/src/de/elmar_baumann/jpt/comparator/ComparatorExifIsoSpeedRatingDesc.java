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
 * @version 2009-12-16
 */
public final class ComparatorExifIsoSpeedRatingDesc
        extends    ClassEquality
        implements Comparator<File>,
                   Serializable
    {
    private static final long serialVersionUID = -3791741623919608627L;

    @Override
    public int compare(File fileLeft, File fileRight) {
        Exif exifLeft  = DatabaseImageFiles.INSTANCE.getExifOf(fileLeft.getAbsolutePath());
        Exif exifRight = DatabaseImageFiles.INSTANCE.getExifOf(fileRight.getAbsolutePath());

        return exifLeft == null && exifRight == null
                ? 0
                : exifLeft == null && exifRight != null
                ? 1
                : exifLeft != null && exifRight == null
                ? -1
                : exifRight.getIsoSpeedRatings() - exifLeft.getIsoSpeedRatings()
                ;
    }
}
