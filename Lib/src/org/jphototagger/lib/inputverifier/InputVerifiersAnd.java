package org.jphototagger.lib.inputverifier;

import java.io.Serializable;
import java.util.List;
import javax.swing.InputVerifier;
import javax.swing.JComponent;

/**
 * All added input verifiers must return true for a valid input.
 *
 * @author Elmar Baumann
 */
public final class InputVerifiersAnd extends InputVerifiers implements Serializable {
    private static final long serialVersionUID = 8196624906816940229L;

    /**
     * All added verifiers must verify the input as true for a valid input.
     *
     * @param  input input
     * @return       true if all of the added verifiers returning true, false
     *               if one of the added verifiers returns false
     */
    @Override
    public boolean verify(JComponent input) {
        List<InputVerifier> verifiers = getVerifiers();

        synchronized (verifiers) {
            for (InputVerifier verifier : verifiers) {
                if (!verifier.verify(input)) {
                    return false;
                }
            }
        }

        return true;
    }
}
