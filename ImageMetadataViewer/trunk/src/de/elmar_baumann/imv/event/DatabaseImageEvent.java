package de.elmar_baumann.imv.event;

import de.elmar_baumann.imv.data.ImageFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Event in a database related to an image.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-15
 */
public final class DatabaseImageEvent {

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
         * A in the filesystem not existing image file was deleted from the
         * database
         */
        NOT_EXISTING_IMAGEFILES_DELETED,
        /**
         * A thumbnail was updated
         */
        THUMBNAIL_UPDATED,
        /**
         * XMP metadata was updated
         */
        XMP_UPDATED,
    };
    private static final List<Type> TEXT_METADATA_EVENTS =
            new ArrayList<Type>(5);
    private ImageFile imageFile;
    private ImageFile oldImageFile;
    private Type type;

    static {
        TEXT_METADATA_EVENTS.add(Type.IMAGEFILE_DELETED);
        TEXT_METADATA_EVENTS.add(Type.IMAGEFILE_INSERTED);
        TEXT_METADATA_EVENTS.add(Type.IMAGEFILE_UPDATED);
        TEXT_METADATA_EVENTS.add(Type.NOT_EXISTING_IMAGEFILES_DELETED);
        TEXT_METADATA_EVENTS.add(Type.XMP_UPDATED);
    }

    public DatabaseImageEvent(Type type) {
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
