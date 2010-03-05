/*
 * JPhotoTagger tags and finds images fast
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

/**
 * Token of a filename.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-19
 */
public enum Filename {

    /**
     * The prefix of a filename without it's parent directories
     */
    PREFIX,
    /**
     * The suffix (postfix) of a filename. This is usually a shorthand of the
     * file type (encoding).
     */
    SUFFIX,
    /**
     * The name of a file without it's parent directories
     */
    NAME,
    /**
     * The parent directories of the file up to the root of the file system
     * without the file name
     */
    PARENT_DIRECTORIES,
    /**
     * The parent directories of the file up to the root of the file system
     * with the file name but <em>not</em> the suffix
     */
    FULL_PATH_NO_SUFFIX,
    /**
     * The full path of a filename including all parent directories up to the
     * file system's root
     */
    FULL_PATH,
}
