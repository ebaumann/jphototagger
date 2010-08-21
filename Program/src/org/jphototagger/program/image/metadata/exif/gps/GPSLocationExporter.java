/*
 * @(#)GPSLocationExporter.java    Created on 2010-08-20
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

package org.jphototagger.program.image.metadata.exif.gps;

import org.jphototagger.program.image.metadata.exif.GPSImageInfo;

import java.io.IOException;
import java.io.OutputStream;

import java.nio.charset.UnsupportedCharsetException;

import java.util.Collection;

import javax.swing.filechooser.FileFilter;

/**
 * Exports GPS metadata into a specefic format.
 *
 * @author Elmar Baumann
 */
public interface GPSLocationExporter {

    /**
     * Exports GPS metadata.
     *
     * @param gpsImageInfo GPS metadata to export
     * @param os           output stream to export to
     * @throws             IOException on I/O errors
     * @throws             UnsupportedCharsetException if the system does not
     *                     support UTF-8 strings
     */
    public void export(Collection<? extends GPSImageInfo> gpsImageInfo,
                       OutputStream os)
            throws IOException, UnsupportedCharsetException;

    /**
     * Returns the filter for exported files.
     *
     * @return filter
     */
    public FileFilter getFileFilter();

    /**
     * Returns the file name suffix.
     *
     * @return suffix, e.g. <code>".kml"</code>
     */
    public String getFilenameExtension();

    /**
     * Returns the display name of the exporter.
     *
     * @return display name
     */
    public String getDisplayName();
}
