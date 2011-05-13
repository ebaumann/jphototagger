package org.jphototagger.lib.inputverifier;

import java.io.Serializable;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 * Verifies wheter a text component's text matches a regex string pattern.
 *
 * @author Elmar Baumann
 */
public final class InputVerifierStringPattern extends InputVerifier implements Serializable {
    private static final long serialVersionUID = 3060108768868984287L;
    private final String pattern;
    private final boolean trim;

    /**
     * Verifies the untrimmed input.
     *
     * @param pattern pattern
     */
    public InputVerifierStringPattern(String pattern) {
        this.pattern = pattern;
        trim = false;
    }

    /**
     * Verifies trimmed or untrimmed input.
     *
     * @param pattern pattern
     * @param trim    true if the input shall be trimmed before verifyng
     */
    public InputVerifierStringPattern(String pattern, boolean trim) {
        this.pattern = pattern;
        this.trim = trim;
    }

    /**
     * Verifies the input.
     *
     * @param  input an instance of <code>JTextComponent</code>
     * @return       true if input is ok
     */
    @Override
    public boolean verify(JComponent input) {
        if (input instanceof JTextComponent) {
            return isValid((JTextComponent) input);
        }

        return true;
    }

    private boolean isValid(JTextComponent textComponent) {
        String text = trim
                      ? textComponent.getText().trim()
                      : textComponent.getText();

        return text.matches(pattern);
    }
}
