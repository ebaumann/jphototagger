package org.jphototagger.program.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Utils for {@link Content}.
 *
 * @author Elmar Baumann
 */
public final class ContentUtil {
    private static final List<Content> CONTENT_IS_A_FILESYSTEM_DIRECTORY = new ArrayList<Content>();

    static {
        CONTENT_IS_A_FILESYSTEM_DIRECTORY.add(Content.DIRECTORY);
        CONTENT_IS_A_FILESYSTEM_DIRECTORY.add(Content.FAVORITE);
    }

    /**
     * Returns wheter a content is in a single directory.
     *
     * @param  content content
     * @return         true if the content is in a single directory
     */
    public static boolean isSingleDirectoryContent(Content content) {
        if (content == null) {
            throw new NullPointerException("content == null");
        }

        return CONTENT_IS_A_FILESYSTEM_DIRECTORY.contains(content);
    }

    private ContentUtil() {}
}
