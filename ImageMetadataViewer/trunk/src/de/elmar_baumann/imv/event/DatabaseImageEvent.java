package de.elmar_baumann.imv.event;

import de.elmar_baumann.imv.data.ImageFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Event in a database related to an image.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/15
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
        MAINTAINANCE_NOT_EXISTING_IMAGEFILES_DELETED,
        /**
         * A thumbnail was updated
         */
        THUMBNAIL_UPDATED,
        /**
         * XMP metadata was updated
         */
        XMP_UPDATED,
    };
    private static final List<Type> metadataEvents = new ArrayList<Type>(5);
    private ImageFile imageFile;
    private Type type;


    static {
        metadataEvents.add(Type.IMAGEFILE_DELETED);
        metadataEvents.add(Type.IMAGEFILE_INSERTED);
        metadataEvents.add(Type.IMAGEFILE_UPDATED);
        metadataEvents.add(Type.MAINTAINANCE_NOT_EXISTING_IMAGEFILES_DELETED);
        metadataEvents.add(Type.XMP_UPDATED);
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
     * Returns wether text metadata may be affected by the event.
     *
     * @return true if text metadata is affected
     */
    public boolean isTextMetadataAffected() {
        return metadataEvents.contains(type);
    }
}
