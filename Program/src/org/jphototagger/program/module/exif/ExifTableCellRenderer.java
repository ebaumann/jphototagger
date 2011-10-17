package org.jphototagger.program.module.exif;

import java.awt.Component;
import java.util.Comparator;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableStringConverter;

import org.openide.util.Lookup;

import org.jphototagger.api.branding.TableLookAndFeel;
import org.jphototagger.domain.metadata.exif.ExifTag;
import org.jphototagger.lib.swing.util.TableUtil;
import org.jphototagger.lib.util.StringUtil;

/**
 * @author Elmar Baumann
 */
public final class ExifTableCellRenderer implements TableCellRenderer {

    private final TableLookAndFeel lookAndFeel = Lookup.getDefault().lookup(TableLookAndFeel.class);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (lookAndFeel == null) {
            return new JLabel(StringUtil.toStringNullToEmptyString(value));
        }

        JLabel cellLabel = new JLabel();
        lookAndFeel.setTableCellColor(cellLabel, isSelected);
        if (column == 0) {
            lookAndFeel.setTableRowHeaderFont(cellLabel);
        } else {
            lookAndFeel.setTableCellFont(cellLabel);
        }

        boolean isRowHeader = column == 0;
        int maxChars = isRowHeader ? lookAndFeel.getRowHeaderMaxChars() : lookAndFeel.getCellMaxChars();
        String css = isRowHeader ? lookAndFeel.getRowHeaderCss() : lookAndFeel.getCellCss();
        if (value instanceof ExifTag) {
            ExifTag exifTag = (ExifTag) value;

            if (column == 0) {
                String displayName = exifTag.getDisplayName();
                TableUtil.embedTableCellTextInHtml(table, row, cellLabel, displayName, maxChars, css);
            } else {
                String displayValue = exifTag.getDisplayValue();
                TableUtil.embedTableCellTextInHtml(table, row, cellLabel, displayValue, maxChars, css);
            }
        } else if (value instanceof Component) {
            return (Component) value;
        }

        return cellLabel;
    }

    public static TableStringConverter createTableStringConverter() {
        return new ExifTableStringConverter();
    }

    public static Comparator<?> createColumn0Comparator() {
        return new Column0Comparator();
    }

    public static Comparator<?> createColumn1Comparator() {
        return new Column1Comparator();
    }

    private static class Column0Comparator implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            if (o1 instanceof ExifTag && o2 instanceof ExifTag) {
                ExifTag exifTag1 = (ExifTag) o1;
                ExifTag exifTag2 = (ExifTag) o2;
                String displayName1 = exifTag1.getDisplayName();
                String displayName2 = exifTag2.getDisplayName();

                return displayName1.compareToIgnoreCase(displayName2);
            } else {
                return 0;
            }
        }
    }

    private static class Column1Comparator implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            if (o1 instanceof ExifTag && o2 instanceof ExifTag) {
                ExifTag exifTag1 = (ExifTag) o1;
                ExifTag exifTag2 = (ExifTag) o2;
                String displayValue1 = exifTag1.getDisplayValue();
                String displayValue2 = exifTag2.getDisplayValue();

                return displayValue1.compareToIgnoreCase(displayValue2);
            } else {
                return 0;
            }
        }
    }

    private static class ExifTableStringConverter extends TableStringConverter {

        @Override
        public String toString(TableModel model, int row, int column) {
            Object value = model.getValueAt(row, column);

            if (value instanceof ExifTag) {
                ExifTag exifTag = (ExifTag) value;

                return column == 0
                        ? exifTag.getDisplayName()
                        : exifTag.getDisplayValue();
            } else {
                return StringUtil.toStringNullToEmptyString(value);
            }
        }
    }
}
