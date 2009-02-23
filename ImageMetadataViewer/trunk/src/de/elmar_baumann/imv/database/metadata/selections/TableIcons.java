package de.elmar_baumann.imv.database.metadata.selections;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.database.metadata.Table;
import de.elmar_baumann.imv.database.metadata.collections.TableCollectionNames;
import de.elmar_baumann.imv.database.metadata.collections.TableCollections;
import de.elmar_baumann.imv.database.metadata.exif.TableExif;
import de.elmar_baumann.imv.database.metadata.file.TableFiles;
import de.elmar_baumann.imv.database.metadata.savedsearches.TableSavedSearches;
import de.elmar_baumann.imv.database.metadata.xmp.TableXmp;
import de.elmar_baumann.imv.database.metadata.xmp.TableXmpDcSubjects;
import de.elmar_baumann.imv.database.metadata.xmp.TableXmpPhotoshopSupplementalCategories;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;

/**
 * Liefert Icons für Tabellen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/13
 */
public final class TableIcons {

    private static final Icon iconUndefined = AppIcons.getIcon("icon_table_undefined.png"); // NOI18N
    private static final Icon iconFiles = AppIcons.getIcon("icon_file.png"); // NOI18N
    private static final Icon iconExif = AppIcons.getIcon("icon_exif.png"); // NOI18N
    private static final Icon iconXmp = AppIcons.getIcon("icon_xmp.png"); // NOI18N
    private static final Icon iconImageCollection = AppIcons.getIcon("icon_imagecollection.png"); // NOI18N
    private static final Icon iconSavedSearch = AppIcons.getIcon("icon_search.png"); // NOI18N
    private static final Map<Table, Icon> iconOfTable = new HashMap<Table, Icon>();
    

    static {
        iconOfTable.put(TableExif.INSTANCE, iconExif);
        iconOfTable.put(TableFiles.INSTANCE, iconFiles);
        iconOfTable.put(TableXmp.INSTANCE, iconXmp);
        iconOfTable.put(TableXmpDcSubjects.INSTANCE, iconXmp);
        iconOfTable.put(TableXmpPhotoshopSupplementalCategories.INSTANCE, iconXmp);
        iconOfTable.put(TableCollections.INSTANCE, iconImageCollection);
        iconOfTable.put(TableCollectionNames.INSTANCE, iconImageCollection);
        iconOfTable.put(TableSavedSearches.INSTANCE, iconSavedSearch);
    }

    /**
     * Liefert das Icon für eine Tabelle.
     * 
     * @param  table Tabelle
     * @return Icon der Tabelle oder ein Icon für eine undefinierte Tabelle
     *         (Standard-Tabellenicon)
     */
    public static Icon getIcon(Table table) {
        Icon icon = iconOfTable.get(table);
        return icon == null ? iconUndefined : icon;
    }

    private TableIcons() {
    }
}
