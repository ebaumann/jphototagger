package org.jphototagger.lib.inputverifier;

import org.jphototagger.lib.resource.JslBundle;

import java.io.Serializable;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;

/**
 * Verifies Input in a <code>JTextComponent</code> against a maximum and maximum
 * number and displays an error message dialog on errors.
 *
 * @author Elmar Baumann
 */
public final class InputVerifierNumberRange extends InputVerifier
        implements Serializable {
    private static final long serialVersionUID = -8805199830659169644L;
    private final double      min;
    private final double      max;
    private boolean           message = true;

    /**
     * Constructor.
     *
     * @param  min minimum value
     * @param  max maximum value
     * @throws IllegalArgumentException if maximum is less than minimum
     */
    public InputVerifierNumberRange(double min, double max) {
        if (max < min) {
            throw new IllegalArgumentException("Maximum is less than minimum! "
                                               + max + " < " + min);
        }

        this.min = min;
        this.max = max;
    }

    /**
     * Sets whether to display an error message on invalid input.
     *
     * @param display true if display an error message. Default: true.
     */
    public void setDisplayMessage(boolean display) {
        this.message = display;
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
        String string = getString(component);

        if (string.isEmpty()) {
            return true;
        }

        Double value = toDouble(string);

        if (value == null) {
            return false;
        }

        return (value >= min) && (value <= max);
    }

    private String getString(JComponent component) {
        if (component instanceof JTextComponent) {
            return ((JTextComponent) component).getText().trim();
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
        if (!message) {
            return;
        }

        JOptionPane
            .showMessageDialog(
                input,
                JslBundle.INSTANCE
                    .getString(
                        "InputVerifierNumberRange.ErrorMessage", min,
                        max), JslBundle.INSTANCE
                            .getString(
                                "InputVerifierNumberRange.Error.Title"), JOptionPane
                                    .ERROR_MESSAGE);
    }
}
