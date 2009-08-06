package de.elmar_baumann.imv.database.metadata;

import de.elmar_baumann.imv.database.metadata.exif.TableExif;
import de.elmar_baumann.imv.database.metadata.file.TableFiles;
import de.elmar_baumann.imv.database.metadata.xmp.TableXmp;
import java.util.List;

/**
 * SQL-Joins.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class Join {

    /**
     * Type of a SQL join - only the currently used types.
     */
    public enum Type {

        INNER("INNER JOIN"),
        LEFT("LEFT JOIN");
        private final String string;

        private Type(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    /**
     * Liefert den JOIN-Anteil eines SQL-Statements für eine Verknüpfung
     * der Tabelle <code>exif</code> mit verschiedenen EXIF-Tabellen (INNER JOIN;
     * aktuell gibt es nur eine EXIF-Tabele).
     * 
     * @param type       type of join between {@link TableFiles} and
     *                   {@link TableExif}
     * @param tablenames Namen der Tabellen
     * @return           JOIN-Statement
     */
    public static String getSqlFilesExifJoin(Type type, List<String> tablenames) {
        return " files " + type.toString() + " exif on files.id = exif.id_files"; // NOI18N
    }

    /**
     * Liefert den JOIN-Anteil eines SQL-Statements für eine Verknüpfung
     * der xmp-Tabelle mit verschiedenen XMP-Tabellen (LEFT JOIN).
     * 
     * @param typeFiles  type of join between {@link TableFiles} and
     *                   {@link TableXmp}
     * @param typeXmp    type of join between {@link TableXmp} and one of it's
     *                   n:m resolution tables
     * @param tablenames Namen der Tabellen
     * @return           JOIN-Statement
     */
    public static String getSqlFilesXmpJoin(Type typeFiles, Type typeXmp,
            List<String> tablenames) {
        StringBuilder sb = new StringBuilder(" files " + typeFiles.toString() + // NOI18N
                " xmp on files.id = xmp.id_files"); // NOI18N

        for (String tablename : tablenames) {
            if (tablename.startsWith("xmp") && !tablename.equals("xmp")) { // NOI18N
                sb.append(" " + typeXmp.toString() + " " + tablename +
                        " ON xmp.id = " + tablename + ".id_xmp"); // NOI18N
            }
        }

        return sb.toString();
    }

    private Join() {
    }
}
