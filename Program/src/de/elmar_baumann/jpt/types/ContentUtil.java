/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Utils for {@link Content}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-14
 */
public final class ContentUtil {

    private static final List<Content> CONTENT_IS_A_FILESYSTEM_DIRECTORY =
            new ArrayList<Content>();

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
        return CONTENT_IS_A_FILESYSTEM_DIRECTORY.contains(content);
    }

    private ContentUtil() {
    }
}
