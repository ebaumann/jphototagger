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
import javax.swing.JTabbedPane;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-25
 */
public final class TabbedPaneUtil {

    /**
     * Sets to a tabbed pane mnemonics.
     *
     * Takes the first valid character of the tab titles. If the first character
     * on a tab is always a mnemonic the second will be taken, then the third etc.
     *
     * @param pane tabbed pane
     */
    public static void setMnemonics(JTabbedPane pane) {
        int             count     = pane.getTabCount();
        List<Character> mnemonics = new ArrayList<Character>();

        for (int i = 0; i < count; i++) {
            String title = pane.getTitleAt(i);
            if (title != null && title.length() >= 1) {
                char mnemonicChar = getNotExistingMnemonicChar(title, mnemonics);
                if (MnemonicUtil.isInRange(mnemonicChar)) {
                    int mnemonic = MnemonicUtil.getMnemonicOf(mnemonicChar);
                    pane.setMnemonicAt(i, mnemonic);
                }
            }
        }
    }

    private static char getNotExistingMnemonicChar(
                          String title, List<Character> existingMnemonicChars) {

        assert title != null && title.length() >= 1;

        int     len       = title.length();
        int     index     = 0;
        boolean doesExist = true;
        boolean inRange   = false;
        char    mnemonic  = '0';

        while ((!inRange || doesExist) && index++ < len) {
            mnemonic = title.substring(index, index + 1).toUpperCase().charAt(0);
            doesExist = existingMnemonicChars.contains(mnemonic);
            inRange   = MnemonicUtil.isInRange(mnemonic);
        }

        return mnemonic;
    }

    private TabbedPaneUtil() {
    }
}
