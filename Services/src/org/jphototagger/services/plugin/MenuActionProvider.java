package org.jphototagger.services.plugin;

import javax.swing.Action;

/**
 * A Menu Action will be added as menu item to JPhotoTagger's progam window menu bar.
 * This interface will not used directly, only interfaces extending this interface,
 * to determine, to which main menu item the action's item shall be added, e.g. to
 * the Files menu or to the Edit menu.
 * <p>
 * <em>The action must have a localized displayable name</em>
 * ({@link Action#putValue(java.lang.String, java.lang.Object)} with
 * {@link Action#NAME} as key and the display name as value).
 * <p>
 * Hint: A plugin can implement multiple menu action providers to
 * add multiple menu items. The item order is defined through
 * {@link #getPosition()}.
 *
 * @author Elmar Baumann
 */
public interface MenuActionProvider extends PositionProvider {

    Action getMenuAction();
}
