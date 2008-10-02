package de.elmar_baumann.imagemetadataviewer.view.renderer;

import de.elmar_baumann.imagemetadataviewer.database.metadata.selections.IptcInDatabase;
import de.elmar_baumann.imagemetadataviewer.image.metadata.iptc.IptcEntry;
import de.elmar_baumann.imagemetadataviewer.resource.Translation;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Rendert Tabellen mit
 * {@link com.imagero.reader.iptc.IPTCEntryMeta}-Spaltenobjekten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/14
 */
public class TableCellRendererIptc extends TableCellRendererMetadata
    implements TableCellRenderer {

    private static final Translation translation = new Translation("IptcRecordDataSetNumberTranslations"); // NOI18N
    private static final IptcInDatabase iptcInDatabase = IptcInDatabase.getInstance();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel cellLabel = new JLabel();
        IptcEntry iptcEntry = (IptcEntry) value;

        setDefaultCellColors(cellLabel, isSelected);
        setIsStoredInDatabaseColor(cellLabel, iptcEntry);

        String number =
            Integer.toString(iptcEntry.getRecordNumber()) + ":" + // NOI18N
            Integer.toString(iptcEntry.getDataSetNumber());
        if (column == 0) {
            setContentFont(cellLabel);
            cellLabel.setText(number);
        } else if (column == 1) {
            setHeaderFont(cellLabel);
            cellLabel.setText(paddingLeft + translation.translate(number));
        } else {
            assert column < 3 : column;
            setContentFont(cellLabel);
            cellLabel.setText(paddingLeft + iptcEntry.getData());
        }
        return cellLabel;
    }

    private void setIsStoredInDatabaseColor(JLabel cellLabel, IptcEntry iptcEntry) {
        if (iptcInDatabase.isInDatabase(iptcEntry.getEntry().getEntryMeta())) {
            setIsStoredInDatabaseColors(cellLabel);
        }
    }
}
