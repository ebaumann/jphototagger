package org.jphototagger.lib.inputverifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Elmar Baumann
 */
public final class InputVerifierForbiddenCharacters extends InputVerifier implements Serializable {
    private static final long serialVersionUID = -137243048239784843L;
    private final List<Character> forbiddenCharacters = new ArrayList<Character>();

    public InputVerifierForbiddenCharacters(Character... forbidden) {
        forbiddenCharacters.addAll(Arrays.asList(forbidden));
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
            return !containsOneOf((JTextComponent) input);
        }

        return true;
    }

    private boolean containsOneOf(JTextComponent tc) {
        String text = tc.getText();

        for (Character c : forbiddenCharacters) {
            if (text.indexOf(c) >= 0) {
                return true;
            }
        }

        return false;
    }

    public String getChars() {
        StringBuilder sb = new StringBuilder();

        for (Character c : forbiddenCharacters) {
            sb.append(c);
        }

        return sb.toString();
    }
}
