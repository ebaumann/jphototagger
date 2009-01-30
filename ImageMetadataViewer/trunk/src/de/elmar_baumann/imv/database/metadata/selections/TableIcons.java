package de.elmar_baumann.imv.database.metadata.selections;

import de.elmar_baumann.imv.AppSettings;
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

    private static final Icon iconUndefined = AppSettings.getIcon("icon_table_undefined.png"); // NOI18N
    private static final Icon iconFiles = AppSettings.getIcon("icon_file.png"); // NOI18N
    private static final Icon iconExif = AppSettings.getIcon("icon_exif.png"); // NOI18N
    private static final Icon iconXmp = AppSettings.getIcon("icon_xmp.png"); // NOI18N
    private static final Icon iconImageCollection = AppSettings.getIcon("icon_imagecollection.png"); // NOI18N
    private static final Icon iconSavedSearch = AppSettings.getIcon("icon_search.png"); // NOI18N
    private static final Map<Table, Icon> iconOfTable = new HashMap<Table, Icon>();
    

    static {
        iconOfTable.put(TableExif.getInstance(), iconExif);
        iconOfTable.put(TableFiles.getInstance(), iconFiles);
        iconOfTable.put(TableXmp.getInstance(), iconXmp);
        iconOfTable.put(TableXmpDcSubjects.getInstance(), iconXmp);
        iconOfTable.put(TableXmpPhotoshopSupplementalCategories.getInstance(), iconXmp);
        iconOfTable.put(TableCollections.getInstance(), iconImageCollection);
        iconOfTable.put(TableCollectionNames.getInstance(), iconImageCollection);
        iconOfTable.put(TableSavedSearches.getInstance(), iconSavedSearch);
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
