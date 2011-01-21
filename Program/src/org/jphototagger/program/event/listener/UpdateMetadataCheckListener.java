package org.jphototagger.program.event.listener;

import org.jphototagger.program.event.UpdateMetadataCheckEvent;

/**
 * Listens for updating metadata.
 *
 * @author Elmar Baumann
 */
public interface UpdateMetadataCheckListener {
    void checkForUpdate(UpdateMetadataCheckEvent evt);
}
