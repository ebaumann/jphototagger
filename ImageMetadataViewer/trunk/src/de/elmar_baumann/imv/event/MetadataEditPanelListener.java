package de.elmar_baumann.imv.event;

/**
 * Beobachtet ein
 * {@link de.elmar_baumann.imv.view.panels.MetadataEditPanelsArray}.
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
