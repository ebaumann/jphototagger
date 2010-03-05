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
package de.elmar_baumann.jpt.io;

import de.elmar_baumann.jpt.app.AppFileFilters;
import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.lib.io.FileLock;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.io.filefilter.RegexFileFilter;
import de.elmar_baumann.lib.runtime.External;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * I/O utils.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class IoUtil {

    private static final String QUOTE                = "\"";
    private static final String SEPARATOR            = " ";
    private static final String EMPTY                = "";
    private static final String PATTERN_FS_PATH      = "%s";
    private static final String PATTERN_FS_ROOT      = "%d";
    private static final String PATTERN_FS_DIR_PATH  = "%p";
    private static final String PATTERN_FS_FILE_NAME = "%n";
    private static final String PATTERN_FS_FILE_EXT  = "%x";

    /**
     * Executes an application and desplays a message dialog on errors.
     *
     * Does <em>not</em> quote any of the parameters.
     *
     * @param appPath   path to the external application
     * @param arguments arguments added to the path
     */
    public static void execute(String appPath, String arguments) {
        if (!appPath.isEmpty()) {
            String openCommand = appPath + getDefaultCommandLineSeparator() +
                    arguments;
            try {
                AppLogger.logInfo(IoUtil.class, "IoUtil.Info.Execute", openCommand);
                Runtime.getRuntime().exec(External.parseQuotedCommandLine(openCommand));
            } catch (Exception ex) {
                AppLogger.logSevere(IoUtil.class, ex);
                MessageDisplayer.error(null, "IoUtil.Error.OpenFile");
            }
        }
    }

    /**
     * Filters from a collection of arbitrary file image files.
     *
     * @param  arbitraryFiles arbitrary files
     * @return                image files of <code>files</code>
     */
    public static List<File> filterImageFiles(Collection<File> arbitraryFiles) {
        List<File> imageFiles = new ArrayList<File>();
        RegexFileFilter filter = AppFileFilters.ACCEPTED_IMAGE_FILENAME_FILTER;
        for (File file : arbitraryFiles) {
            if (filter.accept(file)) {
                imageFiles.add(file);
            }
        }
        return imageFiles;
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
        return getQuotedForCommandLine(
                Arrays.asList(files),
                getDefaultCommandLineSeparator(),
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
        return getQuotedForCommandLine(
                files,
                getDefaultCommandLineSeparator(),
                getDefaultCommandlineQuote());
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
        String path      = file.getAbsolutePath();
        String root      = FileUtil.getRootName(path);
        String dirPath   = FileUtil.getDirPath(file);
        String name      = FileUtil.getPrefix(file.getName());
        String extension = FileUtil.getSuffix(file.getName());

        return pattern.replace(PATTERN_FS_DIR_PATH , dirPath).
                replace(PATTERN_FS_FILE_EXT , extension).
                replace(PATTERN_FS_FILE_NAME, name).
                replace(PATTERN_FS_PATH     , path).
                replace(PATTERN_FS_ROOT     , root)
                ;
    }

    private static String getQuotedForCommandLine(
            Collection<? extends File> files, String separator, String quote) {

        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (File file : files) {
            sb.append((index++ == 0
                    ? EMPTY
                    : separator) + quote + file.getAbsolutePath() + quote);
        }
        return sb.toString();
    }

    /**
     * Locks <em>internally</em> a file (other applications doesn't regognize
     * the lock) and logs a warning if the file couldn't be locked.
     * <p>
     * If a file couldn't be locked,
     * {@link AppLogger#logWarning(java.lang.Class, java.lang.String, Object[])} will be
     * called.
     * <p>
     * Uses {@link FileLock#lock(java.io.File, java.lang.Object)}. <em>The
     * caller has to call {@link FileLock#unlock(java.io.File, java.lang.Object)}
     * after using the file!</em>
     *
     * @param  file  file to lock
     * @param  owner owner of the file lock
     * @return       true if the file was locked
     */
    public static boolean lockLogWarning(File file, Object owner) {
        if (!FileLock.INSTANCE.lock(file, owner)) {
            AppLogger.logWarning(owner.getClass(), "IoUtil.Error.lock", file, owner, FileLock.INSTANCE.getOwner(file));
            return false;
        }
        return true;
    }

    private IoUtil() {
    }
}
