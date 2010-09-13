/*
 * @(#)XmpRatingFileFilter.java    Created on 2010-03-30
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

package org.jphototagger.program.filefilter;

import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpRating;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;

import java.io.File;
import java.io.FileFilter;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class XmpRatingFileFilter implements FileFilter {
    private final int rating;

    /**
     * Creates a new instance.
     *
     * @param rating required rating
     */
    public XmpRatingFileFilter(int rating) {
        this.rating = rating;
    }

    /**
     * Compares the rating in a XMP sidecar file against the rating value of
     * this instance.
     *
     * @param  imageFile image file
     * @return           true if the image file has a sidecar file and the
     *                   rating in the sidecar file is equal to the rating of
     *                   this instance
     */
    @Override
    public boolean accept(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        Xmp xmp = XmpMetadata.getXmpFromSidecarFileOf(imageFile);

        if (xmp == null) {
            return false;
        }

        Object o = xmp.getValue(ColumnXmpRating.INSTANCE);

        if (o instanceof Long) {
            if (o != null) {
                return ((Long) o).longValue() == rating;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final XmpRatingFileFilter other = (XmpRatingFileFilter) obj;

        if (this.rating != other.rating) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;

        hash = 89 * hash + this.rating;

        return hash;
    }
}
