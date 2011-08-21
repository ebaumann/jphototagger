package org.jphototagger.program.types;

import org.jphototagger.domain.thumbnails.TypeOfDisplayedImages;
import java.util.ArrayList;
import java.util.List;

/**
 * Utils for {@link TypeOfDisplayedImages}.
 *
 * @author Elmar Baumann
 */
public final class ContentUtil {
    private static final List<TypeOfDisplayedImages> CONTENT_IS_A_FILESYSTEM_DIRECTORY = new ArrayList<TypeOfDisplayedImages>();

    static {
        CONTENT_IS_A_FILESYSTEM_DIRECTORY.add(TypeOfDisplayedImages.DIRECTORY);
        CONTENT_IS_A_FILESYSTEM_DIRECTORY.add(TypeOfDisplayedImages.FAVORITE);
    }

    /**
     * Returns wheter a content is in a single directory.
     *
     * @param  content content
     * @return         true if the content is in a single directory
     */
    public static boolean isSingleDirectoryContent(TypeOfDisplayedImages content) {
        if (content == null) {
            throw new NullPointerException("content == null");
        }

        return CONTENT_IS_A_FILESYSTEM_DIRECTORY.contains(content);
    }

    private ContentUtil() {}
}
