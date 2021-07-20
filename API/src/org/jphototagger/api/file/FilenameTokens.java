package org.jphototagger.api.file;

/**
 * Tokens of a filename.
 *
 * @author Elmar Baumann
 */
public enum FilenameTokens {

    /**
     * The prefix of a filename without it's parent directories.
     * <p>
     * <<strong>Example:</strong>
     * <code>myfile</code> is the prefix of <code>/home/elmar/myfile.txt</code>.
     */
    PREFIX,
    /**
     * The suffix (postfix) of a filename. This is usually a shorthand for it's
     * file type (encoding).
     * <p>
     * <<strong>Example:</strong>
     * <code>txt</code> is the suffix of <code>/home/elmar/myfile.txt</code>.
     */
    SUFFIX,
    /**
     * The name of a file without it's parent directories.
     * <p>
     * <<strong>Example:</strong>
     * <code>myfile.txt</code> is the name of <code>/home/elmar/myfile.txt</code>.
     */
    NAME,
    /**
     * The parent directories of the file up to the root of the file system
     * without the file name.
     * <p>
     * <<strong>Example:</strong>
     * <code>/home/elmar</code> are the parent directories of <code>/home/elmar/myfile.txt</code>.
     */
    PARENT_DIRECTORIES,
    /**
     * The parent directories of the file up to the root of the file system
     * with the file name but <em>not</em> the suffix.
     * <p>
     * <<strong>Example:</strong>
     * <code>/home/elmar/myfile</code> is the full path without prefix for <code>/home/elmar/myfile.txt</code>.
     */
    FULL_PATH_NO_SUFFIX,
    /**
     * The full path of a filename including all parent directories up to the
     * file system's root.
     * <p>
     * <<strong>Example:</strong>
     * <code>/home/elmar/myfile.txt</code> is the full path of <code>/home/elmar/myfile.txt</code>.
     */
    FULL_PATH,}
