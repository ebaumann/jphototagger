package org.jphototagger.program.image.thumbnail;

/**
 * How to create humbnails.
 *
 * @author Elmar Baumann
 */
public enum ThumbnailCreator {

    /**
     * Get in the image embedded thumbnail
     */
    EMBEDDED,

    /**
     * Create thumbnail with external application
     */
    EXTERNAL_APP,

    /**
     * Create thumbnail with Imagero
     */
    IMAGERO,

    /**
     * Create thumbnail with the Java Image IO
     */
    JAVA_IMAGE_IO,
}
