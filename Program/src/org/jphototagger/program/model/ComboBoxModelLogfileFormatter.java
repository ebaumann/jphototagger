package org.jphototagger.program.model;

import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;
import javax.swing.DefaultComboBoxModel;

/**
 * Elements are the <strong>{@link Class} objects</strong> of specialized
 * {@link java.util.logging.Formatter}s.
 *
 * These formatters can be used for formatting {@link java.util.logging.Logger}
 * output.
 *
 * @author Elmar Baumann
 */
public final class ComboBoxModelLogfileFormatter extends DefaultComboBoxModel {
    private static final long serialVersionUID = -7817194934431355197L;

    public ComboBoxModelLogfileFormatter() {
        addElements();
    }

    private void addElements() {

        // Wird die Reihenfolge verändert, stimmen die Benutzereinstellungen
        // nicht mehr, da diese den Index speichern
        addElement(XMLFormatter.class);
        addElement(SimpleFormatter.class);
    }
}
