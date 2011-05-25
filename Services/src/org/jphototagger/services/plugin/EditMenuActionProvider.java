package org.jphototagger.services.plugin;

import javax.swing.Action;

/**
 * An Edit Menu Action will be added to JPhotoTagger's "Edit" menu.
 *
 * @author Elmar Baumann
 */
public interface EditMenuActionProvider extends MenuActionProvider {

    @Override
    Action getMenuAction();
}
