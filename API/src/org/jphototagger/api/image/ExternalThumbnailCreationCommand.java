package org.jphototagger.api.image;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface ExternalThumbnailCreationCommand {

    /**
     *
     * @return null if undefined
     */
    String getThumbnailCreationCommand();

    String getDisplayName();

    /**
     *
     * @return false e.g. if not aviable for the current operating system
     */
    boolean isEnabled();
}
