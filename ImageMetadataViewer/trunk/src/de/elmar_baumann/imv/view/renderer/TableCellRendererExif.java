package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.database.metadata.selections.ExifInDatabase;
import de.elmar_baumann.imv.image.metadata.exif.ExifFieldValueFormatter;
import de.elmar_baumann.imv.image.metadata.exif.ExifGpsMetadata;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import de.elmar_baumann.imv.resource.Translation;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/14
 */
public final class TableCellRendererExif extends TableCellRendererMetadata
        implements TableCellRenderer {

    private static final Translation translation = new Translation("ExifTagIdTagNameTranslations"); // NOI18N

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
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
                String translated = translation.translate(
                        Integer.toString(ifdEntry.getTag()),
                        ifdEntry.getName());
                cellLabel.setText(translated.trim());
            } else {
                cellLabel.setText(ExifFieldValueFormatter.format(ifdEntry));
            }
        } else if (value instanceof ExifGpsMetadata) {
            if (column == 0) {
                cellLabel.setText("Aufnahmeort anzeigen in:");
            }
        } else if (value instanceof Component) {
            return (Component) value;
        } else {
            cellLabel.setText(value.toString());
        }
        return cellLabel;
    }

    private void setIsStoredInDatabaseColor(JLabel cellLabel, IdfEntryProxy ifdEntry) {
        if (ExifInDatabase.isInDatabase(ifdEntry.getTag())) {
            setIsStoredInDatabaseColors(cellLabel);
        }
    }
}
