/*
 * @(#)InputVerifierStringPattern.java    Created on 2010-01-06
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

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 * Verifies wheter a text component's text matches a regex string pattern.
 *
 * @author  Elmar Baumann
 */
public final class InputVerifierStringPattern extends InputVerifier
        implements Serializable {
    private static final long serialVersionUID = 3060108768868984287L;
    private final String      pattern;
    private final boolean     trim;

    /**
     * Verifies the untrimmed input.
     *
     * @param pattern pattern
     */
    public InputVerifierStringPattern(String pattern) {
        this.pattern = pattern;
        trim         = false;
    }

    /**
     * Verifies trimmed or untrimmed input.
     *
     * @param pattern pattern
     * @param trim    true if the input shall be trimmed before verifyng
     */
    public InputVerifierStringPattern(String pattern, boolean trim) {
        this.pattern = pattern;
        this.trim    = trim;
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
