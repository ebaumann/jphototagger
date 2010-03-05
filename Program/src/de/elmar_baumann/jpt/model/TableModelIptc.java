/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.model;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.image.metadata.iptc.IptcEntryComparator;
import de.elmar_baumann.jpt.image.metadata.iptc.IptcEntry;
import de.elmar_baumann.jpt.image.metadata.iptc.IptcMetadata;
import de.elmar_baumann.jpt.resource.JptBundle;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * All elements are {@link IptcEntry}s of <em>one</em> image file retrieved
 * through {@link IptcMetadata#getIptcEntries(java.io.File)}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class TableModelIptc extends DefaultTableModel {

    private static final long            serialVersionUID = -3988241922301609843L;
    private              File            file;
    private              List<IptcEntry> iptcEntries      = new ArrayList<IptcEntry>();

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
        if (!UserSettings.INSTANCE.isDisplayIptc()) return;
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
        addColumn(JptBundle.INSTANCE.getString("TableModelIptc.HeaderColumn.1"));
        addColumn(JptBundle.INSTANCE.getString("TableModelIptc.HeaderColumn.2"));
        addColumn(JptBundle.INSTANCE.getString("TableModelIptc.HeaderColumn.3"));
    }
}
