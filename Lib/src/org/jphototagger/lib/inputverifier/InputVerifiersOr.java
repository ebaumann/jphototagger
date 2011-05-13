package org.jphototagger.lib.inputverifier;

import java.io.Serializable;
import java.util.List;
import javax.swing.InputVerifier;
import javax.swing.JComponent;

/**
 * One of the added input verifiers must return true for a valid input.
 *
 * @author Elmar Baumann
 */
public final class InputVerifiersOr extends InputVerifiers implements Serializable {
    private static final long serialVersionUID = -9086823766117067406L;

    /**
     * One of the added verifiers must verify the input as true for a valid
     * input.
     *
     * @param  input input
     * @return       true if one of the added verifiers returns true, false
     *               if all of the added verifiers returns false
     */
    @Override
    public boolean verify(JComponent input) {
        List<InputVerifier> verifiers = getVerifiers();

        synchronized (verifiers) {
            for (InputVerifier verifier : verifiers) {
                if (verifier.verify(input)) {
                    return true;
                }
            }
        }

        return false;
    }
}
