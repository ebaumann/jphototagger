package org.jphototagger.program.view.renderer;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.jphototagger.domain.repository.RepositoryDataExporter;
import org.jphototagger.domain.repository.RepositoryDataImporter;

/**
 * Renders lists with {@code RepositoryDataImporter}s and {@code RepositoryDataExporter}s for keywords.
 *
 * @author Elmar Baumann
 */
public final class KeywordImExportListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = -2640679743272527934L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof RepositoryDataImporter) {
            RepositoryDataImporter importer = (RepositoryDataImporter) value;

            label.setText(importer.getDisplayName());
            label.setIcon(importer.getIcon());
        } else if (value instanceof RepositoryDataExporter) {
            RepositoryDataExporter exporter = (RepositoryDataExporter) value;

            label.setText(exporter.getDisplayName());
            label.setIcon(exporter.getIcon());
        }

        return label;
    }
}
