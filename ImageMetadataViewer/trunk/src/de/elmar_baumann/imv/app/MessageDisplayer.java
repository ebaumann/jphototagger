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
     * User action of a confirm message
     */
    public enum ConfirmAction {

        /**
         * User answered with "Yes"
         */
        YES(JOptionPane.YES_OPTION),
        /**
         * User answered with "No"
         */
        NO(JOptionPane.NO_OPTION),
        /**
         * User will cancel the operation
         */
        CANCEL(JOptionPane.CANCEL_OPTION);
        private final int optionType;

        private ConfirmAction(int optionType) {
            this.optionType = optionType;
        }

        /**
         * Returns the option type for a {@link JOptionPane}.
         *
         * @return option type
         */
        public int getOptionType() {
            return optionType;
        }

        /**
         * Returns the action type of an option type of {@link JOptionPane}.
         *
         * @param  type option type
         * @return      action or null if no action has that option type
         */
        public static ConfirmAction actionOfOptionType(int type) {
            for (ConfirmAction action : values()) {
                if (action.getOptionType() == type) {
                    return action;
                }
            }
            return null;
        }
    }

    /**
     * Options of a confirm message
     */
    public enum CancelButton {

        /**
         * Display the cancel button
         */
        SHOW(true),
        /**
         * Hide the cancel button
         */
        HIDE(false);
        private final boolean show;

        private CancelButton(boolean showCancelButton) {
            this.show = showCancelButton;
        }

        /**
         * Returns whether to display the canel button.
         *
         * @return true if the cancel button shall be displayed
         */
        public boolean isShow() {
            return show;
        }
    }

    /**
     * Displays a confirm message.
     *
     * @param propertyKey  property key for {@link Bundle}. There also a key for
     *                     the title has to be in the properties file with the
     *                     same name and the postfix <code>.Title</code>
     * @param cancelButton cancel button visibility
     * @param params       parameters for message format placeholders
     * @return             user action
     */
    public static ConfirmAction confirm(
            String propertyKey, CancelButton cancelButton, Object... params) {
        return ConfirmAction.actionOfOptionType(
                JOptionPane.showConfirmDialog(null,
                Bundle.getString(propertyKey, params),
                getTitle(propertyKey, JOptionPane.QUESTION_MESSAGE),
                cancelButton.isShow()
                ? JOptionPane.YES_NO_CANCEL_OPTION
                : JOptionPane.YES_NO_OPTION));
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
