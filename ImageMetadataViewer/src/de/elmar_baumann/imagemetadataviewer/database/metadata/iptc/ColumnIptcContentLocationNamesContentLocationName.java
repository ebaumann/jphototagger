package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>content_location_name</code> der Tabelle
 * <code>iptc_content_location_names</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class ColumnIptcContentLocationNamesContentLocationName extends Column {

    private static ColumnIptcContentLocationNamesContentLocationName instance =
        new ColumnIptcContentLocationNamesContentLocationName();

    public static Column getInstance() {
        return instance;
    }

    private ColumnIptcContentLocationNamesContentLocationName() {
        super(
            TableIptcContentLocationNames.getInstance(),
            "content_location_name", // NOI18N
            DataType.string);

        setLength(64);
        setDescription(Bundle.getString("ColumnIptcContentLocationNamesContentLocationName.Description"));
    }
}
