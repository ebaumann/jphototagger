package org.jphototagger.domain.event.listener;

import org.jphototagger.domain.event.UpdateMetadataCheckEvent;

/**
 * Listens for updating metadata.
 *
 * @author Elmar Baumann
 */
public interface UpdateMetadataCheckListener {

    void checkForUpdate(UpdateMetadataCheckEvent evt);
}
