package org.jphototagger.services.plugin;

import java.awt.Component;

/**
 * A Selection Component will be placed whithin JPhotoTaggers Program Window (left side)
 * and is used to select files through a special context like a file folder (all files
 * in that folder) or a keyword (all files whoe's metadata containing that keyword).
 *
 * @author Elmar Baumann
 */
public interface SelectionComponentProvider extends ComponentProvider {

    @Override
    Component getComponent();
}
