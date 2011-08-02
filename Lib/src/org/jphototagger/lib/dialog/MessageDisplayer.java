package org.jphototagger.lib.dialog;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import org.jphototagger.lib.componentutil.ComponentUtil;

import org.jphototagger.lib.util.Bundle;

/**
 *
 * @author Elmar Baumann
 */
public final class MessageDisplayer {

    private static final Map<Integer, String> TITLE_OF_MESSAGE_TYPE = new HashMap<Integer, String>(4);

    static {
        TITLE_OF_MESSAGE_TYPE.put(JOptionPane.ERROR_MESSAGE,
                Bundle.getString(MessageDisplayer.class, "MessageDisplayer.DefaultTitle.ErrorMessage"));
        TITLE_OF_MESSAGE_TYPE.put(JOptionPane.WARNING_MESSAGE,
                Bundle.getString(MessageDisplayer.class, "MessageDisplayer.DefaultTitle.WarningMessage"));
        TITLE_OF_MESSAGE_TYPE.put(JOptionPane.INFORMATION_MESSAGE,
                Bundle.getString(MessageDisplayer.class, "MessageDisplayer.DefaultTitle.Info"));
        TITLE_OF_MESSAGE_TYPE.put(JOptionPane.QUESTION_MESSAGE,
                Bundle.getString(MessageDisplayer.class, "MessageDisplayer.DefaultTitle.QuestionMessage"));
    }

    private MessageDisplayer() {
    }

    /**
     *
     * @param message maybe null
     * @param input maybe null
     * @return user's input or null if the user did not accept the input
     */
    public static String input(String message, String input) {
        InputDialog inputDialog = new InputDialog();

        inputDialog.setInfo(message == null ? "" : message);
        inputDialog.setInput(input == null ? "" : input);
        inputDialog.setVisible(true);

        boolean accepted = inputDialog.isAccepted();

        return accepted
               ? inputDialog.getInput()
               : null;
    }

    /**
     *
     * @param parentComponent maybe null
     * @param message
     */
    public static void error(Component parentComponent, String message) {
        if (message == null) {
            throw new NullPointerException("message == null");
        }

        message(parentComponent, message, JOptionPane.ERROR_MESSAGE);
    }

    /**
     *
     * @param parentComponent maybe null
     * @param message
     */
    public static void warning(Component parentComponent, String message) {
        if (message == null) {
            throw new NullPointerException("message == null");
        }

        message(parentComponent, message, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Displays an information message.
     *
     * @param parentComponent  maybe null
     * @param message
     */
    public static void information(Component parentComponent, String message) {
        if (message == null) {
            throw new NullPointerException("message == null");
        }

        message(parentComponent, message, JOptionPane.INFORMATION_MESSAGE);
    }

    public enum ConfirmAction {
        YES,
        NO,
        CANCEL;
    }

    public static ConfirmAction confirmYesNoCancel(Component parentComponent, String message) {
        if (message == null) {
            throw new NullPointerException("message == null");
        }

        Component parent = getNotNullParent(parentComponent);
        String title = getTitleOfMessageType(JOptionPane.QUESTION_MESSAGE);
        int optionType = JOptionPane.YES_NO_CANCEL_OPTION;

        int option = JOptionPane.showConfirmDialog(parent, message, title, optionType);

        return option == JOptionPane.YES_OPTION
               ? ConfirmAction.YES
                : option == JOptionPane.NO_OPTION
                 ? ConfirmAction.NO
                 : ConfirmAction.CANCEL;
    }

    public static boolean confirmYesNo(Component parentComponent, String message) {
        if (message == null) {
            throw new NullPointerException("message == null");
        }

        Component parent = getNotNullParent(parentComponent);
        String title = getTitleOfMessageType(JOptionPane.QUESTION_MESSAGE);
        int optionType = JOptionPane.YES_NO_OPTION;

        return JOptionPane.showConfirmDialog(parent, message, title, optionType) == JOptionPane.YES_OPTION;
    }

    private static void message(Component parentComponent, String message, int messageType) {
        Component parent = getNotNullParent(parentComponent);
        String title = getTitleOfMessageType(messageType);

        JOptionPane.showMessageDialog(parent, message, title, messageType);
    }

    private static Component getNotNullParent(Component parentComponent) {
        return parentComponent == null ? ComponentUtil.getFrameWithIcon() : parentComponent;
    }

    private static String getTitleOfMessageType(int messageType) {
        return TITLE_OF_MESSAGE_TYPE.containsKey(messageType)
                ? TITLE_OF_MESSAGE_TYPE.get(messageType)
                : "";
    }
}
