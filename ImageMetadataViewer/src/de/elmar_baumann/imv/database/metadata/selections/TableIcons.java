package de.elmar_baumann.imv.database.metadata.selections;

import de.elmar_baumann.imv.database.metadata.Table;
import de.elmar_baumann.imv.database.metadata.collections.TableCollectionNames;
import de.elmar_baumann.imv.database.metadata.collections.TableCollections;
import de.elmar_baumann.imv.database.metadata.exif.TableExif;
import de.elmar_baumann.imv.database.metadata.file.TableFiles;
import de.elmar_baumann.imv.database.metadata.savedsearches.TableSavedSearches;
import de.elmar_baumann.imv.database.metadata.xmp.TableXmp;
import de.elmar_baumann.imv.database.metadata.xmp.TableXmpDcSubjects;
import de.elmar_baumann.imv.database.metadata.xmp.TableXmpPhotoshopSupplementalCategories;
import de.elmar_baumann.lib.image.icon.IconUtil;
import java.util.HashMap;
import javax.swing.ImageIcon;

/**
 * Liefert Icons für Tabellen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/13
 */
public class TableIcons {

    private static final ImageIcon iconUndefined = IconUtil.getImageIcon("/de/elmar_baumann/imagemetadataviewer/resource/icon_table_undefined_small.png"); // NOI18N
    private static final ImageIcon iconFiles = IconUtil.getImageIcon("/de/elmar_baumann/imagemetadataviewer/resource/icon_files_small.png"); // NOI18N
    private static final ImageIcon iconExif = IconUtil.getImageIcon("/de/elmar_baumann/imagemetadataviewer/resource/icon_exif_small.png"); // NOI18N
    private static final ImageIcon iconXmp = IconUtil.getImageIcon("/de/elmar_baumann/imagemetadataviewer/resource/icon_xmp_small.png"); // NOI18N
    private static final ImageIcon iconImageCollection = IconUtil.getImageIcon("/de/elmar_baumann/imagemetadataviewer/resource/icon_image_collection_child.png"); // NOI18N
    private static final ImageIcon iconSavedSearch = IconUtil.getImageIcon("/de/elmar_baumann/imagemetadataviewer/resource/icon_saved_searches_child.png"); // NOI18N
    private static HashMap<Table, ImageIcon> iconOfTable = new HashMap<Table, ImageIcon>();
    private static TableIcons instance = new TableIcons();
    

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
     * Liefert die einzige Klasseninstanz.
     * 
     * @return Klasseninstanz
     */
    public static TableIcons getInstance() {
        return instance;
    }

    /**
     * Liefert das Icon für eine Tabelle.
     * 
     * @param  table Tabelle
     * @return Icon der Tabelle oder ein Icon für eine undefinierte Tabelle
     *         (Standard-Tabellenicon)
     */
    public ImageIcon getIcon(Table table) {
        ImageIcon icon = iconOfTable.get(table);
        return icon == null ? iconUndefined : icon;
    }

    private TableIcons() {
    }
}
