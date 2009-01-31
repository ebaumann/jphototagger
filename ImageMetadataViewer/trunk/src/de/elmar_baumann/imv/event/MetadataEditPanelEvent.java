package de.elmar_baumann.imv.event;

import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;

/**
 * Ereignis bei
 * {@link de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/22
 */
public final class MetadataEditPanelEvent {

    public enum Type {

        /**
         * Bearbeiten wurde ermöglict
         */
        EDIT_ENABLED,
        /**
         * Bearbeiten wurde deaktiviert
         */
        EDIT_DISABLED
    }
    
    private final EditMetadataPanelsArray source;
    private final Type type;

    /**
     * Konstruktor.
     * 
     * @param source  Ereignisquelle
     * @param type    Ereignistyp
     */
    public MetadataEditPanelEvent(EditMetadataPanelsArray source, Type type) {
        this.source = source;
        this.type = type;
    }

    /**
     * Liefert die Ereignisquelle.
     * 
     * @return Ereignisquelle
     */
    public EditMetadataPanelsArray getSource() {
        return source;
    }

    /**
     * Liefert den Ereignistyp.
     * 
     * @return Ereignistyp
     */
    public Type getType() {
        return type;
    }
}
