package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.exporter.HierarchicalKeywordsExporter;
import de.elmar_baumann.imv.importer.HierarchicalKeywordsImporter;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Renders lists with {@link HierarchicalKeywordsImporter}s and
 * {@link HierarchicalKeywordsExporter}s.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-04
 */
public final class ListCellRendererHierarchicalKeywordsImExporter
        extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        if (value instanceof HierarchicalKeywordsImporter) {
            HierarchicalKeywordsImporter importer =
                    (HierarchicalKeywordsImporter) value;
            label.setText(importer.getDescription());
            label.setIcon(importer.getIcon());
        } else if (value instanceof HierarchicalKeywordsExporter) {
            HierarchicalKeywordsExporter exporter =
                    (HierarchicalKeywordsExporter) value;
            label.setText(exporter.getDescription());
            label.setIcon(exporter.getIcon());
        }
        return label;
    }
}
