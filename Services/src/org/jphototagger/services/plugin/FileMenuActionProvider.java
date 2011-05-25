package org.jphototagger.services.plugin;

import javax.swing.Action;

/**
 * A File Menu Action will be added to JPhotoTagger's "File" menu.
 *
 * @author Elmar Baumann
 */
public interface FileMenuActionProvider extends MenuActionProvider {

    @Override
    Action getMenuAction();
}
