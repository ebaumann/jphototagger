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
package de.elmar_baumann.jpt.database.metadata.mapping;

import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.lib.generics.Pair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapping zwischen
 * {@link de.elmar_baumann.jpt.database.metadata.Column}
 * und dem Start eines
 * {@link com.adobe.xmp.properties.XMPPropertyInfo#getPath()}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-19
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
