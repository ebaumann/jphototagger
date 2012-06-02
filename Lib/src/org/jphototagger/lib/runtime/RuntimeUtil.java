package org.jphototagger.lib.runtime;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

import org.jphototagger.lib.io.FileUtil;

/**
 * @author Elmar Baumann
 */
public final class RuntimeUtil {

    private static final String DEFAULT_COMMAND_LINE_QUOTE = "\"";
    private static final String DEFAULT_COMMAND_LINE_SEPARATOR = " ";
    private static final String EMPTY_STRING = "";
    public static final String PATTERN_FS_PATH = "%s";
    public static final String PATTERN_FS_ROOT = "%d";
    public static final String PATTERN_FS_DIR_PATH = "%p";
    public static final String PATTERN_FS_FILE_NAME = "%n";
    public static final String PATTERN_FS_FILE_EXT = "%x";
    private static final Logger LOGGER = Logger.getLogger(RuntimeUtil.class.getName());

    public static String getDefaultCommandLineSeparator() {
        return DEFAULT_COMMAND_LINE_SEPARATOR;
    }

    public static String quoteForCommandLine(File... files) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }
        return quoteForCommandLine(Arrays.asList(files));
    }

    public static String quoteForCommandLine(Collection<? extends File> files) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (File file : files) {
            sb.append(index == 0 ? EMPTY_STRING : DEFAULT_COMMAND_LINE_SEPARATOR);
            sb.append(DEFAULT_COMMAND_LINE_QUOTE);
            sb.append(file.getAbsolutePath());
            sb.append(DEFAULT_COMMAND_LINE_QUOTE);
            index++;
        }
        return sb.toString();
    }

    /**
     * Substitudes in a string placeholders with portions of a filename:
     * <ul>
     * <li>%s with the absolute path of the file</li>
     * <li>%d with {@code  FileUtil#getRoot(java.io.File)}</li>
     * <li>%p with {@code FileUtil#getDirectoryPath(java.io.File)}</li>
     * <li>%n with {@code FileUtil#getPrefix(java.lang.String)}</li>
     * <li>%x with {@code FileUtil#getSuffix(java.lang.String)}</li>
     * </ul>
     *
     * @param  file    file
     * @param  pattern pattern
     * @return         string with replaced
     */
    public static String substitudePattern(File file, String pattern) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        if (pattern == null) {
            throw new NullPointerException("pattern == null");
        }
        String path = file.getAbsolutePath();
        String root = FileUtil.getRoot(new File(path)).getAbsolutePath();
        String dirPath = FileUtil.getDirectoryPath(file);
        String name = FileUtil.getPrefix(file);
        String extension = FileUtil.getSuffix(file);
        return pattern
                .replace(PATTERN_FS_DIR_PATH, dirPath)
                .replace(PATTERN_FS_FILE_EXT, extension)
                .replace(PATTERN_FS_FILE_NAME, name)
                .replace(PATTERN_FS_PATH, path)
                .replace(PATTERN_FS_ROOT, root);
    }

    private RuntimeUtil() {
    }
}
