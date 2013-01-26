package org.jphototagger.program.app.logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.util.SystemProperties;
import org.jphototagger.program.app.AppInfo;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class AppLogUtil {

    private static final String LINE_SEP = SystemProperties.getLineSeparator();;

    public static void logSystemInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(LINE_SEP).append("JPhotoTagger " + AppInfo.APP_VERSION);
        sb.append(SystemProperties.systemInfoToString());
        sb.append(LINE_SEP);
        sb.append(getPreferences());
        sb.append(LINE_SEP);
        Logger.getLogger(AppLogUtil.class.getName()).log(Level.INFO, sb.toString());
    }

    private static String getPreferences() {
        StringBuilder sb = new StringBuilder();
        sb.append("Preferences:");
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        List<String> keys = new ArrayList<>(prefs.keys());
        Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
        for (String key : keys) {
            sb.append(LINE_SEP).append(key).append(':').append(prefs.getString(key));
        }
        return sb.toString();
    }

    private AppLogUtil() {
    }
}
