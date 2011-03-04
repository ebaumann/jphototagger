package org.jphototagger.program.view.renderer;

import org.jphototagger.lib.componentutil.TableUtil;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.image.metadata.iptc.IptcEntry;
import org.jphototagger.program.resource.Translation;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Rendert Tabellen mit
 * {@link com.imagero.reader.iptc.IPTCEntryMeta}-Spaltenobjekten.
 *
 * @author Elmar Baumann
 */
public final class TableCellRendererIptc extends FormatterLabelMetadata implements TableCellRenderer {
    private static final Translation TRANSLATION = new Translation("IptcRecordDataSetNumberTranslations");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        JLabel cellLabel = new JLabel();
        IptcEntry iptcEntry = (IptcEntry) value;

        setDefaultCellColors(cellLabel, isSelected);

        String number = Integer.toString(iptcEntry.getRecordNumber()) + ":"
                        + Integer.toString(iptcEntry.getDataSetNumber());

        if (column == 0) {
            setContentFont(cellLabel);
            TableUtil.embedTableCellTextInHtml(table, row, cellLabel, number,
                                               AppLookAndFeel.TABLE_MAX_CHARS_ROW_HEADER,
                                               AppLookAndFeel.TABLE_ROW_HEADER_CSS);
        } else if (column == 1) {
            setHeaderFont(cellLabel);
            TableUtil.embedTableCellTextInHtml(table, row, cellLabel, TRANSLATION.translate(number, number),
                                               AppLookAndFeel.TABLE_MAX_CHARS_CELL, AppLookAndFeel.TABLE_CELL_CSS);
        } else {
            assert column < 3 : column;
            setContentFont(cellLabel);
            TableUtil.embedTableCellTextInHtml(table, row, cellLabel, iptcEntry.getData(),
                                               AppLookAndFeel.TABLE_MAX_CHARS_CELL, AppLookAndFeel.TABLE_CELL_CSS);
        }

        return cellLabel;
    }
}
