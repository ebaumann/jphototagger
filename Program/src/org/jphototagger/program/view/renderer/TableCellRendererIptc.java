package org.jphototagger.program.view.renderer;

import javax.swing.table.TableModel;
import org.jphototagger.lib.componentutil.TableUtil;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.image.metadata.iptc.IptcEntry;
import org.jphototagger.program.resource.Translation;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableStringConverter;
import org.jphototagger.lib.util.StringUtil;

/**
 * Rendert Tabellen mit
 * {@link com.imagero.reader.iptc.IPTCEntryMeta}-Spaltenobjekten.
 *
 * @author Elmar Baumann
 */
public final class TableCellRendererIptc extends FormatterLabelMetadata implements TableCellRenderer {
    private static final Translation TRANSLATION = new Translation("IptcRecordDataSetNumberTranslations");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel cellLabel = new JLabel();
        IptcEntry iptcEntry = (IptcEntry) value;

        setDefaultCellColors(cellLabel, isSelected);

        String entryNumber = getIptcEntryNumber(iptcEntry);

        if (column == 0) {
            setContentFont(cellLabel);
            TableUtil.embedTableCellTextInHtml(table, row, cellLabel, entryNumber,
                                               AppLookAndFeel.TABLE_MAX_CHARS_ROW_HEADER,
                                               AppLookAndFeel.TABLE_ROW_HEADER_CSS);
        } else if (column == 1) {
            setHeaderFont(cellLabel);
            TableUtil.embedTableCellTextInHtml(table, row, cellLabel, TRANSLATION.translate(entryNumber, entryNumber),
                                               AppLookAndFeel.TABLE_MAX_CHARS_CELL, AppLookAndFeel.TABLE_CELL_CSS);
        } else {
            setContentFont(cellLabel);
            TableUtil.embedTableCellTextInHtml(table, row, cellLabel, iptcEntry.getData(),
                                               AppLookAndFeel.TABLE_MAX_CHARS_CELL, AppLookAndFeel.TABLE_CELL_CSS);
        }

        return cellLabel;
    }

    private static String getIptcEntryNumber(IptcEntry iptcEntry) {
        return Integer.toString(iptcEntry.getRecordNumber()) + ":" + Integer.toString(iptcEntry.getDataSetNumber());
    }

    public static TableStringConverter createTableStringConverter() {
        return new IptcTableStringConverter();
    }

    private static class IptcTableStringConverter extends TableStringConverter {

        @Override
        public String toString(TableModel model, int row, int column) {
            Object value = model.getValueAt(row, column);

            if (value instanceof IptcEntry) {
                IptcEntry iptcEntry = (IptcEntry) value;
                String entryNumber = getIptcEntryNumber(iptcEntry);

                return column == 0
                        ? entryNumber
                        : column == 1
                        ? TRANSLATION.translate(entryNumber, entryNumber)
                        : iptcEntry.getData();
            } else {
                return StringUtil.toStringNullToEmptyString(value);
            }
        }

    }
}
