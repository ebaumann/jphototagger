package de.elmar_baumann.imv;

import de.elmar_baumann.lib.io.FileFilter;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import java.io.File;

/**
 * Anwendungseinstellungen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class AppSettings {

    /**
     * Dateifilter für alle Bilddateiformate, die verarbeitet werden können.
     */
    public static final FileFilter fileFilterAcceptedImageFileFormats = new FileFilter(
        ".*\\.[cC][rR][wW];" + // NOI18N
        ".*\\.[cC][rR]2;" + // NOI18N
        ".*\\.[dD][cC][rR];" + // NOI18N
        ".*\\.[dD][nN][gG];" + // NOI18N
        ".*\\.[jJ][pP][gG];" + // NOI18N
        ".*\\.[jJ][pP][eE][gG];" + // NOI18N
        ".*\\.[mM][rR][wW];" + // NOI18N
        ".*\\.[nN][eE][fF];" + // NOI18N
        ".*\\.[tT][hH][mM];" + // NOI18N
        ".*\\.[tT][iI][fF];" + // NOI18N
        ".*\\.[tT][iI][fF][fF];", // NOI18N
        ";");  // NOI18N

    /**
     * Liefert den Namen der Logdatei.
     * 
     * @return Logdatei
     */
    public static String getLogfileName() {
        return PersistentSettings.getInstance().getDirectoryName() +
            File.separator + "imagemetadataviewerlog.xml";  // NOI18N
    }

    private AppSettings() {}
}
