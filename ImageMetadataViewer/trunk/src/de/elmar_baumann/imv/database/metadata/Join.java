package de.elmar_baumann.imv.database.metadata;

import java.util.List;

/**
 * SQL-Joins.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class Join {

    /**
     * Liefert den JOIN-Anteil eines SQL-Statements für eine Verknüpfung
     * der Tabelle <code>exif</code> mit verschiedenen EXIF-Tabellen (INNER JOIN;
     * aktuell gibt es nur eine EXIF-Tabele).
     * 
     * @param tablenames Namen der Tabellen
     * @return           JOIN-Statement
     */
    public static String getSqlFilesExifJoin(List<String> tablenames) {
        return " files INNER JOIN exif on files.id = exif.id_files "; // NOI18N
    }

    /**
     * Liefert den JOIN-Anteil eines SQL-Statements für eine Verknüpfung
     * der xmp-Tabelle mit verschiedenen XMP-Tabellen (LEFT JOIN).
     * 
     * @param tablenames Namen der Tabellen
     * @return           JOIN-Statement
     */
    public static String getSqlFilesXmpJoin(List<String> tablenames) {
        StringBuffer join = new StringBuffer(
            " files INNER JOIN xmp on files.id = xmp.id_files"); // NOI18N

        for (String tablename : tablenames) {
            if (tablename.startsWith("xmp") && !tablename.equals("xmp")) { // NOI18N
                join.append(" LEFT JOIN " + tablename + " ON xmp.id = " + // NOI18N
                    tablename + ".id_xmp"); // NOI18N
            }
        }

        return join.toString();
    }

    private Join() {}
}
