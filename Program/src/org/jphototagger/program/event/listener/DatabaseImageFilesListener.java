/*
 * @(#)DatabaseImageFilesListener.java    Created on
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

package org.jphototagger.program.event.listener;

import org.jphototagger.program.data.Exif;
import org.jphototagger.program.data.Xmp;

import java.io.File;

/**
 * Listens to events in
 * {@link org.jphototagger.program.database.DatabaseImageFiles}.
 *
 * @author Elmar Baumann
 */
public interface DatabaseImageFilesListener {

    /**
     * Will be called if an image files was inserted into
     * {@link org.jphototagger.program.database.DatabaseImageFiles}.
     *
     * @param imageFile inserted image file
     */
    void imageFileInserted(File imageFile);

    /**
     * Will be called if an image file was renamed in
     * {@link org.jphototagger.program.database.DatabaseImageFiles}.
     *
     * @param oldImageFile old image file
     * @param newImageFile new image file
     */
    void imageFileRenamed(File oldImageFile, File newImageFile);

    /**
     * Will be called if an image file was deleted from
     * {@link org.jphototagger.program.database.DatabaseImageFiles}.
     *
     * @param imageFile deleted image file
     */
    void imageFileDeleted(File imageFile);

    /**
     * Will be called if XMP metadata of an image files was inserted in
     * {@link org.jphototagger.program.database.DatabaseImageFiles}.
     *
     * @param imageFile image file, which XMP metadata was inserted
     * @param xmp       inserted XMP metadata
     */
    void xmpInserted(File imageFile, Xmp xmp);

    /**
     * Will be called if XMP metadata of an image files was updated in
     * {@link org.jphototagger.program.database.DatabaseImageFiles}.
     *
     * @param imageFile  image file, which XMP metadata was updated
     * @param oldXmp     old XMP metadata
     * @param updatedXmp updated (new) XMP metadata
     */
    void xmpUpdated(File imageFile, Xmp oldXmp, Xmp updatedXmp);

    /**
     * Will be called if XMP metadata of an image files was deleted from
     * {@link org.jphototagger.program.database.DatabaseImageFiles}.
     *
     * @param imageFile image file, which XMP metadata was deleted
     * @param xmp       deleted XMP metadata
     */
    void xmpDeleted(File imageFile, Xmp xmp);

    /**
     * Will be called if EXIF metadata of an image files was inserted in
     * {@link org.jphototagger.program.database.DatabaseImageFiles}.
     *
     * @param imageFile image file, which EXIF metadata was inserted
     * @param exif      EXIF metadata
     */
    void exifInserted(File imageFile, Exif exif);

    /**
     * Will be called if EXIF metadata of an image files was updated in
     * {@link org.jphototagger.program.database.DatabaseImageFiles}.
     *
     * @param imageFile   image file, which EXIF metadata was updated
     * @param oldExif     old EXIF metadata
     * @param updatedExif updated (new) EXIF metadata
     */
    void exifUpdated(File imageFile, Exif oldExif, Exif updatedExif);

    /**
     * Will be called if EXIF metadata of an image files was deleted from
     * {@link org.jphototagger.program.database.DatabaseImageFiles}.
     *
     * @param imageFile image file, which EXIF metadata was deleted
     * @param exif      EXIF metadata
     */
    void exifDeleted(File imageFile, Exif exif);

    /**
     * Will be called if a thumbnail was updated in
     * {@link org.jphototagger.program.database.DatabaseImageFiles}.
     *
     * @param imageFile image file, which thumbnail was updated
     */
    void thumbnailUpdated(File imageFile);

    /**
     * Will be called if a Dublin Core subject was deleted from
     * {@link org.jphototagger.program.database.DatabaseImageFiles} (not linked
     * with a file, only update of the DC subjects table).
     *
     * @param dcSubject deleted Dublin Core subject
     */
    void dcSubjectInserted(String dcSubject);

    /**
     * Will be called if a Dublin Core subject was deleted from
     * {@link org.jphototagger.program.database.DatabaseImageFiles} (not linked
     * with a file, only update of the DC subjects table).
     *
     * @param dcSubject deleted Dublin Core subject
     */
    void dcSubjectDeleted(String dcSubject);
}
