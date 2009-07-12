package de.elmar_baumann.imv.app;

import de.elmar_baumann.imv.resource.Bundle;
import javax.swing.JOptionPane;

/**
 * Displays messages.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/12
 */
public final class MessageDisplayer {

    /**
     * Displays an error message.
     * 
     * @param propertyKey property key for {@link Bundle}. There also a key for
     *                    the title has to be in the properties file with the
     *                    same name and the postfix <code>.Title</code>
     * @param params      parameters for message format placeholders
     */
    public static void error(String propertyKey, Object... params) {
        JOptionPane.showMessageDialog(null,
                Bundle.getString(propertyKey, params),
                Bundle.getString(propertyKey + ".Title"),
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays an error message.
     *
     * @param propertyKey property key for {@link Bundle}. There also a key for
     *                    the title has to be in the properties file with the
     *                    same name and the postfix <code>.Title</code>
     */
    public static void error(String propertyKey) {
        JOptionPane.showMessageDialog(null,
                Bundle.getString(propertyKey),
                Bundle.getString(propertyKey + ".Title"),
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays a confirm message.
     *
     * @param propertyKey property key for {@link Bundle}. There also a key for
     *                    the title has to be in the properties file with the
     *                    same name and the postfix <code>.Title</code>
     * @param cancel      true if a cancel button shall be displayed
     * @return            one of these:
     *                    <ul>
     *                    <li>{@link JOptionPane#YES_OPTION}</li>
     *                    <li>{@link JOptionPane#NO_OPTION}</li>
     *                    <li>{@link JOptionPane#CANCEL_OPTION}</li>
     *                    </ul>
     */
    public static int confirm(String propertyKey, boolean cancel) {
        return JOptionPane.showConfirmDialog(null,
                Bundle.getString(propertyKey),
                propertyKey + ".Title",
                cancel
                ? JOptionPane.YES_NO_CANCEL_OPTION
                : JOptionPane.YES_NO_OPTION);
    }

    /**
     * Displays a confirm message.
     *
     * @param propertyKey property key for {@link Bundle}. There also a key for
     *                    the title has to be in the properties file with the
     *                    same name and the postfix <code>.Title</code>
     * @param cancel      true if a cancel button shall be displayed
     * @param params      parameters for message format placeholders
     * @return            one of these:
     *                    <ul>
     *                    <li>{@link JOptionPane#YES_OPTION}</li>
     *                    <li>{@link JOptionPane#NO_OPTION}</li>
     *                    <li>{@link JOptionPane#CANCEL_OPTION}</li>
     *                    </ul>
     */
    public static int confirm(
            String propertyKey, boolean cancel, Object... params) {
        return JOptionPane.showConfirmDialog(null,
                Bundle.getString(propertyKey, params),
                propertyKey + ".Title",
                cancel
                ? JOptionPane.YES_NO_CANCEL_OPTION
                : JOptionPane.YES_NO_OPTION);
    }

    private MessageDisplayer() {
    }
}
