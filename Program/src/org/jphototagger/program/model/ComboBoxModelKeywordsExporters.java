package org.jphototagger.program.model;

import org.jphototagger.program.exporter.Exporter;
import org.jphototagger.program.exporter.KeywordsExporters;
import javax.swing.DefaultComboBoxModel;

/**
 * Elements are all implemented {@link Exporter}s retrieved through
 * {@link KeywordsExporters#getAll()}.
 *
 * @author Elmar Baumann
 */
public final class ComboBoxModelKeywordsExporters extends DefaultComboBoxModel {
    private static final long serialVersionUID = 9136865883087790779L;

    public ComboBoxModelKeywordsExporters() {
        addElements();
    }

    private void addElements() {
        for (Exporter exporter : KeywordsExporters.getAll()) {
            addElement(exporter);
        }
    }
}
