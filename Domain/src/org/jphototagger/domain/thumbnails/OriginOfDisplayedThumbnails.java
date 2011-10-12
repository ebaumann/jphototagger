package org.jphototagger.domain.thumbnails;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 */
public enum OriginOfDisplayedThumbnails {

    FILES_FOUND_BY_FAST_SEARCH,
    FILES_FOUND_BY_SAVED_SEARCH,
    FILES_IN_SAME_DIRECTORY,
    FILES_IN_SAME_FAVORITE_DIRECTORY,
    FILES_MATCHING_A_KEYWORD,
    FILES_MATCHING_DATES_IN_A_TIMELINE,
    FILES_MATCHING_MISC_METADATA,
    FILES_MATCHING_MISSING_METADATA,
    FILES_OF_AN_IMAGE_COLLECTION,
    UNDEFINED_ORIGIN,;
    /**
     * Contents where images of displayed thumbnails can be deleted from the
     * file system
     */
    private static final Set<OriginOfDisplayedThumbnails> CONTENT_DELETE_IMAGES_FROM_FILESYSTEM_ALLOWED = EnumSet.noneOf(OriginOfDisplayedThumbnails.class);
    /**
     * Contents where images located elsewhere in the file system can be
     * inserted
     */
    private static final Set<OriginOfDisplayedThumbnails> CONTENT_INSERT_IMAGES_FROM_FILESYSTEM_ALLOWED = EnumSet.noneOf(OriginOfDisplayedThumbnails.class);

    static {
        CONTENT_DELETE_IMAGES_FROM_FILESYSTEM_ALLOWED.addAll(Arrays.asList(values()));

        // Insertion is allowed if the directory is not ambigious where the
        // files shall be inserted. This is true if all displayed thumbnails
        // are in the same directory.
        CONTENT_INSERT_IMAGES_FROM_FILESYSTEM_ALLOWED.add(FILES_IN_SAME_DIRECTORY);
        CONTENT_INSERT_IMAGES_FROM_FILESYSTEM_ALLOWED.add(FILES_IN_SAME_FAVORITE_DIRECTORY);
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
        return this.equals(FILES_IN_SAME_DIRECTORY) || this.equals(FILES_IN_SAME_FAVORITE_DIRECTORY);
    }
}
