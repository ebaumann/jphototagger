package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.image.metadata.iptc.IptcEntry;
import de.elmar_baumann.imv.resource.Translation;
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

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel cellLabel = new JLabel();
        IptcEntry iptcEntry = (IptcEntry) value;

        setDefaultCellColors(cellLabel, isSelected);

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
}
