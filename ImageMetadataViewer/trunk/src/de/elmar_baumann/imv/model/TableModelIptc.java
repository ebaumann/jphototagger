package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.image.metadata.iptc.IptcEntryComparator;
import de.elmar_baumann.imv.image.metadata.iptc.IptcEntry;
import de.elmar_baumann.imv.image.metadata.iptc.IptcMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * IPTC-Daten eines Bilds.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class TableModelIptc extends DefaultTableModel {

    private File file;
    private List<IptcEntry> iptcEntries = new ArrayList<IptcEntry>();

    public TableModelIptc() {
        addColumnHeaders();
    }

    /**
     * Setzt die Bilddatei. Der bisherige Inhalt wird ersetzt
     * durch die IPTC-Daten des Bilds.
     * 
     * @param file  Datei
     */
    public void setFile(File file) {
        this.file = file;
        removeAllElements();
        iptcEntries = IptcMetadata.getIptcEntries(file);
        addRows();
    }

    /**
     * Entfernt alle IPTC-Einträge.
     */
    public void removeAllElements() {
        getDataVector().removeAllElements();
    }

    /**
     * Liefert die Bilddatei.
     * 
     * @return Dateiname. Null, falls nicht gesetzt.
     */
    public File getFile() {
        return file;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    private void addRows() {
        Collections.sort(iptcEntries, IptcEntryComparator.INSTANCE);
        for (IptcEntry entry : iptcEntries) {
            super.addRow(getTableRow(entry));
        }
    }

    private Object[] getTableRow(IptcEntry entry) {
        return new Object[]{entry, entry, entry};
    }

    private void addColumnHeaders() {
        addColumn(Bundle.getString("TableModelIptc.HeaderColumn.1"));
        addColumn(Bundle.getString("TableModelIptc.HeaderColumn.2"));
        addColumn(Bundle.getString("TableModelIptc.HeaderColumn.3"));
    }
}
