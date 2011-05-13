package org.jphototagger.lib.componentutil;

import org.jphototagger.lib.generics.Pair;
import javax.swing.JTabbedPane;

/**
 *
 *
 * @author Elmar Baumann
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
        if (pane == null) {
            throw new NullPointerException("pane == null");
        }

        int tabCount = pane.getTabCount();

        for (int tabIndex = 0; tabIndex < tabCount; tabIndex++) {
            String title = pane.getTitleAt(tabIndex);

            if ((title != null) && (title.length() > 1)) {
                Pair<Integer, String> mnPair = MnemonicUtil.getMnemonic(title);

                if (mnPair.getFirst() != -1) {
                    pane.setTitleAt(tabIndex, mnPair.getSecond());
                    pane.setMnemonicAt(tabIndex, mnPair.getFirst());
                }
            }
        }
    }

    private TabbedPaneUtil() {}
}
