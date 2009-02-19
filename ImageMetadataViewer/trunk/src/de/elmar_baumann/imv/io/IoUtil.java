package de.elmar_baumann.imv.io;

import de.elmar_baumann.imv.AppIcons;
import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.io.FileFilter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

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
                Runtime.getRuntime().exec(openCommand);
            } catch (IOException ex) {
                de.elmar_baumann.imv.Log.logWarning(IoUtil.class, ex);
                JOptionPane.showMessageDialog(null,
                    Bundle.getString("IoUtil.ErrorMessage.OpenFile"),
                    Bundle.getString("IoUtil.ErrorMessage.OpenFile.Title"),
                    JOptionPane.ERROR_MESSAGE,
                    AppIcons.getMediumAppIcon());
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
        FileFilter filter = AppSettings.fileFilterAcceptedImageFileFormats;
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
            buffer.append(" " + quote + file.getAbsolutePath() + quote);
        }
        return buffer.toString();
    }

    private IoUtil() {}
}
