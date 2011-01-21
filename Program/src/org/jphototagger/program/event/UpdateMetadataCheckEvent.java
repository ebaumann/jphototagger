package org.jphototagger.program.event;

import java.io.File;

/**
 * Files will be checked whether their metadata shall be updated.
 *
 * @author Elmar Baumann
 */
public final class UpdateMetadataCheckEvent {
    private final File imageFile;
    private final Type type;

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

    /**
     *
     * @param type      can be null
     * @param imageFile
     */
    public UpdateMetadataCheckEvent(Type type, File imageFile) {
        if (type == null) {
            throw new NullPointerException("type == null");
        }

        this.type      = type;
        this.imageFile = imageFile;
    }

    /**
     * Returns the file that will be checked for update.
     *
     * @return file or null when the event is not {@link Type#CHECKING_FILE}
     */
    public File getImageFile() {
        return imageFile;
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
