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

import java.util.ArrayList;
import java.util.List;
import javax.swing.InputVerifier;
import javax.swing.JComponent;

/**
 * A collection of input verifiers.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-06
 */
public class InputVerifiers extends InputVerifier {

    private final List<InputVerifier> verifiers = new ArrayList<InputVerifier>();

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
        return verifiers;
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
