/*
 * @(#)InputVerifierMaxLength.java    Created on 2008-10-28
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

import org.jphototagger.lib.resource.JslBundle;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;

/**
 * Verifies Input in a <code>JTextComponent</code> against a maximum allowed
 * length and displays an error message dialog on errors.
 *
 * @author  Elmar Baumann
 */
public final class InputVerifierMaxLength extends InputVerifier {
    private final int maxLength;
    private boolean   message = true;

    /**
     * Constructor.
     *
     * @param maxLength Maximum length of the input. {@code Must be >= 0}.
     * @throws IllegalArgumentException if {@code maxLength < 0}
     */
    public InputVerifierMaxLength(int maxLength) {
        if (maxLength < 0) {
            throw new IllegalArgumentException("maxLength < 0: " + maxLength);
        }

        this.maxLength = maxLength;
    }

    /**
     * Sets whether to display an error message on invalid input.
     *
     * @param display true if display an error message. Default: true.
     */
    public void setDisplayMessage(boolean display) {
        this.message = display;
    }

    @Override
    public boolean verify(JComponent input) {
        boolean lengthOk = lengthOk(input);

        if (!lengthOk) {
            errorMessage(input);
        }

        return lengthOk;
    }

    private boolean lengthOk(JComponent component) {
        return getString(component).length() <= maxLength;
    }

    private String getString(JComponent component) {
        if (component instanceof JTextComponent) {
            return ((JTextComponent) component).getText().trim();
        }

        return "";
    }

    private void errorMessage(JComponent input) {
        if (!message) {
            return;
        }

        JOptionPane
            .showMessageDialog(
                input,
                JslBundle.INSTANCE
                    .getString(
                        "InputVerifierMaxLength.ErrorMessage",
                        maxLength), JslBundle.INSTANCE
                            .getString(
                                "InputVerifierMaxLength.Error.Title"), JOptionPane
                                    .ERROR_MESSAGE);
    }
}
