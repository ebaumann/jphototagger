/*
 * @(#)ComparatorXmpRatingDesc.java    Created on 2009-12-16
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.comparator;

import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpRating;
import org.jphototagger.lib.util.ClassEquality;

import java.io.File;
import java.io.Serializable;

import java.util.Comparator;

/**
 *
 * @author Elmar Baumann
 */
public final class ComparatorXmpRatingDesc extends ClassEquality
        implements Comparator<File>, Serializable {
    private static final long serialVersionUID = -6367296007733860352L;

    @Override
    public int compare(File fileLeft, File fileRight) {
        Xmp  xmpLeft     =
            DatabaseImageFiles.INSTANCE.getXmpOfImageFile(fileLeft);
        Xmp  xmpRight    =
            DatabaseImageFiles.INSTANCE.getXmpOfImageFile(fileRight);
        Long ratingLeft  = xmpLeft.contains(ColumnXmpRating.INSTANCE)
                           ? (Long) xmpLeft.getValue(ColumnXmpRating.INSTANCE)
                           : null;
        Long ratingRight = xmpRight.contains(ColumnXmpRating.INSTANCE)
                           ? (Long) xmpRight.getValue(ColumnXmpRating.INSTANCE)
                           : null;

        return ((ratingLeft == null) && (ratingRight == null))
               ? 0
               : ((ratingLeft == null) && (ratingRight != null))
                 ? 1
                 : ((ratingLeft != null) && (ratingRight == null))
                   ? -1
                   : (int) (ratingRight - ratingLeft);
    }
}
