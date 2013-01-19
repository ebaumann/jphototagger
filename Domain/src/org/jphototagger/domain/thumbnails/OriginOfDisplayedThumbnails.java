package org.jphototagger.domain.thumbnails;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author Elmar Baumann
 */
public enum OriginOfDisplayedThumbnails {

    FILES_FOUND_BY_FAST_SEARCH,
    FILES_FOUND_BY_SAVED_SEARCH,
    FILES_IN_SAME_DIRECTORY,
    FILES_IN_DIRECTORY_RECURSIVE,
    FILES_IN_SAME_FAVORITE_DIRECTORY,
    FILES_IN_FAVORITE_DIRECTORY_RECURSIVE,
    FILES_MATCHING_A_KEYWORD,
    FILES_MATCHING_DATES_IN_A_TIMELINE,
    FILES_MATCHING_MISC_METADATA,
    FILES_MATCHING_MISSING_METADATA,
    FILES_OF_AN_IMAGE_COLLECTION,
    UNDEFINED_ORIGIN,
    ;

    private static final Set<OriginOfDisplayedThumbnails> CONTENT_IS_SORTABLE = EnumSet.allOf(OriginOfDisplayedThumbnails.class);
    private static final Set<OriginOfDisplayedThumbnails> CONTENT_IS_FILTERABLE = EnumSet.allOf(OriginOfDisplayedThumbnails.class);
    private static final Set<OriginOfDisplayedThumbnails> CONTENT_DELETE_IMAGES_FROM_FILESYSTEM_ALLOWED = EnumSet.allOf(OriginOfDisplayedThumbnails.class);
    private static final Set<OriginOfDisplayedThumbnails> CONTENT_INSERT_IMAGES_FROM_FILESYSTEM_ALLOWED = EnumSet.noneOf(OriginOfDisplayedThumbnails.class);

    static {
        CONTENT_INSERT_IMAGES_FROM_FILESYSTEM_ALLOWED.add(FILES_IN_SAME_DIRECTORY);
        CONTENT_INSERT_IMAGES_FROM_FILESYSTEM_ALLOWED.add(FILES_IN_SAME_FAVORITE_DIRECTORY);
        CONTENT_INSERT_IMAGES_FROM_FILESYSTEM_ALLOWED.add(FILES_IN_DIRECTORY_RECURSIVE);
        CONTENT_INSERT_IMAGES_FROM_FILESYSTEM_ALLOWED.add(FILES_IN_FAVORITE_DIRECTORY_RECURSIVE);

        CONTENT_IS_SORTABLE.remove(FILES_OF_AN_IMAGE_COLLECTION);

        CONTENT_IS_FILTERABLE.remove(FILES_OF_AN_IMAGE_COLLECTION);
    }

    public boolean canDeleteImagesFromFileSystem() {
        return CONTENT_DELETE_IMAGES_FROM_FILESYSTEM_ALLOWED.contains(this);
    }

    public boolean canInsertImagesFromFileSystem() {
        return CONTENT_INSERT_IMAGES_FROM_FILESYSTEM_ALLOWED.contains(this);
    }

    public boolean isInSameFileSystemDirectory() {
        return this.equals(FILES_IN_SAME_DIRECTORY) || this.equals(FILES_IN_SAME_FAVORITE_DIRECTORY);
    }

    public boolean isSortable() {
        return CONTENT_IS_SORTABLE.contains(this);
    }

    public boolean isFilterable() {
        return CONTENT_IS_FILTERABLE.contains(this);
    }
}
