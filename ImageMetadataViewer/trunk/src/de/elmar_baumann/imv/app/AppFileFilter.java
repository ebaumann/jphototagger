package de.elmar_baumann.imv.app;

import de.elmar_baumann.lib.io.RegexFileFilter;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class AppFileFilter {

    /**
     * Dateifilter für alle Bilddateiformate, die verarbeitet werden können.
     */
    public static final RegexFileFilter acceptedImageFileFormats = new RegexFileFilter(
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

    private AppFileFilter() {}
}
