package de.elmar_baumann.imv.event;

/**
 * Files will be checked whether their metadata shall be updated.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-06
 */
public final class CheckForUpdateMetadataEvent {

    /**
     * Check type
     */
    public enum Type {

        /**
         * Check will be started
         */
        CHECK_STARTED,
        /**
         * A file will be checked for update
         */
        CHECKING_FILE,
        /**
         * Check has been finished
         */
        CHECK_FINISHED,
    }
    private final Type type;
    private final String imageFilename;

    public CheckForUpdateMetadataEvent(Type type, String imageFilename) {
        this.type = type;
        this.imageFilename = imageFilename;
    }

    /**
     * Returns the file that will be checked for update.
     *
     * @return file or null when the event is not {@link Type#CHECKING_FILE}
     */
    public String getImageFilename() {
        return imageFilename;
    }

    /**
     * Returns the check type.
     *
     * @return type
     */
    public Type getType() {
        return type;
    }
}
