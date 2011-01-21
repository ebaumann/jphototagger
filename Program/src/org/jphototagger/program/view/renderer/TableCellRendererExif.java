package org.jphototagger.program.view.renderer;

import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.database.metadata.selections.ExifInDatabase;
import org.jphototagger.program.image.metadata.exif.ExifMetadata.IfdType;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import org.jphototagger.program.image.metadata.exif.ExifTagValueFormatter;
import org.jphototagger.program.image.metadata.exif.tag.ExifGpsMetadata;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.resource.Translation;
import org.jphototagger.lib.componentutil.TableUtil;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class TableCellRendererExif extends FormatterLabelMetadata
        implements TableCellRenderer {
    private static final Translation TRANSLATION =
        new Translation("ExifTagIdTagNameTranslations");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        assert column < 2 : column;

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
                String translated = getTagName(exifTag);

                TableUtil.embedTableCellTextInHtml(
                    table, row, cellLabel, translated.trim(),
                    AppLookAndFeel.TABLE_MAX_CHARS_ROW_HEADER,
                    AppLookAndFeel.TABLE_ROW_HEADER_CSS);
            } else {
                TableUtil.embedTableCellTextInHtml(
                    table, row, cellLabel,
                    ExifTagValueFormatter.format(exifTag),
                    AppLookAndFeel.TABLE_MAX_CHARS_CELL,
                    AppLookAndFeel.TABLE_CELL_CSS);
            }
        } else if (value instanceof ExifGpsMetadata) {
            if (column == 0) {
                cellLabel.setText(
                    JptBundle.INSTANCE.getString(
                        "TableCellRendererExif.Column.ShowLocationIn"));
            }
        } else if (value instanceof Component) {
            return (Component) value;
        } else {
            TableUtil.embedTableCellTextInHtml(
                table, row, cellLabel, value.toString(),
                AppLookAndFeel.TABLE_MAX_CHARS_CELL,
                AppLookAndFeel.TABLE_CELL_CSS);
        }

        return cellLabel;
    }

    private String getTagName(ExifTag exifTag) {
        String tagName = exifTag.name();

        if (exifTag.ifdType().equals(IfdType.MAKER_NOTE)) {
            return tagName;
        }

        if (exifTag.id().value() >= ExifTag.Id.MAKER_NOTE.value()) {
            return tagName;
        }

        return TRANSLATION.translate(Integer.toString(exifTag.idValue()),
                                     tagName);
    }

    private void setIsMakerNoteTagColor(JLabel cellLabel, ExifTag exifTag,
            boolean isSelected) {
        if (exifTag.ifdType().equals(IfdType.MAKER_NOTE)) {
            setIsExifMakerNoteColors(cellLabel, isSelected);
        }
    }

    private void setIsStoredInDatabaseColor(JLabel cellLabel, ExifTag exifTag,
            boolean isSelected) {
        if (ExifInDatabase.isInDatabase(exifTag.ifdType(), exifTag.id())) {
            setIsStoredInDatabaseColors(cellLabel, isSelected);
        }
    }
}
