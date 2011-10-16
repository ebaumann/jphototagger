package org.jphototagger.api.windows;

import org.jphototagger.api.messages.MessageType;

/**
 * @author Elmar Baumann
 */
public interface MainWindowManager {

    void setMainWindowTitle(String title);

    void setMainWindowStatusbarText(String text, MessageType type, long millisecondsToDisplay);
}
