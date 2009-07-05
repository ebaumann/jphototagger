package de.elmar_baumann.imv.types;

import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import java.util.HashSet;
import java.util.Set;

/**
 * Content type of the displayed thumbnails. In other words: The content of the
 * {@link ImageFileThumbnailsPanel}.
 */
public enum Content {

    /**
     * The displayed thumbnails are the images with a specific category.
     * The images can be located in multiple file system directories.
     */
    CATEGORY,
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
     * The displayed thumbnails are the images of a saved search.
     * The images can be located in multiple file system directories.
     */
    SAFED_SEARCH,
    /**
     * The type of the displayed thumbnails is not known.
     */
    UNDEFINED,;
    /**
     * Contents where images of displayed thumbnails can be deleted from the
     * file system
     */
    private static final Set<Content> contentDeleteImagesFromFileSystemAllowed =
            new HashSet<Content>();
    /**
     * Contents where images located elsewhere in the file system can be
     * inserted
     */
    private static final Set<Content> contentInsertImagesFromFileSystemAllowed =
            new HashSet<Content>();
    /**
     * Contents which is sortable
     */
    private static final Set<Content> sortableContent = new HashSet<Content>();

    static {
        // Deletion from the file system is always allowed if the content is not
        // not an image collection where deletion means deleting an image from
        // the image collection and not from the file system
        for (Content content : values()) {
            contentDeleteImagesFromFileSystemAllowed.add(content);
        }
        contentDeleteImagesFromFileSystemAllowed.remove(IMAGE_COLLECTION);

        // Insertion is allowed if the directory is not ambigious where the
        // files shall be inserted. This is true if all displayed thumbnails
        // are in the same directory.
        contentInsertImagesFromFileSystemAllowed.add(DIRECTORY);
        contentInsertImagesFromFileSystemAllowed.add(FAVORITE);

        // Currently only image collections are not sortable
        sortableContent.add(CATEGORY);
        sortableContent.add(DIRECTORY);
        sortableContent.add(FAST_SEARCH);
        sortableContent.add(FAVORITE);
        sortableContent.add(KEYWORD);
        sortableContent.add(MISC_METADATA);
        sortableContent.add(SAFED_SEARCH);
        sortableContent.add(TIMELINE);
    }

    /**
     * Returns whether images in this content can be deleted from file system.
     *
     * @return true if images in this content can be deleted from file system
     */
    public boolean canDeleteImagesFromFileSystem() {
        return contentDeleteImagesFromFileSystemAllowed.contains(this);
    }

    /**
     * Returns whether images from the file system can be inserted into this
     * content.
     *
     * @return true if images from file system can be inserted into this content
     */
    public boolean canInsertImagesFromFileSystem() {
        return contentInsertImagesFromFileSystemAllowed.contains(this);
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

    /**
     * Returns whether the content is sortable.
     *
     * @return true if the content is sortable
     */
    public boolean isSortable() {
        return sortableContent.contains(this);
    }
}
