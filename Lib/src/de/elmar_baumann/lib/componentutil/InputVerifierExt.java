/*
 * JPhotoTagger tags and finds images fast
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

import java.util.HashSet;
import java.util.Set;
import javax.swing.InputVerifier;
import javax.swing.JComponent;

/**
 * Extended input verifier, can extended by other verfiers.
 * <p>
 * A specialiced class has to call {@link #verify()} of this class to ensure,
 * that all added verifiers will verify the input.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-06
 */
public class InputVerifierExt extends InputVerifier {

    private final Set<InputVerifier> otherVerifiers = new HashSet<InputVerifier>();

    public void addVerifier(InputVerifier verifier) {
        synchronized (otherVerifiers) {
            otherVerifiers.add(verifier);
        }
    }

    public void removeVerifier(InputVerifier verifier) {
        synchronized (otherVerifiers) {
            otherVerifiers.remove(verifier);
        }
    }

    @Override
    public boolean verify(JComponent input) {
        synchronized (otherVerifiers) {

            for (InputVerifier verifier : otherVerifiers) {

                if (!verifier.verify(input)) return false;
            }
        }
        return true;
    }
}
