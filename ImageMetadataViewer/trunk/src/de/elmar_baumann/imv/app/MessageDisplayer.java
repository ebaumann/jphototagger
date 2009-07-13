package de.elmar_baumann.imv.app;

import de.elmar_baumann.imv.resource.Bundle;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 * Displays messages.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/12
 */
public final class MessageDisplayer {

    private static final Map<Integer, String> defaultTitleOfMessageType =
            new HashMap<Integer, String>();

    {
        defaultTitleOfMessageType.put(JOptionPane.ERROR_MESSAGE,
                Bundle.getString("MessageDisplayer.DefaultTitle.ErrorMessage"));
        defaultTitleOfMessageType.put(JOptionPane.WARNING_MESSAGE,
                Bundle.getString("MessageDisplayer.DefaultTitle.WarningMessage"));
        defaultTitleOfMessageType.put(JOptionPane.INFORMATION_MESSAGE,
                Bundle.getString(
                "MessageDisplayer.DefaultTitle.InformationMessage"));
        defaultTitleOfMessageType.put(JOptionPane.QUESTION_MESSAGE,
                Bundle.getString("MessageDisplayer.DefaultTitle.QuestionMessage"));
    }

    /**
     * Displays an error message.
     * 
     * @param propertyKey property key for {@link Bundle}. If in the property
     *                    file exists a key having the same name plus the
     *                    postfix <code>.Title</code> this key is used for the
     *                    title. Else a default title will be set.
     * @param params      parameters for message format placeholders
     */
    public static void error(String propertyKey, Object... params) {
        message(propertyKey, JOptionPane.ERROR_MESSAGE, params);
    }

    /**
     * Displays an warning message.
     *
     * @param propertyKey property key for {@link Bundle}. If in the property
     *                    file exists a key having the same name plus the
     *                    postfix <code>.Title</code> this key is used for the
     *                    title. Else a default title will be set.
     * @param params      parameters for message format placeholders
     */
    public static void warning(String propertyKey, Object... params) {
        message(propertyKey, JOptionPane.WARNING_MESSAGE, params);
    }

    /**
     * Displays an information message.
     *
     * @param propertyKey property key for {@link Bundle}. If in the property
     *                    file exists a key having the same name plus the
     *                    postfix <code>.Title</code> this key is used for the
     *                    title. Else a default title will be set.
     * @param params      parameters for message format placeholders
     */
    public static void information(String propertyKey, Object... params) {
        message(propertyKey, JOptionPane.INFORMATION_MESSAGE, params);
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
                getTitle(propertyKey, JOptionPane.QUESTION_MESSAGE),
                cancel
                ? JOptionPane.YES_NO_CANCEL_OPTION
                : JOptionPane.YES_NO_OPTION);
    }

    private static void message(String propertyKey, int type, Object... params) {
        JOptionPane.showMessageDialog(null,
                Bundle.getString(propertyKey, params),
                getTitle(propertyKey, type),
                type);

    }

    private static String getTitle(String propertyKey, int messageType) {
        assert defaultTitleOfMessageType.containsKey(messageType) : messageType;
        return Bundle.containsKey(propertyKey)
               ? Bundle.getString(propertyKey + ".Title")
               : defaultTitleOfMessageType.get(messageType);
    }

    private MessageDisplayer() {
    }
}
