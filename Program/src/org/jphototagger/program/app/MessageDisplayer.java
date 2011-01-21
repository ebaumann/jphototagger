package org.jphototagger.program.app;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.dialog.InputDialog;

import java.awt.Component;

import java.io.File;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * Displays messages.
 *
 * @author Elmar Baumann
 */
public final class MessageDisplayer {
    private static final Map<Integer, String> defaultTitleOfMessageType =
        new HashMap<Integer, String>();
    private static final InputDialog inputDialog = new InputDialog();

    static {
        defaultTitleOfMessageType.put(
            JOptionPane.ERROR_MESSAGE,
            JptBundle.INSTANCE.getString(
                "MessageDisplayer.DefaultTitle.ErrorMessage"));
        defaultTitleOfMessageType.put(
            JOptionPane.WARNING_MESSAGE,
            JptBundle.INSTANCE.getString(
                "MessageDisplayer.DefaultTitle.WarningMessage"));
        defaultTitleOfMessageType.put(
            JOptionPane.INFORMATION_MESSAGE,
            JptBundle.INSTANCE.getString("MessageDisplayer.DefaultTitle.Info"));
        defaultTitleOfMessageType.put(
            JOptionPane.QUESTION_MESSAGE,
            JptBundle.INSTANCE.getString(
                "MessageDisplayer.DefaultTitle.QuestionMessage"));
    }

    private MessageDisplayer() {}

    /**
     * User action of a confirmYesNo message
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
         * @param  type          option type
         * @param  defaultAction action to return if type is invalid
         * @return               action or <code>defaultAction</code> if no
         *                       action has that option type
         */
        public static ConfirmAction actionOfOptionType(int type,
                ConfirmAction defaultAction) {
            for (ConfirmAction action : values()) {
                if (action.getOptionType() == type) {
                    return action;
                }
            }

            return defaultAction;
        }
    }

    /**
     * Displays a input dialog.
     *
     * @param infoBundleKey JptBundle key of info (prompts, what to input) or
     *                      null
     * @param input         default value or null
     * @param propertyKey   key to write size and location
     * @param infoArgs      optional argumets for the info message
     * @return              input or null if the user cancelled the input
     */
    public static String input(String infoBundleKey, String input,
                               String propertyKey, Object... infoArgs) {
        assert propertyKey != null;
        inputDialog.setInfo((infoBundleKey == null)
                            ? ""
                            : JptBundle.INSTANCE.getString(infoBundleKey,
                            infoArgs));
        inputDialog.setInput((input == null)
                             ? ""
                             : input);
        inputDialog.setProperties(UserSettings.INSTANCE.getProperties(),
                                  propertyKey + ".InputDialog");
        ComponentUtil.show(inputDialog);

        boolean accepted = inputDialog.isAccepted();

        UserSettings.INSTANCE.writeToFile();

        return accepted
               ? inputDialog.getInput()
               : null;
    }

    /**
     * Displays an error message.
     *
     * @param component   parent component or null
     * @param propertyKey property key for {@link JptBundle}. If in the property
     *                    file exists a key having the same name plus the
     *                    postfix <code>.Title</code> this key is used for the
     *                    title. Else a default title will be set.
     * @param params      parameters for message format placeholders
     */
    public static void error(Component component, String propertyKey,
                             Object... params) {
        message(component, propertyKey, JOptionPane.ERROR_MESSAGE, params);
        enableMenuItemErrorLogfile();
    }

    /**
     * Displays an warning message.
     *
     * @param component   parent component or null
     * @param propertyKey property key for {@link JptBundle}. If in the property
     *                    file exists a key having the same name plus the
     *                    postfix <code>.Title</code> this key is used for the
     *                    title. Else a default title will be set.
     * @param params      parameters for message format placeholders
     */
    public static void warning(Component component, String propertyKey,
                               Object... params) {
        message(component, propertyKey, JOptionPane.WARNING_MESSAGE, params);
    }

    /**
     * Displays an information message.
     *
     * @param component   parent component or null
     * @param propertyKey property key for {@link JptBundle}. If in the property
     *                    file exists a key having the same name plus the
     *                    postfix <code>.Title</code> this key is used for the
     *                    title. Else a default title will be set.
     * @param params      parameters for message format placeholders
     */
    public static void information(Component component, String propertyKey,
                                   Object... params) {
        message(component, propertyKey, JOptionPane.INFORMATION_MESSAGE,
                params);
    }

    private static void enableMenuItemErrorLogfile() {

        // The message displayer could be used before the GUI is created
        if (GUI.getAppFrame() == null) {
            return;
        }

        JMenuItem item = GUI.getAppFrame().getMenuItemDisplayLogfile();

        if (item.isEnabled()) {
            return;
        }

        String logfile = AppLoggingSystem.getLogfilePathErrorMessages();
        long   len     = new File(logfile).length();

        if (len > 0) {
            item.setEnabled(true);
        }
    }

    /**
     * Displays a confirm message with a Yes, No and Cancel button.
     *
     * @param component    component where to display the dialog or null
     * @param propertyKey  property key for {@link JptBundle}. There also a key
     *                     for the title has to be in the properties file with
     *                     the same name and the postfix <code>.Title</code>
     * @param params       optional parameters for message format placeholders
     * @return             user action
     */
    public static ConfirmAction confirmYesNoCancel(Component component,
            String propertyKey, Object... params) {
        int exit = JOptionPane.showConfirmDialog((component == null)
                ? GUI.getAppFrame()
                : component, JptBundle.INSTANCE.getString(propertyKey, params),
                             getTitle(propertyKey,
                                      JOptionPane.QUESTION_MESSAGE), JOptionPane
                                          .YES_NO_CANCEL_OPTION);

        return (exit == JOptionPane.YES_OPTION)
               ? ConfirmAction.YES
               : (exit == JOptionPane.NO_OPTION)
                 ? ConfirmAction.NO
                 : ConfirmAction.CANCEL;
    }

    /**
     * Displays a confirmYesNo message with a Yes and No option
     * (<em>no</em> Cancel option).
     *
     * @param component    component where to display the dialog or null
     * @param propertyKey  property key for {@link JptBundle}. There also a key
     *                     for the title has to be in the properties file with
     *                     the same name and the postfix <code>.Title</code>
     * @param params       optional parameters for message format placeholders
     * @return             user action
     */
    public static boolean confirmYesNo(Component component, String propertyKey,
                                       Object... params) {
        return JOptionPane.showConfirmDialog((component == null)
                ? GUI.getAppFrame()
                : component, JptBundle.INSTANCE.getString(propertyKey, params),
                             getTitle(propertyKey,
                                      JOptionPane.QUESTION_MESSAGE), JOptionPane
                                          .YES_NO_OPTION) == JOptionPane
                                              .YES_OPTION;
    }

    private static void message(Component component, String propertyKey,
                                int type, Object... params) {
        JOptionPane.showMessageDialog((component == null)
                                      ? GUI.getAppFrame()
                                      : component, JptBundle.INSTANCE.getString(
                                          propertyKey, params), getTitle(
                                          propertyKey, type), type);
    }

    private static String getTitle(String propertyKey, int messageType) {
        assert defaultTitleOfMessageType.containsKey(messageType) :
               "Message type " + messageType + " is not in "
               + defaultTitleOfMessageType.keySet();

        String titlePropertyKey = propertyKey + ".Title";

        return JptBundle.INSTANCE.containsKey(titlePropertyKey)
               ? JptBundle.INSTANCE.getString(titlePropertyKey)
               : defaultTitleOfMessageType.get(messageType);
    }
}
