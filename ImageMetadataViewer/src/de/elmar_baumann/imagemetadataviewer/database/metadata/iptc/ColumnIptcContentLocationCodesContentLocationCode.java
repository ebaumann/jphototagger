package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>content_location_code</code> der Tabelle 
 * <code>iptc_content_location_codes</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class ColumnIptcContentLocationCodesContentLocationCode extends Column {

    private static ColumnIptcContentLocationCodesContentLocationCode instance =
        new ColumnIptcContentLocationCodesContentLocationCode();

    public static Column getInstance() {
        return instance;
    }

    private ColumnIptcContentLocationCodesContentLocationCode() {
        super(
            TableIptcContentLocationCodes.getInstance(),
            "content_location_code", // NOI18N
            DataType.string);

        setLength(3);
        setDescription(Bundle.getString("ColumnIptcContentLocationCodesContentLocationCode.Description"));
    }
}
