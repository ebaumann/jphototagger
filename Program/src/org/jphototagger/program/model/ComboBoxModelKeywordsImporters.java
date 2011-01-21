package org.jphototagger.program.model;

import org.jphototagger.program.importer.KeywordImporters;
import org.jphototagger.program.importer.KeywordsImporter;

import javax.swing.DefaultComboBoxModel;

/**
 * Contains all implemented {@link KeywordsImporter}s retrieved through
 * {@link KeywordImporters#getAll()}.
 *
 * @author Elmar Baumann
 */
public final class ComboBoxModelKeywordsImporters extends DefaultComboBoxModel {
    private static final long serialVersionUID = 7228501230169153588L;

    public ComboBoxModelKeywordsImporters() {
        addElements();
    }

    private void addElements() {
        for (KeywordsImporter importer : KeywordImporters.getAll()) {
            addElement(importer);
        }
    }
}
