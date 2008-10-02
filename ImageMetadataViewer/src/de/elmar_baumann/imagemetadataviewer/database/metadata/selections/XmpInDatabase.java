package de.elmar_baumann.imagemetadataviewer.database.metadata.selections;

import java.util.Vector;

/**
 * Liefert, welche XMP-Metadaten in die Datenbank gespeichert werden.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/14
 */
public class XmpInDatabase {

    private static Vector<String> storedPathsStartsWith = new Vector<String>();
    private static XmpInDatabase instance = new XmpInDatabase();
    

    static {
        storedPathsStartsWith.add("Iptc4xmpCore:Location"); // NOI18N
        storedPathsStartsWith.add("Iptc4xmpCore:CountryCode"); // NOI18N
        storedPathsStartsWith.add("photoshop:Source"); // NOI18N
        storedPathsStartsWith.add("photoshop:Credit"); // NOI18N
        storedPathsStartsWith.add("photoshop:CaptionWriter"); // NOI18N
        storedPathsStartsWith.add("photoshop:AuthorsPosition"); // NOI18N
        storedPathsStartsWith.add("photoshop:Headline"); // NOI18N
        storedPathsStartsWith.add("photoshop:TransmissionReference"); // NOI18N
        storedPathsStartsWith.add("photoshop:Instructions"); // NOI18N
        storedPathsStartsWith.add("photoshop:Category"); // NOI18N
        storedPathsStartsWith.add("photoshop:SupplementalCategories"); // NOI18N
        storedPathsStartsWith.add("photoshop:City"); // NOI18N
        storedPathsStartsWith.add("photoshop:State"); // NOI18N
        storedPathsStartsWith.add("photoshop:Country"); // NOI18N
        storedPathsStartsWith.add("dc:title"); // NOI18N
        storedPathsStartsWith.add("dc:creator"); // NOI18N
        storedPathsStartsWith.add("dc:description"); // NOI18N
        storedPathsStartsWith.add("dc:rights"); // NOI18N
        storedPathsStartsWith.add("dc:subject"); // NOI18N
        storedPathsStartsWith.add("dc:subject"); // NOI18N
        storedPathsStartsWith.add("dc:subject"); // NOI18N
    }

    public static XmpInDatabase getInstance() {
        return instance;
    }

    /**
     * Liefert, ob die Metadaten eines XMP-Pfads in die Datenbank gespeichert
     * werden.
     * 
     * @param  path  Pfad
     * @return true, falls gespeichert
     */
    public boolean isInDatabase(String path) {
        for (String storedPathStartsWith : storedPathsStartsWith) {
            if (path.startsWith(storedPathStartsWith)) {
                return true;
            }
        }
        return false;
    }

    private XmpInDatabase() {
    }
}
