package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.app.AppLookAndFeel;
import de.elmar_baumann.imv.database.metadata.selections.ExifInDatabase;
import de.elmar_baumann.imv.image.metadata.exif.ExifFieldValueFormatter;
import de.elmar_baumann.imv.image.metadata.exif.entry.ExifGpsMetadata;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Translation;
import de.elmar_baumann.lib.componentutil.TableUtil;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/14
 */
public final class TableCellRendererExif extends FormatterLabelMetadata
        implements TableCellRenderer {

    private static final Translation TRANSLATION =
            new Translation("ExifTagIdTagNameTranslations"); // NOI18N

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

        if (value instanceof IdfEntryProxy) {
            IdfEntryProxy ifdEntry = (IdfEntryProxy) value;

            setIsStoredInDatabaseColor(cellLabel, ifdEntry);

            if (column == 0) {
                String translated = TRANSLATION.translate(
                        Integer.toString(ifdEntry.getTag()),
                        ifdEntry.getName());
                TableUtil.embedTableCellTextInHtml(table, row, cellLabel,
                        translated.trim(),
                        AppLookAndFeel.TABLE_MAX_CHARS_ROW_HEADER,
                        AppLookAndFeel.TABLE_CSS_ROW_HEADER);
            } else {
                TableUtil.embedTableCellTextInHtml(table, row, cellLabel,
                        ExifFieldValueFormatter.format(ifdEntry),
                        AppLookAndFeel.TABLE_MAX_CHARS_CELL,
                        AppLookAndFeel.TABLE_CSS_CELL);
            }
        } else if (value instanceof ExifGpsMetadata) {
            if (column == 0) {
                cellLabel.setText(Bundle.getString(
                        "TableCellRendererExif.Column.ShowLocationIn"));
            }
        } else if (value instanceof Component) {
            return (Component) value;
        } else {
            TableUtil.embedTableCellTextInHtml(table, row, cellLabel,
                    value.toString(),
                    AppLookAndFeel.TABLE_MAX_CHARS_CELL,
                    AppLookAndFeel.TABLE_CSS_CELL);
        }
        return cellLabel;
    }

    private void setIsStoredInDatabaseColor(JLabel cellLabel,
            IdfEntryProxy ifdEntry) {
        if (ExifInDatabase.isInDatabase(ifdEntry.getTag())) {
            setIsStoredInDatabaseColors(cellLabel);
        }
    }
}
