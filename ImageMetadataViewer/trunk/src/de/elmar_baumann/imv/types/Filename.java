package de.elmar_baumann.imv.types;

/**
 * Token of a filename.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/19
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
