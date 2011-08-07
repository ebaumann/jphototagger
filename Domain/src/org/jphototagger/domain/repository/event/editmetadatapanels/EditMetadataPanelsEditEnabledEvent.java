package org.jphototagger.domain.repository.event.editmetadatapanels;

/**
 *
 *
 * @author  Elmar Baumann
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
