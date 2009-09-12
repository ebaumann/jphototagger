/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
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

import de.elmar_baumann.lib.resource.Bundle;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Verifies Input in a <code>JTextField</code> or <code>JTextArea</code> against
 * a maximum allowed length. Shows a message dialog on errors.
 * 
 * To use other components, enhance the private method <code>lengthOk()</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-28
 */
public final class InputVerifierMaxLength extends InputVerifier {

    private final int maxLength;

    /**
     * Constructor.
     *
     * @param maxLength Maximum length of the input. {@code Must be >= 0}.
     * @throws IllegalArgumentException if {@code maxLength < 0}
     */
    public InputVerifierMaxLength(int maxLength) {
        if (maxLength < 0) {
            throw new IllegalArgumentException("maxLength < 0: " + maxLength); // NOI18N
        }

        this.maxLength = maxLength;
    }

    @Override
    public boolean verify(JComponent input) {
        boolean lengthOk = lengthOk(input);
        if (!lengthOk) {
            errorMessage(input);
        }
        return lengthOk;
    }

    private boolean lengthOk(JComponent input) {
        assert input != null : input;

        if (input instanceof JTextField) {
            return ((JTextField) input).getText().length() <= maxLength;
        } else if (input instanceof JTextArea) {
            return ((JTextArea) input).getText().length() <= maxLength;
        } else {
            assert false : "Unknown component: " + input.getClass().toString(); // NOI18N
        }
        return true;
    }

    private void errorMessage(JComponent input) {
        JOptionPane.showMessageDialog(
                input,
                Bundle.getString("InputVerifierMaxLength.ErrorMessage", maxLength), // NOI18N
                Bundle.getString("InputVerifierMaxLength.Error.Title"), // NOI18N
                JOptionPane.ERROR_MESSAGE);
    }
}
