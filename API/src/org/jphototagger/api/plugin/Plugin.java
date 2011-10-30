package org.jphototagger.api.plugin;

import java.awt.Component;

import org.jphototagger.api.component.DescriptionProvider;
import org.jphototagger.api.component.DisplayNameProvider;
import org.jphototagger.api.component.IconProvider;

/**
 * Extensions for JPhotoTagger invoked on demand.
 * <p>
 * Plugins will be picked up through the Java Service Provider Interface (SPI).
 *
 * @author Elmar Baumann
 */
public interface Plugin extends DescriptionProvider, DisplayNameProvider, IconProvider {

    /**
     *
     * @return Component for a settings dialog or null
     */
    Component getSettingsComponent();

    /**
     *
     * @return true, if available, false if this plugin does not work under
     * the current environmend, e.g. a "Windows only" plugin under a Unix system
     */
    boolean isAvailable();
}
