package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.database.metadata.Column;
import java.awt.Component;
import java.awt.Font;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Renderer für Tabellenspaltenbeschreibungen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/13
 * @see     de.elmar_baumann.imv.database.metadata.Column#getDescription()
 */
public class ListCellRendererTableColumns extends DefaultListCellRenderer {

    private static Font cellFont = new Font(Font.DIALOG, Font.PLAIN, 11);

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setFont(cellFont);
        RendererTableColumn.setLabelText(label, (Column) value);
        return label;
    }
}
