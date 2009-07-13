package de.elmar_baumann.imv.io;

import de.elmar_baumann.imv.app.AppFileFilter;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.io.filefilter.RegexFileFilter;
import de.elmar_baumann.lib.runtime.External;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utils für I/O.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class IoUtil {

    /**
     * Startet eine Anwendung und zeigt im Fehlerfall einen Messagedialog.
     * 
     * @param appPath   Pfad zur Anwendung
     * @param arguments Argumente die dem Pfad angehängt werden
     */
    public static void execute(String appPath, String arguments) {
        if (!appPath.isEmpty()) {
            String separator = " "; // NOI18N
            String openCommand = appPath + separator + arguments;
            try {
                AppLog.logInfo(IoUtil.class,
                        Bundle.getString("IoUtil.Info.Execute", openCommand)); // NOI18N
                Runtime.getRuntime().exec(External.parseQuotedCommandLine(openCommand));
            } catch (IOException ex) {
                AppLog.logWarning(IoUtil.class, ex);
                MessageDisplayer.error("IoUtil.ErrorMessage.OpenFile"); // NOI18N
            }
        }
    }

    /**
     * Returns image files.
     * 
     * @param  files  arbitrary files
     * @return image files of <code>files</code>
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
     * @return string
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

    private IoUtil() {
    }
}
