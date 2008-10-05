package de.elmar_baumann.imagemetadataviewer.database.metadata.mapping;

import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.lib.template.Pair;
import java.util.HashMap;
import java.util.List;

/**
 * Mapping zwischen
 * {@link de.elmar_baumann.imagemetadataviewer.database.metadata.Column}
 * und dem Start eines
 * {@link com.adobe.xmp.properties.XMPPropertyInfo#getPath()}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/19
 */
public class XmpColumnXmpPathStartMapping {

    private static HashMap<Column, String> xmpPathStartOfColumn = new HashMap<Column, String>();
    private static XmpColumnXmpPathStartMapping instance = new XmpColumnXmpPathStartMapping();
    

    static {
        List<Pair<IPTCEntryMeta, Column>> pairs = IptcXmpMapping.getInstance().getAllPairs();
        IptcEntryXmpPathStartMapping iptcEntryXmpPathMapping = IptcEntryXmpPathStartMapping.getInstance();
        for (Pair<IPTCEntryMeta, Column> pair : pairs) {
            IPTCEntryMeta iptcEntryMeta = pair.getFirst();
            Column xmpColumn = pair.getSecond();
            String xmpPathStart =
                iptcEntryXmpPathMapping.getXmpPathStartOfIptcEntryMeta(iptcEntryMeta);
            xmpPathStartOfColumn.put(xmpColumn, xmpPathStart);
        }
    }

    /**
     * Liefert den Start des XMP-Pfads für eine XMP-Spalte.
     * 
     * @param  column  XMP-Spalte
     * @return Pfadstart oder null bei unzugeordneter Spalte
     */
    public String getXmpPathStartOfColumn(Column column) {
        return xmpPathStartOfColumn.get(column);
    }

    /**
     * Liefert die einzige Klasseninstanz.
     * 
     * @return Instanz
     */
    public static XmpColumnXmpPathStartMapping getInstance() {
        return instance;
    }

    private XmpColumnXmpPathStartMapping() {
    }
}
