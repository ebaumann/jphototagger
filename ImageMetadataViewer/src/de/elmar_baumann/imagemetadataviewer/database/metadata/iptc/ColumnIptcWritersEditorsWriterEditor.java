package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>writer_editor</code> der Tabelle <code>iptc_writers_editors</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class ColumnIptcWritersEditorsWriterEditor extends Column {

    private static ColumnIptcWritersEditorsWriterEditor instance = new ColumnIptcWritersEditorsWriterEditor();

    public static Column getInstance() {
        return instance;
    }

    private ColumnIptcWritersEditorsWriterEditor() {
        super(
            TableIptcWritersEditors.getInstance(),
            "writer_editor", // NOI18N
            DataType.string);

        setLength(32);
        setDescription(Bundle.getString("ColumnIptcWritersEditorsWriterEditor.Description"));
    }
}
