package de.elmar_baumann.imv.io;

import de.elmar_baumann.imv.app.AppFileFilter;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.io.FileLock;
import de.elmar_baumann.lib.io.filefilter.RegexFileFilter;
import de.elmar_baumann.lib.runtime.External;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * I/O utils.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class IoUtil {

    /**
     * Executes an application and desplays a message dialog on errors.
     * 
     * @param appPath   path to the external application
     * @param arguments arguments added to the path
     */
    public static void execute(String appPath, String arguments) {
        if (!appPath.isEmpty()) {
            String separator = " "; // NOI18N
            String openCommand = appPath + separator + arguments;
            try {
                AppLog.logInfo(IoUtil.class,
                        Bundle.getString("IoUtil.Info.Execute", openCommand)); // NOI18N
                Runtime.getRuntime().exec(External.parseQuotedCommandLine(
                        openCommand));
            } catch (IOException ex) {
                AppLog.logSevere(IoUtil.class, ex);
                MessageDisplayer.error("IoUtil.Error.OpenFile"); // NOI18N
            }
        }
    }

    /**
     * Returns from a list of arbitrary file image files.
     * 
     * @param  files arbitrary files
     * @return       image files of <code>files</code>
     */
    public static List<File> getImageFiles(List<File> files) {
        List<File> imageFiles = new ArrayList<File>();
        RegexFileFilter filter = AppFileFilter.ACCEPTED_IMAGE_FILE_FORMATS;
        for (File file : files) {
            if (filter.accept(file)) {
                imageFiles.add(file);
            }
        }
        return imageFiles;
    }

    /**
     * Returns a String with space separated filenames, each enclosed in quotes.
     * 
     * @param  files  files
     * @param  quote  qoute before and after each filename
     * @return        string
     */
    public static String getQuotedForCommandline(List<File> files, String quote) {
        StringBuffer buffer = new StringBuffer();
        for (File file : files) {
            if (buffer.length() == 0) {
                buffer.append(quote + file.getAbsolutePath() + quote);
            } else {
                buffer.append(" " + quote + file.getAbsolutePath() + quote); // NOI18N
            }
        }
        return buffer.toString();
    }

    /**
     * Locks <em>internally</em> a file (other applications doesn't regognize
     * the lock) and logs errors.
     * <p>
     * If a file couldn't be locked,
     * {@link AppLog#logWarning(java.lang.Class, java.lang.String)} will be
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
    public static boolean lockLogError(File file, Object owner) {
        if (!FileLock.INSTANCE.lock(file, owner)) {
            AppLog.logWarning(owner.getClass(),
                    Bundle.getString("IoUtil.Error.lock",
                    file, owner, FileLock.INSTANCE.getOwner(file)));
            return false;
        }
        return true;
    }

    private IoUtil() {
    }
}
