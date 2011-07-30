package org.jphototagger.lib.componentutil;

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
                MnemonicIndexString mnemonicIndexString = MnemonicUtil.getMnemonic(title);

                if (mnemonicIndexString.index != -1) {
                    pane.setTitleAt(tabIndex, mnemonicIndexString.string);
                    pane.setMnemonicAt(tabIndex, mnemonicIndexString.index);
                }
            }
        }
    }

    private TabbedPaneUtil() {
    }
}
