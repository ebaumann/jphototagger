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
package de.elmar_baumann.jpt.app;

import de.elmar_baumann.jpt.resource.Bundle;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 * Displays messages.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-12
 */
public final class MessageDisplayer {

    private static final Map<Integer, String> defaultTitleOfMessageType =
            new HashMap<Integer, String>();

    static {
        defaultTitleOfMessageType.put(JOptionPane.ERROR_MESSAGE,
                Bundle.getString("MessageDisplayer.DefaultTitle.ErrorMessage"));
        defaultTitleOfMessageType.put(JOptionPane.WARNING_MESSAGE,
                Bundle.getString("MessageDisplayer.DefaultTitle.WarningMessage"));
        defaultTitleOfMessageType.put(JOptionPane.INFORMATION_MESSAGE,
                Bundle.getString(
                "MessageDisplayer.DefaultTitle.Info"));
        defaultTitleOfMessageType.put(JOptionPane.QUESTION_MESSAGE,
                Bundle.getString("MessageDisplayer.DefaultTitle.QuestionMessage"));
    }

    /**
     * Displays an error message.
     * 
     * @param component   parent component or null
     * @param propertyKey property key for {@link Bundle}. If in the property
     *                    file exists a key having the same name plus the
     *                    postfix <code>.Title</code> this key is used for the
     *                    title. Else a default title will be set.
     * @param params      parameters for message format placeholders
     */
    public static void error(
            Component component, String propertyKey, Object... params) {
        message(component, propertyKey, JOptionPane.ERROR_MESSAGE, params);
    }

    /**
     * Displays an warning message.
     *
     * @param component   parent component or null
     * @param propertyKey property key for {@link Bundle}. If in the property
     *                    file exists a key having the same name plus the
     *                    postfix <code>.Title</code> this key is used for the
     *                    title. Else a default title will be set.
     * @param params      parameters for message format placeholders
     */
    public static void warning(
            Component component, String propertyKey, Object... params) {
        message(component, propertyKey, JOptionPane.WARNING_MESSAGE, params);
    }

    /**
     * Displays an information message.
     *
     * @param component   parent component or null
     * @param propertyKey property key for {@link Bundle}. If in the property
     *                    file exists a key having the same name plus the
     *                    postfix <code>.Title</code> this key is used for the
     *                    title. Else a default title will be set.
     * @param params      parameters for message format placeholders
     */
    public static void information(
            Component component, String propertyKey, Object... params) {
        message(component, propertyKey, JOptionPane.INFORMATION_MESSAGE, params);
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
         * @param  type          option type
         * @param  defaultAction action to return if type is invalid
         * @return               action or <code>defaultAction</code> if no
         *                       action has that option type
         */
        public static ConfirmAction actionOfOptionType(
                int type, ConfirmAction defaultAction) {
            for (ConfirmAction action : values()) {
                if (action.getOptionType() == type) {
                    return action;
                }
            }
            return defaultAction;
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
     * @param component    component where to display the dialog or null
     * @param propertyKey  property key for {@link Bundle}. There also a key for
     *                     the title has to be in the properties file with the
     *                     same name and the postfix <code>.Title</code>
     * @param cancelButton cancel button visibility
     * @param params       parameters for message format placeholders
     * @return             user action
     */
    public static ConfirmAction confirm(
            Component component,
            String propertyKey,
            CancelButton cancelButton,
            Object... params) {

        return ConfirmAction.actionOfOptionType(
                JOptionPane.showConfirmDialog(component,
                Bundle.getString(propertyKey, params),
                getTitle(propertyKey, JOptionPane.QUESTION_MESSAGE),
                cancelButton.isShow()
                ? JOptionPane.YES_NO_CANCEL_OPTION
                : JOptionPane.YES_NO_OPTION),
                cancelButton.isShow()
                ? ConfirmAction.CANCEL
                : ConfirmAction.NO);
    }

    private static void message(
            Component component, String propertyKey, int type, Object... params) {

        JOptionPane.showMessageDialog(
                component,
                Bundle.getString(propertyKey, params),
                getTitle(propertyKey, type),
                type);
    }

    private static String getTitle(String propertyKey, int messageType) {
        assert defaultTitleOfMessageType.containsKey(messageType) :
                "Message type " + messageType + " is not in " +
                defaultTitleOfMessageType.keySet();
        String titlePropertyKey = propertyKey + ".Title";
        return Bundle.containsKey(titlePropertyKey)
                ? Bundle.getString(titlePropertyKey)
                : defaultTitleOfMessageType.get(messageType);
    }

    private MessageDisplayer() {
    }
}
