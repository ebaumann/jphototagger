package org.jphototagger.program.view.renderer;

import javax.swing.table.TableModel;
import org.jphototagger.lib.componentutil.TableUtil;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.database.metadata.selections.ExifInDatabase;
import org.jphototagger.program.image.metadata.exif.ExifMetadata.IfdType;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import org.jphototagger.program.image.metadata.exif.ExifTagValueFormatter;
import org.jphototagger.program.image.metadata.exif.tag.ExifGpsMetadata;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.resource.Translation;
import java.awt.Component;
import java.util.Comparator;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableStringConverter;
import org.jphototagger.lib.util.StringUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class TableCellRendererExif extends FormatterLabelMetadata implements TableCellRenderer {
    private static final Translation TRANSLATION = new Translation("ExifTagIdTagNameTranslations");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel cellLabel = new JLabel();

        setDefaultCellColors(cellLabel, isSelected);

        if (column == 0) {
            setHeaderFont(cellLabel);
        } else {
            setContentFont(cellLabel);
        }

        if (value instanceof ExifTag) {
            ExifTag exifTag = (ExifTag) value;

            setIsMakerNoteTagColor(cellLabel, exifTag, isSelected);
            setIsStoredInDatabaseColor(cellLabel, exifTag, isSelected);    // override maker note (more important)

            if (column == 0) {
                String tagName = getTagName(exifTag);

                TableUtil.embedTableCellTextInHtml(table, row, cellLabel, tagName.trim(),
                                                   AppLookAndFeel.TABLE_MAX_CHARS_ROW_HEADER,
                                                   AppLookAndFeel.TABLE_ROW_HEADER_CSS);
            } else {
                TableUtil.embedTableCellTextInHtml(table, row, cellLabel, ExifTagValueFormatter.format(exifTag),
                                                   AppLookAndFeel.TABLE_MAX_CHARS_CELL, AppLookAndFeel.TABLE_CELL_CSS);
            }
        } else if (value instanceof ExifGpsMetadata) {
            if (column == 0) {
                cellLabel.setText(JptBundle.INSTANCE.getString("TableCellRendererExif.Column.ShowLocationIn"));
            }
        } else if (value instanceof Component) {
            return (Component) value;
        } else {
            TableUtil.embedTableCellTextInHtml(table, row, cellLabel, value.toString(),
                                               AppLookAndFeel.TABLE_MAX_CHARS_CELL, AppLookAndFeel.TABLE_CELL_CSS);
        }

        return cellLabel;
    }

    private static String getTagName(ExifTag exifTag) {
        String tagName = exifTag.name();

        if (exifTag.ifdType().equals(IfdType.MAKER_NOTE)) {
            return tagName;
        }

        if (exifTag.id().value() >= ExifTag.Id.MAKER_NOTE.value()) {
            return tagName;
        }

        return TRANSLATION.translate(Integer.toString(exifTag.idValue()), tagName);
    }

    private void setIsMakerNoteTagColor(JLabel cellLabel, ExifTag exifTag, boolean isSelected) {
        if (exifTag.ifdType().equals(IfdType.MAKER_NOTE)) {
            setIsExifMakerNoteColors(cellLabel, isSelected);
        }
    }

    private void setIsStoredInDatabaseColor(JLabel cellLabel, ExifTag exifTag, boolean isSelected) {
        if (ExifInDatabase.isInDatabase(exifTag.ifdType(), exifTag.id())) {
            setIsStoredInDatabaseColors(cellLabel, isSelected);
        }
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
                String o1String = getTagName(exifTag1).trim();
                String o2String = getTagName(exifTag2).trim();

                return o1String.compareToIgnoreCase(o2String);
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
                String o1String = ExifTagValueFormatter.format(exifTag1);
                String o2String = ExifTagValueFormatter.format(exifTag2);

                return o1String.compareToIgnoreCase(o2String);
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
                        ? getTagName(exifTag).trim()
                        : ExifTagValueFormatter.format(exifTag);
            } else {
                return StringUtil.toStringNullToEmptyString(value);
            }
        }
    }
}
