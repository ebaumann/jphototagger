package org.jphototagger.program.module.search;

import org.jphototagger.program.app.ui.FormatterLabelTableColumn;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.jphototagger.domain.metadata.MetaDataValue;

/**
 * Renderer für Tabellenspaltenbeschreibungen.
 *
 * @author Elmar Baumann
 */
public final class TableColumnsListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = -3987847245199721880L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        FormatterLabelTableColumn.setLabelText(label, (MetaDataValue) value);

        return label;
    }
}
