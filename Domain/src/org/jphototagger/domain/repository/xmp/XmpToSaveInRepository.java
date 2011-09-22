package org.jphototagger.domain.repository.xmp;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Elmar Baumann
 */
public final class XmpToSaveInRepository {
    private static final Set<String> STORED_PATHS_PREFIXES = new HashSet<String>();

    static {
        STORED_PATHS_PREFIXES.add("dc:creator");
        STORED_PATHS_PREFIXES.add("dc:description");
        STORED_PATHS_PREFIXES.add("dc:rights");
        STORED_PATHS_PREFIXES.add("dc:subject");
        STORED_PATHS_PREFIXES.add("dc:title");
        STORED_PATHS_PREFIXES.add("Iptc4xmpCore:CountryCode");
        STORED_PATHS_PREFIXES.add("Iptc4xmpCore:DateCreated");
        STORED_PATHS_PREFIXES.add("Iptc4xmpCore:Location");
        STORED_PATHS_PREFIXES.add("photoshop:AuthorsPosition");
        STORED_PATHS_PREFIXES.add("photoshop:CaptionWriter");
        STORED_PATHS_PREFIXES.add("photoshop:City");
        STORED_PATHS_PREFIXES.add("photoshop:Country");
        STORED_PATHS_PREFIXES.add("photoshop:Credit");
        STORED_PATHS_PREFIXES.add("photoshop:Headline");
        STORED_PATHS_PREFIXES.add("photoshop:Instructions");
        STORED_PATHS_PREFIXES.add("photoshop:Source");
        STORED_PATHS_PREFIXES.add("photoshop:State");
        STORED_PATHS_PREFIXES.add("photoshop:TransmissionReference");
        STORED_PATHS_PREFIXES.add("xap:Rating");
    }

    /**
     * Liefert, ob die Metadaten eines XMP-Pfads in die Datenbank gespeichert
     * werden.
     *
     * @param  path  Pfad
     * @return true, falls gespeichert
     */
    public static boolean isSaveInRepository(String path) {
        if (path == null) {
            throw new NullPointerException("path == null");
        }

        for (String storedPathStartsWith : STORED_PATHS_PREFIXES) {
            if (path.startsWith(storedPathStartsWith)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns all path prefixes of XMP paths if the related XMP metadata is
     * stored in the repository.
     *
     * @return path prefixes
     */
    public static Set<String> getPathPrefixes() {
        return new HashSet<String>(STORED_PATHS_PREFIXES);
    }

    private XmpToSaveInRepository() {}
}
