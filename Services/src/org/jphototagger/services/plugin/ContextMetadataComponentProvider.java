package org.jphototagger.services.plugin;

import java.awt.Component;

/**
 * A Context Metadata Component will be placed whithin JPhotoTaggers Program Window (right side)
 * and is used for displaying/editing metadata of the selected files.
 *
 * @author Elmar Baumann
 */
public interface ContextMetadataComponentProvider extends ComponentProvider {

    @Override
    Component getComponent();
}
