package de.elmar_baumann.imv.view.renderer;

import com.imagero.reader.tiff.IFDEntry;
import de.elmar_baumann.imv.database.metadata.selections.ExifInDatabase;
import de.elmar_baumann.imv.image.metadata.exif.ExifFieldValueFormatter;
import de.elmar_baumann.imv.resource.Translation;
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
public class TableCellRendererExif extends TableCellRendererMetadata
    implements TableCellRenderer {

    private static final Translation translation = new Translation("ExifTagIdTagNameTranslations"); // NOI18N
    private static final ExifInDatabase exifInDatabase = ExifInDatabase.getInstance();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        assert column < 2 : column;
        JLabel cellLabel = new JLabel();
        IFDEntry ifdEntry = (IFDEntry) value;

        setDefaultCellColors(cellLabel, isSelected);
        setIsStoredInDatabaseColor(cellLabel, ifdEntry);

        if (column == 0) {
            setHeaderFont(cellLabel);
            String translated = translation.translate(
                Integer.toString(ifdEntry.getEntryMeta().getTag()),
                ifdEntry.getEntryMeta().getName());
            cellLabel.setText(translated.trim());
        } else {
            setContentFont(cellLabel);
            cellLabel.setText(ExifFieldValueFormatter.format(ifdEntry));
        }
        return cellLabel;
    }

    private void setIsStoredInDatabaseColor(JLabel cellLabel, IFDEntry ifdEntry) {
        if (exifInDatabase.isInDatabase(ifdEntry.getEntryMeta().getTag())) {
            setIsStoredInDatabaseColors(cellLabel);
        }
    }
}
