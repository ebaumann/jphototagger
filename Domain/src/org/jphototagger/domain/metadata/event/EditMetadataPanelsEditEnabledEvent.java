package org.jphototagger.domain.metadata.event;

/**
 * @author Elmar Baumann
 */
public final class EditMetadataPanelsEditEnabledEvent {

    private final Object source;

    public EditMetadataPanelsEditEnabledEvent(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }
}
