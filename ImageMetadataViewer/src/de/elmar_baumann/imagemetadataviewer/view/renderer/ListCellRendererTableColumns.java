package de.elmar_baumann.imagemetadataviewer.view.renderer;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Renderer für Tabellenspaltenbeschreibungen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/13
 * @see     de.elmar_baumann.imagemetadataviewer.database.metadata.Column#getDescription()
 */
public class ListCellRendererTableColumns extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        RendererTableColumn.setLabelText(label, (Column) value);
        return label;
    }
}
