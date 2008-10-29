package de.elmar_baumann.imv.event;

import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;

/**
 * Ereignis bei
 * {@link de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/22
 */
public class MetaDataEditPanelEvent {

    public enum Type {

        /**
         * Bearbeiten wurde ermöglict
         */
        EditEnabled,
        /**
         * Bearbeiten wurde deaktiviert
         */
        EditDisabled
    }
    private EditMetadataPanelsArray source;
    private Type type;

    /**
     * Konstruktor.
     * 
     * @param source  Ereignisquelle
     * @param type    Ereignistyp
     */
    public MetaDataEditPanelEvent(EditMetadataPanelsArray source, Type type) {
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
     * Setzt die Ereignisquelle.
     * 
     * @param source  Ereignisquelle
     */
    public void setSource(EditMetadataPanelsArray source) {
        this.source = source;
    }

    /**
     * Liefert den Ereignistyp.
     * 
     * @return Ereignistyp
     */
    public Type getType() {
        return type;
    }

    /**
     * Setzt den Ereignistyp.
     * 
     * @param type  Ereignistyp
     */
    public void setType(Type type) {
        this.type = type;
    }
}
