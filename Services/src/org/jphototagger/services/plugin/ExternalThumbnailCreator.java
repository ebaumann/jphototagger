package org.jphototagger.services.plugin;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface ExternalThumbnailCreator {

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
