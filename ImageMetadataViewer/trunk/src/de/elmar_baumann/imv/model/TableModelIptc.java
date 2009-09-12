/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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
        addColumn(Bundle.getString("TableModelIptc.HeaderColumn.1")); // NOI18N
        addColumn(Bundle.getString("TableModelIptc.HeaderColumn.2")); // NOI18N
        addColumn(Bundle.getString("TableModelIptc.HeaderColumn.3")); // NOI18N
    }
}
