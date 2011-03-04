package org.jphototagger.program.event.listener;

import org.jphototagger.program.data.MetadataTemplate;

/**
 * Listens to events in
 * {@link org.jphototagger.program.database.DatabaseMetadataTemplates}.
 *
 * @author Elmar Baumann
 */
public interface DatabaseMetadataTemplatesListener {

    /**
     * Called if a template was deleted from
     * {@link org.jphototagger.program.database.DatabaseMetadataTemplates}.
     *
     * @param template  template
     */
    void templateDeleted(MetadataTemplate template);

    /**
     * Called if a template was inserted into
     * {@link org.jphototagger.program.database.DatabaseMetadataTemplates}.
     *
     * @param template inserted template
     */
    void templateInserted(MetadataTemplate template);

    /**
     * Called if a template was updated in
     * {@link org.jphototagger.program.database.DatabaseMetadataTemplates}.
     *
     * @param oldTemplate     old template before update
     * @param updatedTemplate updated template
     */
    void templateUpdated(MetadataTemplate oldTemplate, MetadataTemplate updatedTemplate);

    /**
     * Called if a template was renamed in
     * {@link org.jphototagger.program.database.DatabaseMetadataTemplates}.
     *
     * @param fromName old template name
     * @param toName   new template name
     */
    void templateRenamed(String fromName, String toName);
}
