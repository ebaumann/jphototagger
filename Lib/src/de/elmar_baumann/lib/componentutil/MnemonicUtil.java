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

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-25
 */
public final class MnemonicUtil {

    private static final Map<Character, Integer> MNEMONIC_OF_CHAR = new HashMap<Character, Integer>();

    static {
        MNEMONIC_OF_CHAR.put('0', KeyEvent.VK_0);
        MNEMONIC_OF_CHAR.put('1', KeyEvent.VK_1);
        MNEMONIC_OF_CHAR.put('2', KeyEvent.VK_2);
        MNEMONIC_OF_CHAR.put('3', KeyEvent.VK_3);
        MNEMONIC_OF_CHAR.put('4', KeyEvent.VK_4);
        MNEMONIC_OF_CHAR.put('5', KeyEvent.VK_5);
        MNEMONIC_OF_CHAR.put('6', KeyEvent.VK_6);
        MNEMONIC_OF_CHAR.put('7', KeyEvent.VK_7);
        MNEMONIC_OF_CHAR.put('8', KeyEvent.VK_8);
        MNEMONIC_OF_CHAR.put('9', KeyEvent.VK_9);
        MNEMONIC_OF_CHAR.put('A', KeyEvent.VK_A);
        MNEMONIC_OF_CHAR.put('B', KeyEvent.VK_B);
        MNEMONIC_OF_CHAR.put('C', KeyEvent.VK_C);
        MNEMONIC_OF_CHAR.put('D', KeyEvent.VK_D);
        MNEMONIC_OF_CHAR.put('E', KeyEvent.VK_E);
        MNEMONIC_OF_CHAR.put('F', KeyEvent.VK_F);
        MNEMONIC_OF_CHAR.put('G', KeyEvent.VK_G);
        MNEMONIC_OF_CHAR.put('H', KeyEvent.VK_H);
        MNEMONIC_OF_CHAR.put('I', KeyEvent.VK_I);
        MNEMONIC_OF_CHAR.put('J', KeyEvent.VK_J);
        MNEMONIC_OF_CHAR.put('K', KeyEvent.VK_K);
        MNEMONIC_OF_CHAR.put('L', KeyEvent.VK_L);
        MNEMONIC_OF_CHAR.put('M', KeyEvent.VK_M);
        MNEMONIC_OF_CHAR.put('N', KeyEvent.VK_N);
        MNEMONIC_OF_CHAR.put('O', KeyEvent.VK_O);
        MNEMONIC_OF_CHAR.put('P', KeyEvent.VK_P);
        MNEMONIC_OF_CHAR.put('Q', KeyEvent.VK_Q);
        MNEMONIC_OF_CHAR.put('R', KeyEvent.VK_R);
        MNEMONIC_OF_CHAR.put('S', KeyEvent.VK_S);
        MNEMONIC_OF_CHAR.put('T', KeyEvent.VK_T);
        MNEMONIC_OF_CHAR.put('U', KeyEvent.VK_U);
        MNEMONIC_OF_CHAR.put('V', KeyEvent.VK_V);
        MNEMONIC_OF_CHAR.put('W', KeyEvent.VK_W);
        MNEMONIC_OF_CHAR.put('X', KeyEvent.VK_X);
        MNEMONIC_OF_CHAR.put('Y', KeyEvent.VK_Y);
        MNEMONIC_OF_CHAR.put('Z', KeyEvent.VK_Z);
    }

    /**
     * Returns a mnemonic (index) of a specific character.
     * 
     * @param  c character in range [A-Z,0-9]
     * @return   mnemonic 
     * @throws   IllegalArgumentException if <code>c</code> is not in range
     * @see      #isInRange(char)
     */
    public static int getMnemonicOf(char c) {
        if (!isInRange(c)) throw new IllegalArgumentException("Character is not in Range: " + c);

        return MNEMONIC_OF_CHAR.get(c);
    }

    public static boolean isInRange(char c) {
        return MNEMONIC_OF_CHAR.containsKey(c);
    }

    private MnemonicUtil() {
    }
}
