/*
 * @(#)GPSImageInfo.java    Created on 2010-08-21
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

import org.jphototagger.program.image.metadata.exif.tag.ExifGpsMetadata;

import java.io.File;

import java.text.MessageFormat;

/**
 * Contains an image file and it's GPS Metadata.
 *
 * @author Elmar Baumann
 */
public final class GPSImageInfo {
    private final File            imageFile;
    private final ExifGpsMetadata gpsMetaData;

    public GPSImageInfo(File imageFile, ExifGpsMetadata gpsMetaData) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (gpsMetaData == null) {
            throw new NullPointerException("gpsMetaData == null");
        }

        this.imageFile   = imageFile;
        this.gpsMetaData = gpsMetaData;
    }

    public ExifGpsMetadata getGPSMetaData() {
        return gpsMetaData;
    }

    public File getImageFile() {
        return imageFile;
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0}: {1}", imageFile, gpsMetaData);
    }
}
