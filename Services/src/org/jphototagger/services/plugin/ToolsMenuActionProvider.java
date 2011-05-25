package org.jphototagger.services.plugin;

import javax.swing.Action;

/**
 * A Tools Menu Action will be added to JPhotoTagger's "Tools" menu.
 *
 * @author Elmar Baumann
 */
public interface ToolsMenuActionProvider extends MenuActionProvider {

    @Override
    Action getMenuAction();
}
