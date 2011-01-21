package org.jphototagger.program.event.listener;

import org.jphototagger.program.data.RenameTemplate;

/**
 * Listens to events in
 * {@link org.jphototagger.program.database.DatabaseRenameTemplates}.
 *
 * @author Elmar Baumann
 */
public interface DatabaseRenameTemplatesListener {

    /**
     * Will be called if a template was
     * {@link org.jphototagger.program.database.DatabaseRenameTemplates}.
     *
     * @param template  template
     */
    void templateDeleted(RenameTemplate template);

    /**
     * Will be called if a template was inserted into
     * {@link org.jphototagger.program.database.DatabaseRenameTemplates}.
     *
     * @param template inserted template
     */
    void templateInserted(RenameTemplate template);

    /**
     * Will be called if a template was updated in
     * {@link org.jphototagger.program.database.DatabaseRenameTemplates}.
     *
     * @param template updated template
     */
    void templateUpdated(RenameTemplate template);
}
