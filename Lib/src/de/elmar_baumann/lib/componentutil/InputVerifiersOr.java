/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.lib.componentutil;

import java.util.List;
import javax.swing.InputVerifier;
import javax.swing.JComponent;

/**
 * One of the added input verifiers must return true for a valid input.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-06
 */
public final class InputVerifiersOr extends InputVerifiers {

    /**
     * One of the added verifiers must verify the input as true for a valid input.
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
                if (verifier.verify(input)) return true;
            }
        }
        return false;
    }
}
