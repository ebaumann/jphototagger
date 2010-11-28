/*
 * @(#)FilenameFormatDate.java    Created on 2008-10-13
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

package org.jphototagger.program.controller.filesystem;

import org.jphototagger.program.data.Exif;
import org.jphototagger.program.image.metadata.exif.ExifMetadata;
import org.jphototagger.program.resource.JptBundle;

import java.io.File;

import java.text.SimpleDateFormat;

import java.util.Date;

/**
 * Formatted date in the order YYYY-MM-dd.
 *
 * @author Elmar Baumann
 */
public final class FilenameFormatDate extends FilenameFormat {
    private String delimiter;
    private String name;

    /**
     * Creates an instance with a delimiter between year, month and day.
     *
     * @param delimiter delimiter
     */
    public FilenameFormatDate(String delimiter) {
        if (delimiter == null) {
            throw new NullPointerException("delimiter == null");
        }

        this.delimiter = delimiter;
    }

    /**
     * Sets the delimiter between year, month and day.
     *
     * @param delimiter delimiter
     */
    public void setDelimiter(String delimiter) {
        if (delimiter == null) {
            throw new NullPointerException("delimiter == null");
        }

        this.delimiter = delimiter;
    }

    @Override
    public String format() {
        formatDate(getFile());

        return name;
    }

    /**
     * Sets the date from the EXIF date of a file, If this is not possible
     * the last modification time of the file system will be used.
     *
     * @param file file
     */
    private void formatDate(File file) {
        Exif exif = ExifMetadata.getExif(file);

        if (exif == null) {
            setFromFilesystem(file);
        } else {
            java.sql.Date date = exif.getDateTimeOriginal();

            if (date == null) {
                setFromFilesystem(file);
            } else {
                formatDate(new Date(date.getTime()));
            }
        }
    }

    /**
     * Sets the date from the last modification time of the file system.
     *
     * @param file file
     */
    private void setFromFilesystem(File file) {
        formatDate(new Date(file.lastModified()));
    }

    private void formatDate(Date date) {
        name = new SimpleDateFormat("yyyy" + delimiter + "MM" + delimiter
                                    + "dd").format(date);
    }

    @Override
    public String toString() {
        return JptBundle.INSTANCE.getString("FilenameFormatDate.String");
    }

    private FilenameFormatDate() {}
}
