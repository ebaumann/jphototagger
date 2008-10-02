package de.elmar_baumann.imagemetadataviewer.database.metadata.mapping;

import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.lib.template.Pair;
import java.util.HashMap;
import java.util.Vector;

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
        Vector<Pair<Column, Column>> pairs = IptcXmpMapping.getInstance().getAllPairs();
        IptcEntryMetaIptcColumnMapping iptcEntryMetaIptcColumnMapping = IptcEntryMetaIptcColumnMapping.getInstance();
        IptcEntryXmpPathStartMapping iptcEntryXmpPathMapping = IptcEntryXmpPathStartMapping.getInstance();
        for (Pair<Column, Column> pair : pairs) {
            Column iptcColumn = pair.getFirst();
            Column xmpColumn = pair.getSecond();
            IPTCEntryMeta iptcEntryMeta = iptcEntryMetaIptcColumnMapping.getEntryMetaOfColumn(iptcColumn);
            String xmpPathStart = iptcEntryXmpPathMapping.getXmpPathStartOfIptcEntryMeta(iptcEntryMeta);
            xmpPathStartOfColumn.put(xmpColumn, xmpPathStart);
        }
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

    /**
     * Liefert den Start des XMP-Pfads für eine XMP-Spalte.
     * 
     * @param  column  XMP-Spalte
     * @return Pfadstart oder null bei unzugeordneter Spalte
     */
    public String getXmpPathStartOfColumn(Column column) {
        return xmpPathStartOfColumn.get(column);
    }
}
