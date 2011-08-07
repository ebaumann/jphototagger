package org.jphototagger.domain.repository.event.renametemplates;

import org.jphototagger.domain.templates.RenameTemplate;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class RenameTemplateUpdatedEvent {

    private final Object source;
    private final RenameTemplate template;

    public RenameTemplateUpdatedEvent(Object source, RenameTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }
        
        this.source = source;
        this.template = template;
    }

    public Object getSource() {
        return source;
    }

    public RenameTemplate getTemplate() {
        return template;
    }
}
