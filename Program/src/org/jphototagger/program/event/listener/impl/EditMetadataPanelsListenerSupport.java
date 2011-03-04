package org.jphototagger.program.event.listener.impl;

import org.jphototagger.program.event.listener.EditMetadataPanelsListener;

/**
 *
 * @author Elmar Baumann
 */
public final class EditMetadataPanelsListenerSupport extends ListenerSupport<EditMetadataPanelsListener> {
    public void notifyEditEnabled() {
        for (EditMetadataPanelsListener listener : listeners) {
            listener.editEnabled();
        }
    }

    public void notifyEditDisabled() {
        for (EditMetadataPanelsListener listener : listeners) {
            listener.editDisabled();
        }
    }
}
