package org.jphototagger.program.view.renderer;

import javax.swing.table.TableModel;
import org.jphototagger.lib.componentutil.TableUtil;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.image.metadata.iptc.IptcEntry;
import org.jphototagger.program.resource.Translation;

import java.awt.Component;
import java.util.Comparator;

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

        if (value == null) {
            return cellLabel;
        }

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
        if (iptcEntry == null) {
            return "";
        }

        int recordNumber = iptcEntry.getRecordNumber();
        int dataSetNumber = iptcEntry.getDataSetNumber();
        
        return Integer.toString(recordNumber) + ":" + Integer.toString(dataSetNumber);
    }

    public static Comparator<?> createColumn0Comparator() {
        return new Column0Comparator();
    }

    public static Comparator<?> createColumn1Comparator() {
        return new Column1Comparator();
    }

    public static Comparator<?> createColumn2Comparator() {
        return new Column2Comparator();
    }

    private static class Column0Comparator implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            if (o1 instanceof IptcEntry && o2 instanceof IptcEntry) {
                IptcEntry iptcEntry1 = (IptcEntry) o1;
                IptcEntry iptcEntry2 = (IptcEntry) o2;
                String o1String = getIptcEntryNumber(iptcEntry1);
                String o2String = getIptcEntryNumber(iptcEntry2);

                return o1String.compareToIgnoreCase(o2String);
            } else {
                return 0;
            }
        }
    }

    private static class Column1Comparator implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            if (o1 instanceof IptcEntry && o2 instanceof IptcEntry) {
                IptcEntry iptcEntry1 = (IptcEntry) o1;
                IptcEntry iptcEntry2 = (IptcEntry) o2;
                String entryNumber1 = getIptcEntryNumber(iptcEntry1);
                String entryNumber2 = getIptcEntryNumber(iptcEntry2);
                String o1String = TRANSLATION.translate(entryNumber1, entryNumber1);
                String o2String = TRANSLATION.translate(entryNumber2, entryNumber2);

                return o1String.compareToIgnoreCase(o2String);
            } else {
                return 0;
            }
        }
    }

    private static class Column2Comparator implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            if (o1 instanceof IptcEntry && o2 instanceof IptcEntry) {
                IptcEntry iptcEntry1 = (IptcEntry) o1;
                IptcEntry iptcEntry2 = (IptcEntry) o2;
                String o1String = iptcEntry1.getData();
                String o2String = iptcEntry2.getData();

                return o1String.compareToIgnoreCase(o2String);
            } else {
                return 0;
            }
        }
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
