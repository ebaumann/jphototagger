/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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

        INNER("INNER JOIN"), // NOI18N
        LEFT("LEFT JOIN"); // NOI18N
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
                sb.append(" " + typeXmp.toString() + " " + tablename + // NOI18N
                        " ON xmp.id = " + tablename + ".id_xmp"); // NOI18N
            }
        }

        return sb.toString();
    }

    private Join() {
    }
}
