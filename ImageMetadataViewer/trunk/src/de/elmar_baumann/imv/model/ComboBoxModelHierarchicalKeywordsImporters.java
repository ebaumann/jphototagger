package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.importer.HierarchicalKeywordsImporter;
import de.elmar_baumann.imv.importer.HierarchicalKeywordsImporters;
import javax.swing.DefaultComboBoxModel;

/**
 * Contains all implemented {@link HierarchicalKeywordsImporter}s.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-01
 */
public final class ComboBoxModelHierarchicalKeywordsImporters
        extends DefaultComboBoxModel {

    public ComboBoxModelHierarchicalKeywordsImporters() {
        addElements();
    }

    private void addElements() {
        for (HierarchicalKeywordsImporter importer :
                HierarchicalKeywordsImporters.getAll()) {
            addElement(importer);
        }
    }
}
