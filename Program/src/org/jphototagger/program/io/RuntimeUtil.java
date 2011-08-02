package org.jphototagger.program.io;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.runtime.External;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.dialog.MessageDisplayer;

/**
 * I/O utils.
 *
 * @author Elmar Baumann
 */
public final class RuntimeUtil {

    private static final String QUOTE = "\"";
    private static final String SEPARATOR = " ";
    private static final String EMPTY = "";
    public static final String PATTERN_FS_PATH = "%s";
    public static final String PATTERN_FS_ROOT = "%d";
    public static final String PATTERN_FS_DIR_PATH = "%p";
    public static final String PATTERN_FS_FILE_NAME = "%n";
    public static final String PATTERN_FS_FILE_EXT = "%x";
    private static final Logger LOGGER = Logger.getLogger(RuntimeUtil.class.getName());

    /**
     * Executes an application and desplays a message dialog on errors.
     *
     * Does <em>not</em> quote any of the parameters.
     *
     * @param appPath   path to the external application
     * @param arguments arguments added to the path
     */
    public static void execute(String appPath, String arguments) {
        if (appPath == null) {
            throw new NullPointerException("appPath == null");
        }

        if (arguments == null) {
            throw new NullPointerException("arguments == null");
        }

        if (!appPath.isEmpty()) {
            String openCommand = appPath + getDefaultCommandLineSeparator() + arguments;

            try {
                LOGGER.log(Level.INFO, "Executing command: ''{0}''", openCommand);
                Runtime.getRuntime().exec(External.parseQuotedCommandLine(openCommand));
            } catch (Exception ex) {
                Logger.getLogger(RuntimeUtil.class.getName()).log(Level.SEVERE, null, ex);
                String message = Bundle.getString(RuntimeUtil.class, "IoUtil.Error.OpenFile");
                MessageDisplayer.error(null, message);
            }
        }
    }

    /**
     * Returns the default quote string for quoting command line tokens.
     *
     * @return quote string
     */
    public static String getDefaultCommandlineQuote() {
        return QUOTE;
    }

    /**
     * Returns the default separator string for separating command line tokens.
     *
     * @return separator string
     */
    public static String getDefaultCommandLineSeparator() {
        return SEPARATOR;
    }

    public static String quoteForCommandLine(String string) {
        if (string == null) {
            throw new NullPointerException("string == null");
        }

        String quote = getDefaultCommandlineQuote();

        return quote + string + quote;
    }

    /**
     * Quotes a string and a file with {@link #getDefaultCommandlineQuote()} and
     * separates them with a space character.
     *
     * @param  string  usually the program path
     * @param  file    usually the file to open with the program
     * @return         quoted string
     */
    public static String quoteForCommandLine(String string, File file) {
        if (string == null) {
            throw new NullPointerException("string == null");
        }

        if (file == null) {
            throw new NullPointerException("file == null");
        }

        String quote = getDefaultCommandlineQuote();
        String separator = getDefaultCommandLineSeparator();

        return quote + string + quote + separator + quote + file.getAbsolutePath() + quote;
    }

    /**
     * Quotes each file with {@link #getDefaultCommandlineQuote()} and separates
     * them with a space character.
     *
     * @param  files files
     * @return       quoted string
     */
    public static String quoteForCommandLine(File... files) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }

        return getQuotedForCommandLine(Arrays.asList(files), getDefaultCommandLineSeparator(),
                getDefaultCommandlineQuote());
    }

    /**
     * Quotes each file of a collection with
     * {@link #getDefaultCommandlineQuote()} and separates them with a space character.
     *
     * @param  files files
     * @return       quoted string
     */
    public static String quoteForCommandLine(Collection<? extends File> files) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }

        return getQuotedForCommandLine(files, getDefaultCommandLineSeparator(), getDefaultCommandlineQuote());
    }

    /**
     * Substitudes in a string placeholders with portions of a filename:
     * <ul>
     * <li>%s with the absolute path of the file</li>
     * <li>%d with {@link FileUtil#getRootName(java.lang.String)}</li>
     * <li>%p with {@link FileUtil#getDirPath(java.io.File)}</li>
     * <li>%n with {@link FileUtil#getPrefix(java.lang.String)}</li>
     * <li>%x with {@link FileUtil#getSuffix(java.lang.String)}</li>
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

        return pattern.replace(PATTERN_FS_DIR_PATH, dirPath)
                .replace(PATTERN_FS_FILE_EXT, extension)
                .replace(PATTERN_FS_FILE_NAME, name)
                .replace(PATTERN_FS_PATH,
                path).replace(PATTERN_FS_ROOT, root);
    }

    private static String getQuotedForCommandLine(Collection<? extends File> files, String separator, String quote) {
        StringBuilder sb = new StringBuilder();
        int index = 0;

        for (File file : files) {
            sb.append((index++ == 0)
                    ? EMPTY
                    : separator).append(quote).append(file.getAbsolutePath()).append(quote);
        }

        return sb.toString();
    }

    private RuntimeUtil() {
    }
}
