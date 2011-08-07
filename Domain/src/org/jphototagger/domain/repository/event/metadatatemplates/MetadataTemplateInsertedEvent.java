package org.jphototagger.domain.repository.event.metadatatemplates;

import org.jphototagger.domain.templates.MetadataTemplate;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class MetadataTemplateInsertedEvent {

    private final Object source;
    private final MetadataTemplate template;

    public MetadataTemplateInsertedEvent(Object source, MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }
        
        this.source = source;
        this.template = template;
    }

    public MetadataTemplate getTemplate() {
        return template;
    }

    public Object getSource() {
        return source;
    }
}
