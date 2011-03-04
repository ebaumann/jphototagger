package org.jphototagger.program.view.renderer;

import org.jphototagger.program.database.metadata.Column;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Renderer für Tabellenspaltenbeschreibungen.
 *
 * @author Elmar Baumann
 * @see     org.jphototagger.program.database.metadata.Column#getDescription()
 */
public final class ListCellRendererTableColumns extends DefaultListCellRenderer {
    private static final long serialVersionUID = -3987847245199721880L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        FormatterLabelTableColumn.setLabelText(label, (Column) value);

        return label;
    }
}
