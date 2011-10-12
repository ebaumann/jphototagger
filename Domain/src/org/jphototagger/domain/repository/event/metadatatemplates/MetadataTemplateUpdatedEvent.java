package org.jphototagger.domain.repository.event.metadatatemplates;

import org.jphototagger.domain.templates.MetadataTemplate;

/**
 * @author  Elmar Baumann
 */
public final class MetadataTemplateUpdatedEvent {

    private final Object source;
    private final MetadataTemplate oldTemplate;
    private final MetadataTemplate updatedTemplate;

    public MetadataTemplateUpdatedEvent(Object source, MetadataTemplate oldTemplate, MetadataTemplate updatedTemplate) {
        if (oldTemplate == null) {
            throw new NullPointerException("oldTemplate == null");
        }

        if (updatedTemplate == null) {
            throw new NullPointerException("updatedTemplate == null");
        }

        this.source = source;
        this.oldTemplate = oldTemplate;
        this.updatedTemplate = updatedTemplate;
    }

    public MetadataTemplate getOldTemplate() {
        return oldTemplate;
    }

    public MetadataTemplate getUpdatedTemplate() {
        return updatedTemplate;
    }

    public Object getSource() {
        return source;
    }
}
