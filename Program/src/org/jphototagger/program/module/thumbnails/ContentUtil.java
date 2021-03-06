package org.jphototagger.program.module.thumbnails;

import java.util.ArrayList;
import java.util.List;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;

/**
 * Utils for {@code OriginOfDisplayedThumbnails}.
 *
 * @author Elmar Baumann
 */
public final class ContentUtil {

    private static final List<OriginOfDisplayedThumbnails> CONTENT_IS_A_FILESYSTEM_DIRECTORY = new ArrayList<>();

    static {
        CONTENT_IS_A_FILESYSTEM_DIRECTORY.add(OriginOfDisplayedThumbnails.FILES_IN_SAME_DIRECTORY);
        CONTENT_IS_A_FILESYSTEM_DIRECTORY.add(OriginOfDisplayedThumbnails.FILES_IN_DIRECTORY_RECURSIVE);
        CONTENT_IS_A_FILESYSTEM_DIRECTORY.add(OriginOfDisplayedThumbnails.FILES_IN_SAME_FAVORITE_DIRECTORY);
        CONTENT_IS_A_FILESYSTEM_DIRECTORY.add(OriginOfDisplayedThumbnails.FILES_IN_FAVORITE_DIRECTORY_RECURSIVE);
    }

    /**
     * Returns wheter a content is in a single directory.
     *
     * @param  content content
     * @return         true if the content is in a single directory
     */
    public static boolean isSingleDirectoryContent(OriginOfDisplayedThumbnails content) {
        if (content == null) {
            throw new NullPointerException("content == null");
        }
        return CONTENT_IS_A_FILESYSTEM_DIRECTORY.contains(content);
    }

    private ContentUtil() {
    }
}
