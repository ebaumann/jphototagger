package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>caption_abstract</code> der Tabelle <code>iptc</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class ColumnIptcCaptionAbstract extends Column {

    private static ColumnIptcCaptionAbstract instance = new ColumnIptcCaptionAbstract();

    public static Column getInstance() {
        return instance;
    }

    private ColumnIptcCaptionAbstract() {
        super(
            TableIptc.getInstance(),
            "caption_abstract", // NOI18N
            DataType.string);

        setLength(2000);
        setDescription(Bundle.getString("ColumnIptcCaptionAbstract.Description"));
    }
}
