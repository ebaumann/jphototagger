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
                    component.getClass().toString(); // NOI18N
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
                min, max), // NOI18N
                Bundle.getString("InputVerifierNumberRange.Error.Title"), // NOI18N
                JOptionPane.ERROR_MESSAGE);
    }
}
