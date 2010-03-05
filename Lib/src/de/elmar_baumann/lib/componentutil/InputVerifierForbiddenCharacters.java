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

import java.util.ArrayList;
import java.util.List;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-29
 */
public final class InputVerifierForbiddenCharacters extends InputVerifier {

    private final List<Character> forbiddenCharacters = new ArrayList<Character>();

    public InputVerifierForbiddenCharacters(Character... forbidden) {
        for (Character c : forbidden) {
            forbiddenCharacters.add(c);
        }
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
            return !containsOneOf((JTextComponent) input);
        }
        return  true;
    }

    private boolean containsOneOf(JTextComponent tc) {
        String text = tc.getText();
        for (Character c : forbiddenCharacters) {
            if (text.indexOf(c) >= 0) return true;
        }
        return false;
    }

    public String getChars() {
        StringBuilder sb = new StringBuilder();
        for (Character c : forbiddenCharacters) {
            sb.append(c);
        }
        return sb.toString();
    }
}
