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

import de.elmar_baumann.lib.generics.Pair;
import javax.swing.JTabbedPane;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-25
 */
public final class TabbedPaneUtil {

    /**
     * Sets as mnemonic every character in the tab title after the first
     * ampersand (&amp;).
     *
     * <em>The valid range of mnemonic characters is [A-Za-z0-9]</em>.
     *
     * @param pane tabbed pane
     */
    public static void setMnemonics(JTabbedPane pane) {
        int tabCount = pane.getTabCount();

        for (int tabIndex = 0; tabIndex < tabCount; tabIndex++) {
            String title = pane.getTitleAt(tabIndex);
            if (title != null && title.length() > 1) {
                Pair<Integer, String> mnPair = MnemonicUtil.getMnemonic(title);
                if (mnPair.getFirst() != -1) {
                    pane.setTitleAt(tabIndex, mnPair.getSecond());
                    pane.setMnemonicAt(tabIndex, mnPair.getFirst());
                }
            }
        }
    }

    private TabbedPaneUtil() {
    }
}
