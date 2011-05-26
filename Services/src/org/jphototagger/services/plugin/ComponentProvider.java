package org.jphototagger.services.plugin;

import java.awt.Component;

/**
 * A Component will be added to a JPhotoTaggers Window.
 * <p>
 * Hint: A plugin can implement multiple component providers to
 * add multiple components. The component order is defined through
 * {@link #getPosition()}.
 *
 * @author Elmar Baumann
 */
public interface ComponentProvider extends PositionProvider {

    Component getComponent();

    /**
     * Localized description which will be used e.g. as title whithin tabs.
     *
     * @return <em>Short</em> name
     */
    String getDisplayName();

    /**
     *
     * @return Plugin which provides the component
     */
    MainWindowComponentPlugin getPlugin();
}
