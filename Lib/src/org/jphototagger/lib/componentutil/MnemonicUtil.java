package org.jphototagger.lib.componentutil;

import org.jphototagger.lib.generics.Pair;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

/**
 *
 *
 * @author Elmar Baumann
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
        if (!isInRange(c)) {
            throw new IllegalArgumentException("Character is not in Range: " + c);
        }

        return MNEMONIC_OF_CHAR.get(c);
    }

    public static boolean isInRange(char c) {
        return MNEMONIC_OF_CHAR.containsKey(c);
    }

    /**
     * Returns a mnemonic of a masked string.
     *
     * The mask is the first ampersand (&amp;), the mnemonic is the character
     * behind the ampersand.
     *
     * <em>The valid range of mnemonic characters is [A-Za-z0-9]</em>.
     *
     * @param   string masked string or string without an ampersand
     * @return         The first object of the pair is the mnemonic or -1 if the
     *                 string does not contain an ampersand or the mnemonic
     *                 character is invalid. The second object of the pair is
     *                 the string without the mask, or the string itself if the
     *                 first object is -1.
     * @throws NullPointerException if <code>string</code> is null
     */
    public static Pair<Integer, String> getMnemonic(String string) {
        if (string == null) {
            throw new NullPointerException("string == null");
        }

        Pair<Integer, String> noMnemonicPair = new Pair<Integer, String>(-1, string);

        if (string.length() < 2) {
            return noMnemonicPair;
        }

        int strlen = string.length();
        int ampersandIndex = string.indexOf('&');

        if (ampersandIndex < 0) {
            return noMnemonicPair;
        }

        if ((strlen < 2) || (ampersandIndex < 0) || (ampersandIndex > strlen - 2)) {
            return noMnemonicPair;
        }

        char mnemonicChar = string.substring(ampersandIndex + 1, ampersandIndex + 2).toUpperCase().charAt(0);
        boolean isInRange = MnemonicUtil.isInRange(mnemonicChar);

        assert isInRange : "Not in Range: " + mnemonicChar + " of " + string;

        if (isInRange) {
            int mnemonic = MnemonicUtil.getMnemonicOf(mnemonicChar);
            String titlePrefix = (ampersandIndex == 0)
                                 ? ""
                                 : string.substring(0, ampersandIndex);
            String titlePostfix = (ampersandIndex == strlen - 1)
                                  ? ""
                                  : string.substring(ampersandIndex + 1);

            return new Pair<Integer, String>(mnemonic, titlePrefix + titlePostfix);
        }

        return noMnemonicPair;
    }

    /**
     * Returns from a string the first not existing mnemonic character.
     *
     * @param  string                   string
     * @param  existingMnemonicChars    existing mnemonic chars
     * @return                          not existing valid mnemonic character in
     *                                  <code>string</code> but not in
     *                                  <code>existingMnemonicChars</code> or
     *                                  <code>'\0'</code> if no mnemonic
     *                                  character was found
     * @throws NullPointerException     if <code>string</code> or
     *                                  <strong>existingMnemonicChars</strong>
     *                                   is null
     * @throws IllegalArgumentException if the string is empty
     */
    public static char getNotExistingMnemonicChar(String string,
            Collection<? extends Character> existingMnemonicChars) {
        if (string == null) {
            throw new NullPointerException("string == null");
        }

        if (existingMnemonicChars == null) {
            throw new NullPointerException("existingMnemonicChars == null");
        }

        if (string.isEmpty()) {
            throw new IllegalArgumentException("Empty string!");
        }

        int len = string.length();
        int index = 0;
        boolean doesExist = true;
        boolean inRange = false;
        char mnemonic = '\0';

        while ((!inRange || doesExist) && (index < len)) {
            mnemonic = string.substring(index, index + 1).toUpperCase().charAt(0);
            doesExist = existingMnemonicChars.contains(mnemonic);
            inRange = MnemonicUtil.isInRange(mnemonic);
            index++;
        }

        return mnemonic;
    }

    /**
     * Sets as mnemonic every character in all components of a container after
     * the first ampersand (&amp;):
     *
     * <ul>
     * <li>{@link JLabel#setDisplayedMnemonic(int)} (Do not forget to call
     *     {@link JLabel#setLabelFor(java.awt.Component)})
     * </li>
     * <li>{@link AbstractButton#setMnemonic(int)}</li>
     * <li>{@link JTabbedPane#setMnemonicAt(int, int)}</li>
     * </ul>
     *
     * <em>The valid range of mnemonic characters is [A-Za-z0-9]</em>.
     *
     * @param container container. All components of this container will
     *                  gathered recursively.
     *
     * @throws NullPointerException if <code>component</code> is null
     */
    public static void setMnemonics(Container container) {
        if (container == null) {
            throw new NullPointerException("container == null");
        }

        int count = container.getComponentCount();

        setMnemonics((Component) container);

        for (int i = 0; i < count; i++) {
            Component component = container.getComponent(i);

            setMnemonics(component);

            if (component instanceof Container) {
                setMnemonics((Container) component);    // Recursive
            }
        }
    }

    /**
     * Sets as mnemonic every character in a (<em>one</em>) component after
     * the first ampersand (&amp;):
     *
     * <ul>
     * <li>{@link JLabel#setDisplayedMnemonic(int)} (Do not forget to call
     *     {@link JLabel#setLabelFor(java.awt.Component)})
     * </li>
     * <li>{@link AbstractButton#setMnemonic(int)}</li>
     * <li>{@link JTabbedPane#setMnemonicAt(int, int)}</li>
     * </ul>
     *
     * <em>The valid range of mnemonic characters is [A-Za-z0-9]</em>.
     *
     * @param component component
     *
     * @throws NullPointerException if <code>component</code> is null
     */
    public static void setMnemonics(Component component) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }

        Pair<Integer, String> mnPair = null;

        if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            String text = label.getText();

            if (text != null) {
                mnPair = getMnemonic(text);

                if (hasMnemonic(mnPair)) {
                    label.setText(mnPair.getSecond());
                    label.setDisplayedMnemonic(mnPair.getFirst());
                }
            }
        } else if (component instanceof AbstractButton) {
            AbstractButton button = (AbstractButton) component;
            String text = button.getText();

            if (text != null) {
                mnPair = getMnemonic(text);

                if (hasMnemonic(mnPair)) {
                    button.setText(mnPair.getSecond());
                    button.setMnemonic(mnPair.getFirst());
                }
            }
        } else if (component instanceof JTabbedPane) {
            TabbedPaneUtil.setMnemonics((JTabbedPane) component);
        }
    }

    private static boolean hasMnemonic(Pair<Integer, String> p) {
        return MNEMONIC_OF_CHAR.containsValue(p.getFirst());
    }

    /**
     * Returns the mnemonic characters of an collection of components.
     *
     * @param  components {@link JButton}s and {@link JLabel} will be queried
     * @return            mnemonics
     */
    public static List<Character> getMnemonicCharsOf(Collection<? extends Component> components) {
        if (components == null) {
            throw new NullPointerException("components == null");
        }

        List<Character> mnemonics = new ArrayList<Character>(components.size());

        for (Component component : components) {
            setMnemonics(component);

            int mnemonic = 0;

            if (component instanceof JButton) {
                mnemonic = ((JButton) component).getMnemonic();
            } else if (component instanceof JLabel) {
                mnemonic = ((JLabel) component).getDisplayedMnemonic();
            }

            if (mnemonic > 0) {
                mnemonics.add((char) mnemonic);
            }
        }

        return mnemonics;
    }

    private MnemonicUtil() {}
}
