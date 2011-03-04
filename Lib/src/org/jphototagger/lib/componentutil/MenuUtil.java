package org.jphototagger.lib.componentutil;

import org.jphototagger.lib.system.SystemUtil;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Helper Methods for Menus.
 *
 * @author Elmar Baumann
 */
public final class MenuUtil {
    private static final String AMPERSAND = "&";

    private MenuUtil() {}

    /**
     * Calls {@link #setMnemonics(javax.swing.JMenuItem)} for every menu in a
     * menu bar and for all it's items.
     * <p>
     * According to <a href="http://developer.apple.com/mac/library/documentation/Java/Conceptual/Java14Development/07-NativePlatformIntegration/NativePlatformIntegration.html">Mac OS X Integration for Java</a>
     * no mnemonics will be set, if the virtual machine runs on a macintosh
     * operating system, the mnemonic ampersand will be removed.
     *
     * @param  menuBar menu bar
     * @throws         NullPointerException if <code>menuBar</code> is null
     */
    public static void setMnemonics(JMenuBar menuBar) {
        if (menuBar == null) {
            throw new NullPointerException("menuBar == null");
        }

        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);

            if (menu != null) {
                setMnemonicsToItems(menu);
            }
        }
    }

    /**
     * Calls {@link #setMnemonics(javax.swing.JMenuItem)} to a menu and all it's
     * items.
     * <p>
     * According to <a href="http://developer.apple.com/mac/library/documentation/Java/Conceptual/Java14Development/07-NativePlatformIntegration/NativePlatformIntegration.html">Mac OS X Integration for Java</a>
     * no mnemonics will be set, if the virtual machine runs on a macintosh
     * operating system, the mnemonic ampersand will be removed.
     *
     * @param  menu menu
     * @throws      NullPointerException if <code>menu</code> is null
     */
    private static void setMnemonicsToItems(JMenu menu) {
        if (menu == null) {
            throw new NullPointerException("menu == null");
        }

        setMnemonics(menu);

        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem item = menu.getItem(i);

            if (item != null) {
                setMnemonics(item);

                if (item instanceof JMenu) {
                    setMnemonicsToItems((JMenu) item);    // recursive
                }
            }
        }
    }

    /**
     * Sets to a menu item the char behind the first ampersand ({@code &}) as
     * mnemonic and removes the ampersand.
     * <p>
     * According to <a href="http://developer.apple.com/mac/library/documentation/Java/Conceptual/Java14Development/07-NativePlatformIntegration/NativePlatformIntegration.html">Mac OS X Integration for Java</a>
     * no mnemonics will be set, if the virtual machine runs on a macintosh
     * operating system, the mnemonic ampersand will be removed.
     *
     * @param  item menu item
     * @throws      NullPointerException if <code>item</code> is null
     */
    public static void setMnemonics(JMenuItem item) {
        if (item == null) {
            throw new NullPointerException("item == null");
        }

        String itemText = item.getText();
        int itemTextLength = itemText.length();
        int ampersandIndex = itemText.indexOf(AMPERSAND);

        if ((ampersandIndex >= 0) && (ampersandIndex < itemTextLength - 1)) {
            if (SystemUtil.isMac()) {
                removeAmpersand(item, itemText, ampersandIndex);
            } else {
                removeAmpersandSetMnemonic(item, itemText, ampersandIndex);
            }
        }
    }

    private static void removeAmpersandSetMnemonic(JMenuItem item, String itemText, int ampersandIndex) {
        char mnemonic = itemText.charAt(ampersandIndex + 1);
        int itemTextLength = itemText.length();
        String text = itemText.substring(0, ampersandIndex) + itemText.substring(ampersandIndex + 1, itemTextLength);

        item.setText(text);
        item.setMnemonic(mnemonic);
    }

    private static void removeAmpersand(JMenuItem item, String itemText, int ampersandIndex) {
        if (ampersandIndex == 0) {
            item.setText(itemText.substring(1));
        } else {
            item.setText(itemText.substring(0, ampersandIndex) + itemText.substring(ampersandIndex + 1));
        }
    }
}
