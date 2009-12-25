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
package de.elmar_baumann.jpt.database.metadata.selections;

import java.util.HashSet;
import java.util.Set;

/**
 * Liefert, welche XMP-Metadaten in die Datenbank gespeichert werden.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-14
 */
public final class XmpInDatabase {

    private static final Set<String> STORED_PATHS_PREFIXES =
            new HashSet<String>();

    static {
        STORED_PATHS_PREFIXES.add("Iptc4xmpCore:Location"); // NOI18N
        STORED_PATHS_PREFIXES.add("Iptc4xmpCore:CountryCode"); // NOI18N
        STORED_PATHS_PREFIXES.add("photoshop:Source"); // NOI18N
        STORED_PATHS_PREFIXES.add("photoshop:Credit"); // NOI18N
        STORED_PATHS_PREFIXES.add("photoshop:CaptionWriter"); // NOI18N
        STORED_PATHS_PREFIXES.add("photoshop:AuthorsPosition"); // NOI18N
        STORED_PATHS_PREFIXES.add("photoshop:Headline"); // NOI18N
        STORED_PATHS_PREFIXES.add("photoshop:TransmissionReference"); // NOI18N
        STORED_PATHS_PREFIXES.add("photoshop:Instructions"); // NOI18N
        STORED_PATHS_PREFIXES.add("photoshop:City"); // NOI18N
        STORED_PATHS_PREFIXES.add("photoshop:State"); // NOI18N
        STORED_PATHS_PREFIXES.add("photoshop:Country"); // NOI18N
        STORED_PATHS_PREFIXES.add("dc:title"); // NOI18N
        STORED_PATHS_PREFIXES.add("dc:creator"); // NOI18N
        STORED_PATHS_PREFIXES.add("dc:description"); // NOI18N
        STORED_PATHS_PREFIXES.add("dc:rights"); // NOI18N
        STORED_PATHS_PREFIXES.add("dc:subject"); // NOI18N
    }

    /**
     * Liefert, ob die Metadaten eines XMP-Pfads in die Datenbank gespeichert
     * werden.
     * 
     * @param  path  Pfad
     * @return true, falls gespeichert
     */
    public static boolean isInDatabase(String path) {
        for (String storedPathStartsWith : STORED_PATHS_PREFIXES) {
            if (path.startsWith(storedPathStartsWith)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all path prefixes of XMP paths if the related XMP metadata is
     * stored in the database.
     *
     * @return path prefixes
     */
    public static Set<String> getPathPrefixes() {
        return new HashSet<String>(STORED_PATHS_PREFIXES);
    }

    private XmpInDatabase() {
    }
}
