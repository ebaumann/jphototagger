package org.jphototagger.api.messages;

/**
 * @author Elmar Baumann
 */
public interface StatusBarMessageDisplayer {

    void setStatusbarText(String text, MessageType type, long millisecondsToDisplay);
}
