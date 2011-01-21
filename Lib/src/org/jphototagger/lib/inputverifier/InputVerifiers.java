package org.jphototagger.lib.inputverifier;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.InputVerifier;
import javax.swing.JComponent;

/**
 * A collection of input verifiers.
 *
 * @author Elmar Baumann
 */
public class InputVerifiers extends InputVerifier implements Serializable {
    private static final long         serialVersionUID = 2492720541367098384L;
    private final List<InputVerifier> verifiers =
        new ArrayList<InputVerifier>();

    public void addVerifier(InputVerifier verifier) {
        synchronized (verifiers) {
            verifiers.add(verifier);
        }
    }

    public void removeVerifier(InputVerifier verifier) {
        synchronized (verifiers) {
            verifiers.remove(verifier);
        }
    }

    protected List<InputVerifier> getVerifiers() {
        return Collections.unmodifiableList(verifiers);
    }

    /**
     * Does not verify, this has to be done by a specialized class.
     *
     * @param  input input
     * @return       nothing
     * @throws UnsupportedOperationException always, shall never be called
     */
    @Override
    public boolean verify(JComponent input) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
