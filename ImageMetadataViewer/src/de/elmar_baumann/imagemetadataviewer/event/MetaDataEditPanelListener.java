package de.elmar_baumann.imagemetadataviewer.event;

/**
 * Beobachtet ein
 * {@link de.elmar_baumann.imagemetadataviewer.view.panels.MetaDataEditPanelsArray}.
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public interface MetaDataEditPanelListener {

    /**
     * Ein Ereignis fand statt.
     * 
     * @param event  Ereignis
     */
    public void actionPerformed(MetaDataEditPanelEvent event);
}
