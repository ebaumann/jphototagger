package de.elmar_baumann.imv.model;

import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;
import javax.swing.DefaultComboBoxModel;

/**
 * Logdateien-Formatierer.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/17
 */
public final class ComboBoxModelLogfileFormatter extends DefaultComboBoxModel {

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
