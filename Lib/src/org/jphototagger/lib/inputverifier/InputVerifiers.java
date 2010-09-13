/*
 * @(#)InputVerifiers.java    Created on 2010-01-06
 *
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
 * @author  Elmar Baumann
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
