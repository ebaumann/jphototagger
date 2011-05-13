package org.jphototagger.lib.inputverifier;

import java.io.Serializable;
import javax.swing.InputVerifier;
import javax.swing.JComponent;

/**
 * Considers every input as true.
 *
 * @author Elmar Baumann
 */
public final class InputVerifierAlwaysTrue extends InputVerifier implements Serializable {
    public static final InputVerifierAlwaysTrue INSTANCE = new InputVerifierAlwaysTrue();
    private static final long serialVersionUID = -8801246866926026861L;

    private InputVerifierAlwaysTrue() {}

    /**
     * Returns always true.
     *
     * @param  input input
     * @return       true
     */
    @Override
    public boolean verify(JComponent input) {
        return true;
    }
}
