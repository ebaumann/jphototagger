package org.jphototagger.program.module.iptc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jphototagger.iptc.IptcEntry;
import org.jphototagger.iptc.IptcEntryComparator;
import org.jphototagger.iptc.IptcIgnoreCache;
import org.jphototagger.iptc.IptcMetadata;
import org.jphototagger.lib.swing.TableModelExt;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.filefilter.AppFileFilters;

/**
 * All elements are {@code IptcEntry}s of <em>one</em> image file retrieved
 * through {@code IptcMetadata#getIptcEntries(java.io.File)}.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class IptcTableModel extends TableModelExt {

    private static final long serialVersionUID = 1L;
    private File file;
    private List<IptcEntry> iptcEntries = new ArrayList<IptcEntry>();

    public IptcTableModel() {
        addColumnHeaders();
    }

    /**
     * Setzt die Bilddatei. Der bisherige Inhalt wird ersetzt
     * durch die IPTC-Daten des Bilds.
     *
     * @param file  Datei
     */
    public void setFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (IptcIgnoreCache.INSTANCE.isIgnore(file) || AppFileFilters.INSTANCE.isUserDefinedFileType(file)) {
            return;
        }

        this.file = file;
        iptcEntries = IptcMetadata.getIptcEntries(file);
        IptcIgnoreCache.INSTANCE.setIgnore(file, iptcEntries.isEmpty());
        removeAllRows();
        addRows();
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
        addColumn(Bundle.getString(IptcTableModel.class, "IptcTableModel.HeaderColumn.1"));
        addColumn(Bundle.getString(IptcTableModel.class, "IptcTableModel.HeaderColumn.2"));
        addColumn(Bundle.getString(IptcTableModel.class, "IptcTableModel.HeaderColumn.3"));
    }
}
