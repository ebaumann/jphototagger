package de.elmar_baumann.imv.event.listener;

import de.elmar_baumann.imv.event.CheckForUpdateMetadataEvent;

/**
 * Listens for updating metadata.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-06
 */
public interface CheckingForUpdateMetadataListener {

    public void actionPerformed(CheckForUpdateMetadataEvent evt);
}
