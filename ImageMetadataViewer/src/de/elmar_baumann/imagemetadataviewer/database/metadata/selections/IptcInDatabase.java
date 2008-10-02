package de.elmar_baumann.imagemetadataviewer.database.metadata.selections;

import com.imagero.reader.iptc.IPTCEntryMeta;
import java.util.Stack;
import java.util.Vector;

/**
 * Liefert, welche IPTC-Metadaten in die Datenbank gespeichert werden.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/14
 */
public class IptcInDatabase {

    private static Vector<IPTCEntryMeta> storedEntryMetas = new Stack<IPTCEntryMeta>();
    private static IptcInDatabase instance = new IptcInDatabase();
    

    static {
        storedEntryMetas.add(IPTCEntryMeta.BYLINE);
        storedEntryMetas.add(IPTCEntryMeta.BYLINE_TITLE);
        storedEntryMetas.add(IPTCEntryMeta.CAPTION_ABSTRACT);
        storedEntryMetas.add(IPTCEntryMeta.CATEGORY);
        storedEntryMetas.add(IPTCEntryMeta.CITY);
        storedEntryMetas.add(IPTCEntryMeta.CONTENT_LOCATION_CODE);
        storedEntryMetas.add(IPTCEntryMeta.CONTENT_LOCATION_NAME);
        storedEntryMetas.add(IPTCEntryMeta.COPYRIGHT_NOTICE);
        storedEntryMetas.add(IPTCEntryMeta.COUNTRY_PRIMARY_LOCATION_NAME);
        storedEntryMetas.add(IPTCEntryMeta.CREDIT);
        storedEntryMetas.add(IPTCEntryMeta.DATE_CREATED);
        storedEntryMetas.add(IPTCEntryMeta.HEADLINE);
        storedEntryMetas.add(IPTCEntryMeta.KEYWORDS);
        storedEntryMetas.add(IPTCEntryMeta.OBJECT_NAME);
        storedEntryMetas.add(IPTCEntryMeta.ORIGINAL_TRANSMISSION_REFERENCE);
        storedEntryMetas.add(IPTCEntryMeta.PROVINCE_STATE);
        storedEntryMetas.add(IPTCEntryMeta.SOURCE);
        storedEntryMetas.add(IPTCEntryMeta.SPECIAL_INSTRUCTIONS);
        storedEntryMetas.add(IPTCEntryMeta.SUPPLEMENTAL_CATEGORY);
        storedEntryMetas.add(IPTCEntryMeta.WRITER_EDITOR);
    }

    public static IptcInDatabase getInstance() {
        return instance;
    }

    private IptcInDatabase() {
    }

    /**
     * Liefert, ob die Metadaten eines XMP-Pfads in die Datenbank gespeichert
     * werden.
     * 
     * @param  iptcEntryMeta  Meta 
     * @return true, falls gespeichert
     */
    public boolean isInDatabase(IPTCEntryMeta iptcEntryMeta) {
        return storedEntryMetas.contains(iptcEntryMeta);
    }
}
