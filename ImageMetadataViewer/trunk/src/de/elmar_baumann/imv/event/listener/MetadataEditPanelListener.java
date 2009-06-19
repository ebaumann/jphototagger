package de.elmar_baumann.imv.event.listener;

import de.elmar_baumann.imv.event.MetadataEditPanelEvent;

/**
 * Beobachtet ein
 * {@link de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray}.
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public interface MetadataEditPanelListener {

    /**
     * Ein Ereignis fand statt.
     * 
     * @param event  Ereignis
     */
    public void actionPerformed(MetadataEditPanelEvent event);
}
