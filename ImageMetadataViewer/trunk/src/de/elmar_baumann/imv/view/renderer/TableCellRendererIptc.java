package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.app.AppLookAndFeel;
import de.elmar_baumann.imv.image.metadata.iptc.IptcEntry;
import de.elmar_baumann.imv.resource.Translation;
import de.elmar_baumann.lib.componentutil.TableUtil;
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
public final class TableCellRendererIptc extends TableCellRendererMetadata
        implements TableCellRenderer {

    private static final Translation translation = new Translation(
            "IptcRecordDataSetNumberTranslations"); // NOI18N

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
            TableUtil.embedTableCellTextInHtml(table, row, cellLabel,
                    number,
                    AppLookAndFeel.TABLE_MAX_CHARS_ROW_HEADER,
                    AppLookAndFeel.TABLE_CSS_ROW_HEADER);
        } else if (column == 1) {
            setHeaderFont(cellLabel);
            TableUtil.embedTableCellTextInHtml(table, row, cellLabel,
                    translation.translate(number),
                    AppLookAndFeel.TABLE_MAX_CHARS_CELL,
                    AppLookAndFeel.TABLE_CSS_CELL);
        } else {
            assert column < 3 : column;
            setContentFont(cellLabel);
            TableUtil.embedTableCellTextInHtml(table, row, cellLabel,
                    iptcEntry.getData(),
                    AppLookAndFeel.TABLE_MAX_CHARS_CELL,
                    AppLookAndFeel.TABLE_CSS_CELL);
        }
        return cellLabel;
    }
}
