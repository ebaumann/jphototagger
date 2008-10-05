package de.elmar_baumann.imagemetadataviewer.model;

import de.elmar_baumann.imagemetadataviewer.image.metadata.iptc.IptcEntryComparator;
import de.elmar_baumann.imagemetadataviewer.image.metadata.iptc.IptcEntry;
import de.elmar_baumann.imagemetadataviewer.image.metadata.iptc.IptcMetadata;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import java.util.Collections;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 * IPTC-Daten eines Bilds.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/02/17
 */
public class TableModelIptc extends DefaultTableModel {

    private String filename;
    private ArrayList<IptcEntry> iptcEntries;

    public TableModelIptc() {
        addColumnHeaders();
    }

    /**
     * Setzt den Dateinamen des Bilds. Der bisherige Inhalt wird ersetzt
     * durch die IPTC-Daten des Bilds.
     * 
     * @param filename Dateiname
     */
    public void setFilename(String filename) {
        this.filename = filename;
        removeAllElements();
        IptcMetadata iptcMetadata = new IptcMetadata();
        iptcEntries = iptcMetadata.getMetadata(filename);
        addRows();
    }

    /**
     * Entfernt alle IPTC-Einträge.
     */
    public void removeAllElements() {
        getDataVector().removeAllElements();
    }

    /**
     * Liefert den Dateinamen des Bilds.
     * 
     * @return Dateiname. Null, falls nicht gesetzt.
     */
    public String getFilename() {
        return filename;
    }

    private void addRows() {
        if (iptcEntries != null) {
            Collections.sort(iptcEntries, new IptcEntryComparator());
            for (IptcEntry entry : iptcEntries) {
                super.addRow(getTableRow(entry));
            }
        }
    }

    private Object[] getTableRow(IptcEntry entry) {
        return new Object[]{entry, entry, entry};
    }

    private void addColumnHeaders() {
        addColumn(Bundle.getString("IptcTableModel.HeaderColumn.1"));
        addColumn(Bundle.getString("IptcTableModel.HeaderColumn.2"));
        addColumn(Bundle.getString("IptcTableModel.HeaderColumn.3"));
    }
}
