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
package de.elmar_baumann.jpt.database.metadata.xmp;

import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Collection of all XMP tables.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-08
 */
public final class XmpTables {

    private static final List<Table> XMP_TABLES = new ArrayList<Table>(2);

    static {
        XMP_TABLES.add(TableXmp.INSTANCE);
        XMP_TABLES.add(TableXmpDcSubjects.INSTANCE);
    }

    public static List<Table> get() {
        return new ArrayList<Table>(XMP_TABLES);
    }

    public static boolean isXmpColumn(Column column) {
        for (Table table : XMP_TABLES) {
            if (table.contains(column)) return true;
        }
        return false;
    }

    private XmpTables() {
    }
}
