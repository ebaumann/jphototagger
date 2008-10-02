package de.elmar_baumann.imagemetadataviewer.database.metadata.xmp;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>creator</code> der Tabelle <code>xmp_dc_creators</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public class ColumnXmpDcCreatorsCreator extends Column {

    private static ColumnXmpDcCreatorsCreator instance = new ColumnXmpDcCreatorsCreator();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpDcCreatorsCreator() {
        super(
            TableXmpDcCreators.getInstance(),
            "creator", // NOI18N
            DataType.string);

        setLength(32);
        setDescription(Bundle.getString("ColumnXmpDcCreatorsCreator.Description"));
    }
}
