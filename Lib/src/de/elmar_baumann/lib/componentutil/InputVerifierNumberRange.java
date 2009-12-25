/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.lib.componentutil;

import de.elmar_baumann.lib.resource.Bundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Verifies Input in a <code>JTextField</code> or <code>JTextArea</code> against
 * a maximum and maximum number.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-05
 */
public final class InputVerifierNumberRange extends InputVerifier {

    private final double min;
    private final double max;

    /**
     * Constructor.
     *
     * @param min minimum value
     * @param max maximum value
     * @throws IllegalArgumentException if maximum is less than minimum
     */
    public InputVerifierNumberRange(double min, double max) {
        if (max < min) throw new IllegalArgumentException(
                    "Maximum is less than minimum! " + max + " < " + min);

        this.min = min;
        this.max = max;
    }

    @Override
    public boolean verify(JComponent component) {
        boolean lengthOk = lengthOk(component);
        if (!lengthOk) {
            errorMessage(component);
        }
        return lengthOk;
    }

    private boolean lengthOk(JComponent component) {
        assert component != null : component;

        String string = getString(component);
        if (string.isEmpty()) return true;

        Double value = toDouble(string);
        if (value == null) return false;
        return value >= min && value <= max;
    }

    private String getString(JComponent component) {
        if (component instanceof JTextField) {
            return (((JTextField) component).getText()).trim();
        } else if (component instanceof JTextArea) {
            return (((JTextArea) component).getText()).trim();
        } else {
            assert false : "Unknown component: " +
                    component.getClass().toString();
        }
        return "";
    }

    private Double toDouble(String string) {
        try {
            return Double.valueOf(string);
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "", ex);
        }
        return null;
    }

    private void errorMessage(JComponent input) {
        JOptionPane.showMessageDialog(
                input,
                Bundle.getString("InputVerifierNumberRange.ErrorMessage",
                min, max),
                Bundle.getString("InputVerifierNumberRange.Error.Title"),
                JOptionPane.ERROR_MESSAGE);
    }
}
