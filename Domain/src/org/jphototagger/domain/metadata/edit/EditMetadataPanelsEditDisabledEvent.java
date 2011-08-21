package org.jphototagger.domain.metadata.edit;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class EditMetadataPanelsEditDisabledEvent {

    private final Object source;

    public EditMetadataPanelsEditDisabledEvent(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }
}
