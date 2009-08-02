package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.exporter.HierarchicalKeywordsExporter;
import de.elmar_baumann.imv.exporter.HierarchicalKeywordsExporters;
import javax.swing.DefaultComboBoxModel;

/**
 * Contains all implemented {@link HierarchicalKeywordsExporter}s.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-01
 */
public final class ComboBoxModelHierarchicalKeywordsExporters
        extends DefaultComboBoxModel {

    public ComboBoxModelHierarchicalKeywordsExporters() {
        addElements();
    }

    private void addElements() {
        for (HierarchicalKeywordsExporter importer :
                HierarchicalKeywordsExporters.getAll()) {
            addElement(importer);
        }
    }
}
