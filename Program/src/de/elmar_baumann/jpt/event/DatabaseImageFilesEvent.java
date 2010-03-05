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
package de.elmar_baumann.jpt.event;

import de.elmar_baumann.jpt.data.ImageFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Event in a database related to an image.
 *
 * @author  Elmar Baumann
 * @version 2008-09-15
 */
public final class DatabaseImageFilesEvent {

    /**
     * Event type.
     */
    public enum Type {

        /**
         * An image file was deleted
         */
        IMAGEFILE_DELETED,
        /**
         * An image file was inserted
         */
        IMAGEFILE_INSERTED,
        /**
         * An image file was updated
         */
        IMAGEFILE_UPDATED,
        /**
         * A thumbnail was updated.<p>The only valid calls on the image file
         * returned by {@link #getImageFile()} are {@link ImageFile#getFile()}
         * and {@link ImageFile#getFilename()}, other methods may return null.
         */
        THUMBNAIL_UPDATED,
        /**
         * XMP metadata was updated
         */
        XMP_UPDATED,
        /**
         * EXIF metadata was updated. The only valid getter is
         * {@link ImageFile#getExif() } of {@link #getImageFile()} or
         *  {@link #getOldImageFile()}.
         */
        EXIF_UPDATED
    };
    private static final List<Type> TEXT_METADATA_EVENTS = new ArrayList<Type>(5);
    private              ImageFile  imageFile;
    private              ImageFile  oldImageFile;
    private              Type       type;

    static {
        TEXT_METADATA_EVENTS.add(Type.IMAGEFILE_DELETED);
        TEXT_METADATA_EVENTS.add(Type.IMAGEFILE_INSERTED);
        TEXT_METADATA_EVENTS.add(Type.IMAGEFILE_UPDATED);
        TEXT_METADATA_EVENTS.add(Type.XMP_UPDATED);
    }

    public DatabaseImageFilesEvent(Type type) {
        this.type = type;
    }

    /**
     * Returns the event type.
     *
     * @return event type
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets the event type.
     *
     * @param type event type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Returns the related image file of the event.
     *
     * @return related image file
     */
    public ImageFile getImageFile() {
        return imageFile;
    }

    /**
     * Sets the related image file of the event.
     *
     * @param imageFile related image file
     */
    public void setImageFile(ImageFile imageFile) {
        this.imageFile = imageFile;
    }

    /**
     * Returns the old image file before updating it.
     *
     * @return old image file
     */
    public ImageFile getOldImageFile() {
        return oldImageFile;
    }

    /**
     * Sets the old image file before updating it.
     *
     * @param oldImageFile old image file
     */
    public void setOldImageFile(ImageFile oldImageFile) {
        this.oldImageFile = oldImageFile;
    }

    /**
     * Returns whether text metadata may be affected by the event.
     *
     * @return true if text metadata is affected
     */
    public boolean isTextMetadataAffected() {
        return TEXT_METADATA_EVENTS.contains(type);
    }
}
