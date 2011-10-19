package org.jphototagger.program.app.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.lib.util.SystemProperties;
import org.jphototagger.program.app.AppInfo;

/**
 * @author Elmar Baumann
 */
public final class AppLogUtil {

    private static final String LINE_SEP = System.getProperty("line.separator");

    public static void logSystemInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(LINE_SEP).append("JPhotoTagger " + AppInfo.APP_VERSION);
        sb.append(SystemProperties.systemInfoToString());
        Logger.getLogger(AppLogUtil.class.getName()).log(Level.INFO, sb.toString());
    }

    private AppLogUtil() {
    }
}
