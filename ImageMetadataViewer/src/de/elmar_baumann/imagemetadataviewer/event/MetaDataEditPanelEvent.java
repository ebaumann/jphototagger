package de.elmar_baumann.imagemetadataviewer.event;

import de.elmar_baumann.imagemetadataviewer.view.panels.MetadataEditPanelsArray;

/**
 * Ereignis bei
 * {@link de.elmar_baumann.imagemetadataviewer.view.panels.MetadataEditPanelsArray}.
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
    private MetadataEditPanelsArray source;
    private Type type;

    /**
     * Konstruktor.
     * 
     * @param source  Ereignisquelle
     * @param type    Ereignistyp
     */
    public MetaDataEditPanelEvent(MetadataEditPanelsArray source, Type type) {
        this.source = source;
        this.type = type;
    }

    /**
     * Liefert die Ereignisquelle.
     * 
     * @return Ereignisquelle
     */
    public MetadataEditPanelsArray getSource() {
        return source;
    }

    /**
     * Setzt die Ereignisquelle.
     * 
     * @param source  Ereignisquelle
     */
    public void setSource(MetadataEditPanelsArray source) {
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
