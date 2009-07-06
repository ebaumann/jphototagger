package de.elmar_baumann.imv.database.metadata.mapping;

import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.lib.generics.Pair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapping zwischen
 * {@link de.elmar_baumann.imv.database.metadata.Column}
 * und dem Start eines
 * {@link com.adobe.xmp.properties.XMPPropertyInfo#getPath()}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/19
 */
public final class XmpColumnXmpPathStartMapping {

    private static final Map<Column, String> XMP_PATH_START_OF_COLUMN =
            new HashMap<Column, String>();

    static {
        List<Pair<IPTCEntryMeta, Column>> pairs = IptcXmpMapping.getAllPairs();
        for (Pair<IPTCEntryMeta, Column> pair : pairs) {
            IPTCEntryMeta iptcEntryMeta = pair.getFirst();
            Column xmpColumn = pair.getSecond();
            String xmpPathStart =
                    IptcEntryXmpPathStartMapping.getXmpPathStartOfIptcEntryMeta(
                    iptcEntryMeta);
            XMP_PATH_START_OF_COLUMN.put(xmpColumn, xmpPathStart);
        }
    }

    /**
     * Liefert den Start des XMP-Pfads für eine XMP-Spalte.
     * 
     * @param  column  XMP-Spalte
     * @return Pfadstart oder null bei unzugeordneter Spalte
     */
    public static String getXmpPathStartOfColumn(Column column) {
        return XMP_PATH_START_OF_COLUMN.get(column);
    }

    private XmpColumnXmpPathStartMapping() {
    }
}
