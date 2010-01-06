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

import de.elmar_baumann.lib.resource.Bundle;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;

/**
 * Verifies wheter a text component's text matches a regex string pattern.
 * <p>
 * Displays an error message on errors.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-06
 */
public final class InputVerifierStringPattern extends InputVerifierExt {

    private final String pattern;
    private       String errorHint;

    public InputVerifierStringPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * Sets an error hint which will be displayed instead of the regular
     * expression.
     * <p>
     * This text can contain HTML, it will be displayed in a
     * <code>JOptionPane</code> dialog.
     *
     * @param hint hint
     */
    public void setErrorHint(String hint) {
        errorHint = hint;
    }

    /**
     * Verifies the input.
     *
     * @param  input an instance of <code>JTextComponent</code>
     * @return       true if input is ok
     */
    @Override
    public boolean verify(JComponent input) {

        assert input instanceof JTextComponent : input;

        if (input instanceof JTextComponent) {

            return isValid((JTextComponent) input) && super.verify(input);
        }
        return  true;
    }

    private boolean isValid(JTextComponent textComponent) {

         String text = textComponent.getText();

         if (!text.matches(pattern)) {

             errorMessage(textComponent);
             return false;
         }
         return true;
    }

    private void errorMessage(JComponent input) {
        JOptionPane.showMessageDialog(
                input,
                errorHint == null
                    ? Bundle.getString("InputVerifierStringPattern.ErrorMessage", pattern)
                    : errorHint,
                Bundle.getString("InputVerifierStringPattern.Error.Title"),
                JOptionPane.ERROR_MESSAGE);
    }
}
