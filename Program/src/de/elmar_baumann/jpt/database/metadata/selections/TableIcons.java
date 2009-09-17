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

import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.database.metadata.Table;
import de.elmar_baumann.jpt.database.metadata.collections.TableCollectionNames;
import de.elmar_baumann.jpt.database.metadata.collections.TableCollections;
import de.elmar_baumann.jpt.database.metadata.exif.TableExif;
import de.elmar_baumann.jpt.database.metadata.file.TableFiles;
import de.elmar_baumann.jpt.database.metadata.savedsearches.TableSavedSearches;
import de.elmar_baumann.jpt.database.metadata.xmp.TableXmp;
import de.elmar_baumann.jpt.database.metadata.xmp.TableXmpDcSubjects;
import de.elmar_baumann.jpt.database.metadata.xmp.TableXmpPhotoshopSupplementalCategories;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;

/**
 * Liefert Icons für Tabellen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-13
 */
public final class TableIcons {

    private static final Icon ICON_UNDEFINED = AppLookAndFeel.getIcon("icon_table_undefined.png"); // NOI18N
    private static final Icon ICON_FILES = AppLookAndFeel.getIcon("icon_file.png"); // NOI18N
    private static final Icon ICON_EXIF = AppLookAndFeel.getIcon("icon_exif.png"); // NOI18N
    private static final Icon ICON_XMP = AppLookAndFeel.getIcon("icon_xmp.png"); // NOI18N
    private static final Icon ICON_IMAGE_COLLECTION = AppLookAndFeel.getIcon("icon_imagecollection.png"); // NOI18N
    private static final Icon ICON_SAVED_SEARCH = AppLookAndFeel.getIcon("icon_search.png"); // NOI18N
    private static final Map<Table, Icon> ICON_OF_TABLE = new HashMap<Table, Icon>();
    

    static {
        ICON_OF_TABLE.put(TableExif.INSTANCE, ICON_EXIF);
        ICON_OF_TABLE.put(TableFiles.INSTANCE, ICON_FILES);
        ICON_OF_TABLE.put(TableXmp.INSTANCE, ICON_XMP);
        ICON_OF_TABLE.put(TableXmpDcSubjects.INSTANCE, ICON_XMP);
        ICON_OF_TABLE.put(TableXmpPhotoshopSupplementalCategories.INSTANCE, ICON_XMP);
        ICON_OF_TABLE.put(TableCollections.INSTANCE, ICON_IMAGE_COLLECTION);
        ICON_OF_TABLE.put(TableCollectionNames.INSTANCE, ICON_IMAGE_COLLECTION);
        ICON_OF_TABLE.put(TableSavedSearches.INSTANCE, ICON_SAVED_SEARCH);
    }

    /**
     * Liefert das Icon für eine Tabelle.
     * 
     * @param  table Tabelle
     * @return Icon der Tabelle oder ein Icon für eine undefinierte Tabelle
     *         (Standard-Tabellenicon)
     */
    public static Icon getIcon(Table table) {
        Icon icon = ICON_OF_TABLE.get(table);
        return icon == null ? ICON_UNDEFINED : icon;
    }

    private TableIcons() {
    }
}
