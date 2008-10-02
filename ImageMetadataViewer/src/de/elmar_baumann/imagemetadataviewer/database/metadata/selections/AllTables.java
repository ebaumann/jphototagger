package de.elmar_baumann.imagemetadataviewer.database.metadata.selections;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Table;
import de.elmar_baumann.imagemetadataviewer.database.metadata.collections.TableCollectionNames;
import de.elmar_baumann.imagemetadataviewer.database.metadata.collections.TableCollections;
import de.elmar_baumann.imagemetadataviewer.database.metadata.exif.TableExif;
import de.elmar_baumann.imagemetadataviewer.database.metadata.file.TableFiles;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.TableIptc;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.TableIptcByLines;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.TableIptcByLinesTitles;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.TableIptcContentLocationCodes;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.TableIptcContentLocationNames;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.TableIptcKeywords;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.TableIptcSupplementalCategories;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.TableIptcWritersEditors;
import de.elmar_baumann.imagemetadataviewer.database.metadata.savedsearches.TableSavedSearches;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.TableXmp;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.TableXmpDcCreators;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.TableXmpDcSubjects;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.TableXmpPhotoshopSupplementalCategories;
import java.util.Vector;

/**
 * Alle Tabellen der Anwendung.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/18
 */
public class AllTables {

    private static Vector<Table> allTables = new Vector<Table>();    
    
    // TODO PERMANENT: Neue Tabellen eintragen, Reihenfolge NIE verändern, da Wechselwirkung mit
    // de.elmar_baumann.imagemetadataviewer.database.metadata.ColumnIds.initColumnOfIdMap()

    static {
        allTables.add(TableExif.getInstance());
        allTables.add(TableFiles.getInstance());
        allTables.add(TableIptc.getInstance());
        allTables.add(TableIptcByLines.getInstance());
        allTables.add(TableIptcByLinesTitles.getInstance());
        allTables.add(TableIptcContentLocationCodes.getInstance());
        allTables.add(TableIptcContentLocationNames.getInstance());
        allTables.add(TableIptcKeywords.getInstance());
        allTables.add(TableIptcSupplementalCategories.getInstance());
        allTables.add(TableIptcWritersEditors.getInstance());
        allTables.add(TableXmp.getInstance());
        allTables.add(TableXmpDcCreators.getInstance());
        allTables.add(TableXmpDcSubjects.getInstance());
        allTables.add(TableXmpPhotoshopSupplementalCategories.getInstance());
        allTables.add(TableCollections.getInstance());
        allTables.add(TableCollectionNames.getInstance());
        allTables.add(TableSavedSearches.getInstance());
    }

    public static Vector<Table> get() {
        return allTables;
    }
}
