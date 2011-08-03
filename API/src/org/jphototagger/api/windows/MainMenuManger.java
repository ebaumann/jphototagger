package org.jphototagger.api.windows;

import javax.swing.Action;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface MainMenuManger {

    void addToFilesMenu(Action action);

    void addToEditMenu(Action action);

    void addToViewMenu(Action action);

    void addToGotoMenu(Action action);

    void addToToolsMenu(Action action);

    void addToWindowMenu(Action action);

    void addToHelpMenu(Action action);
}
