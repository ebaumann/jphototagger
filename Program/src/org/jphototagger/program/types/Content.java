package org.jphototagger.program.types;

import org.jphototagger.program.view.panels.ThumbnailsPanel;

import java.util.HashSet;
import java.util.Set;

/**
 * Content type of the displayed thumbnails. In other words: The content of the
 * {@link ThumbnailsPanel}.
 */
public enum Content {

    /**
     * The displayed thumbnails are the images of one directory in the file
     * system.
     */
    DIRECTORY,

    /**
     * The displayed thumbnails are the images of a result of a fast search.
     * The images can be located in multiple file system directories.
     */
    FAST_SEARCH,

    /**
     * The displayed thumbnails are the images of one favorite directory located
     * in the filesystem.
     */
    FAVORITE,

    /**
     * The displayed thumbnails are the images of an image collection.
     * The images can be located in multiple file system directories.
     */
    IMAGE_COLLECTION,

    /**
     * The displayed thumbnails are the images with a specific keyword.
     * The images can be located in multiple file system directories.
     */
    KEYWORD,

    /**
     * The displayed thumbnails are the images of a date in a timeline.
     * The images can be located in multiple file system directories.
     */
    TIMELINE,

    /**
     * The displayed thumbnails are the images of a specific meta date.
     * The images can be located in multiple file system directories.
     */
    MISC_METADATA,

    /**
     * The displayed thumbnails are images without specific metadata
     */
    MISSING_METADATA,

    /**
     * The displayed thumbnails are the images of a saved search.
     * The images can be located in multiple file system directories.
     */
    SAVED_SEARCH,

    /**
     * The type of the displayed thumbnails is not known.
     */
    UNDEFINED,;

    /**
     * Contents where images of displayed thumbnails can be deleted from the
     * file system
     */
    private static final Set<Content> CONTENT_DELETE_IMAGES_FROM_FILESYSTEM_ALLOWED =
        new HashSet<Content>();

    /**
     * Contents where images located elsewhere in the file system can be
     * inserted
     */
    private static final Set<Content> CONTENT_INSERT_IMAGES_FROM_FILESYSTEM_ALLOWED =
        new HashSet<Content>();

    static {

        // Deletion from the file system is always allowed if the content is not
        // not an image collection where deletion means deleting an image from
        // the image collection and not from the file system
        for (Content content : values()) {
            CONTENT_DELETE_IMAGES_FROM_FILESYSTEM_ALLOWED.add(content);
        }

        // Insertion is allowed if the directory is not ambigious where the
        // files shall be inserted. This is true if all displayed thumbnails
        // are in the same directory.
        CONTENT_INSERT_IMAGES_FROM_FILESYSTEM_ALLOWED.add(DIRECTORY);
        CONTENT_INSERT_IMAGES_FROM_FILESYSTEM_ALLOWED.add(FAVORITE);
    }

    /**
     * Returns whether images in this content can be deleted from file system.
     *
     * @return true if images in this content can be deleted from file system
     */
    public boolean canDeleteImagesFromFileSystem() {
        return CONTENT_DELETE_IMAGES_FROM_FILESYSTEM_ALLOWED.contains(this);
    }

    /**
     * Returns whether images from the file system can be inserted into this
     * content.
     *
     * @return true if images from file system can be inserted into this content
     */
    public boolean canInsertImagesFromFileSystem() {
        return CONTENT_INSERT_IMAGES_FROM_FILESYSTEM_ALLOWED.contains(this);
    }

    /**
     * Returns wheter the displayed thumbnails are images in a unique directory
     * (can't show images of more than one directory).
     *
     * @return true if the directory is unique
     */
    public boolean isUniqueFileSystemDirectory() {
        return this.equals(DIRECTORY) || this.equals(FAVORITE);
    }
}
