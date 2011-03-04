package org.jphototagger.program.view.renderer;

import org.jphototagger.program.exporter.Exporter;
import org.jphototagger.program.importer.Importer;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Renders lists with {@link Importer}s and {@link Exporter}s for keywords.
 *
 * @author Elmar Baumann
 */
public final class ListCellRendererKeywordImExport extends DefaultListCellRenderer {
    private static final long serialVersionUID = -2640679743272527934L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof Importer) {
            Importer importer = (Importer) value;

            label.setText(importer.getDisplayName());
            label.setIcon(importer.getIcon());
        } else if (value instanceof Exporter) {
            Exporter exporter = (Exporter) value;

            label.setText(exporter.getDisplayName());
            label.setIcon(exporter.getIcon());
        }

        return label;
    }
}
