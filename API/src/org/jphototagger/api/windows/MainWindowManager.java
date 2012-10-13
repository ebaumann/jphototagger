package org.jphototagger.api.windows;

import java.awt.Component;
import org.jphototagger.api.messages.MessageType;

/**
 * @author Elmar Baumann
 */
public interface MainWindowManager {

    void setMainWindowTitle(String title);

    void setMainWindowStatusbarText(String text, MessageType type, long millisecondsToDisplay);

    boolean isSelectionComponentSelected(Component Component);

    boolean isEditComponentSelected(Component Component);
}
