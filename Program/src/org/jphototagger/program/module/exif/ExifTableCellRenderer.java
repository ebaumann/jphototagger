package org.jphototagger.program.module.exif;

import java.awt.Component;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableStringConverter;

import org.jphototagger.exif.ExifIfdType;
import org.jphototagger.exif.ExifToSaveInRepository;
import org.jphototagger.exif.ExifTag;
import org.jphototagger.exif.ExifTag.Id;
import org.jphototagger.exif.ExifTagValueFormatter;
import org.jphototagger.exif.tag.ExifGpsMetadata;
import org.jphototagger.lib.componentutil.TableUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.lib.util.Translation;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.program.app.ui.FormatterLabelMetadata;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ExifTableCellRenderer extends FormatterLabelMetadata implements TableCellRenderer {

    public static final Translation TAG_ID_TAGNAME_TRANSLATION = new Translation(ExifTableCellRenderer.class, "ExifTagIdTagNameTranslations");
    public static final Translation TAGNAME_TRANSLATION = new Translation(ExifTableCellRenderer.class, "ExifTagNameTranslations");
    private static final Logger LOGGER = Logger.getLogger(ExifTableCellRenderer.class.getName());

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
            setIsStoredInRepositoryColor(cellLabel, exifTag, isSelected);    // override maker note (more important)

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
                cellLabel.setText(Bundle.getString(ExifTableCellRenderer.class, "ExifTableCellRenderer.Column.ShowLocationIn"));
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
        String tagName = exifTag.getName();
        ExifIfdType ifdType = exifTag.getIfdType();
        boolean isMakerNoteIfd = ifdType.equals(ExifIfdType.MAKER_NOTE);

        if (isMakerNoteIfd) {
            return tagName;
        }

        Id exifTagId = exifTag.convertTagIdToEnumId();
        int tagId = exifTagId.getTagId();
        int makerNoteTagId = ExifTag.Id.MAKER_NOTE.getTagId();
        boolean isMakerNoteTag = tagId >= makerNoteTagId;

        if (isMakerNoteTag) {
            boolean canTranslate = TAGNAME_TRANSLATION.canTranslate(tagName);

            if (!canTranslate) {
                LOGGER.log(Level.INFO, "EXIF tag name suggested for translation: ''{0}''", tagName);
            }

            return canTranslate
                    ? TAGNAME_TRANSLATION.translate(tagName)
                    : tagName;
        }

        return TAG_ID_TAGNAME_TRANSLATION.translate(Integer.toString(exifTag.getTagId()), tagName);
    }

    private void setIsMakerNoteTagColor(JLabel cellLabel, ExifTag exifTag, boolean isSelected) {
        if (exifTag.getIfdType().equals(ExifIfdType.MAKER_NOTE)) {
            setIsExifMakerNoteColors(cellLabel, isSelected);
        }
    }

    private void setIsStoredInRepositoryColor(JLabel cellLabel, ExifTag exifTag, boolean isSelected) {
        ExifIfdType ifdType = exifTag.getIfdType();
        Id ExifTagId = exifTag.convertTagIdToEnumId();

        if (ExifToSaveInRepository.isSaveInRepository(ifdType, ExifTagId)) {
            setIsStoredInRepositoryColors(cellLabel, isSelected);
            cellLabel.setToolTipText(Bundle.getString(ExifTableCellRenderer.class, "ExifTableCellRenderer.ToolTipText.CellLabelStoredInRepository"));
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
