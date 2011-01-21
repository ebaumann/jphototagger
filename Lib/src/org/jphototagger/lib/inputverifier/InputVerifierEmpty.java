package org.jphototagger.lib.inputverifier;

import java.io.Serializable;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 * A valid input of a <code>JTextComponent</code> has to be empty.
 *
 * @author Elmar Baumann
 */
public final class InputVerifierEmpty extends InputVerifier
        implements Serializable {
    private static final long serialVersionUID = -7155052662328216344L;
    private final boolean     trim;

    /**
     * Constructor setting whether the text shall be trimmed.
     *
     * @param trim true if the text shall be trimmed before verifying.
     *             Default: false.
     */
    public InputVerifierEmpty(boolean trim) {
        this.trim = trim;
    }

    public InputVerifierEmpty() {
        trim = false;
    }

    @Override
    public boolean verify(JComponent input) {
        if (input instanceof JTextComponent) {
            String text = ((JTextComponent) input).getText();

            return trim
                   ? text.trim().isEmpty()
                   : text.isEmpty();
        }

        return false;
    }
}
