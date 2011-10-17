package org.jphototagger.iptcmodule;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openide.util.Lookup;

import org.jphototagger.domain.repository.UserDefinedFileTypesRepository;
import org.jphototagger.iptc.IptcEntry;
import org.jphototagger.iptc.IptcEntry;
import org.jphototagger.iptc.IptcEntryComparator;
import org.jphototagger.iptc.IptcEntryComparator;
import org.jphototagger.iptc.IptcIgnoreCache;
import org.jphototagger.iptc.IptcIgnoreCache;
import org.jphototagger.iptc.IptcMetadata;
import org.jphototagger.iptc.IptcMetadata;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.swing.TableModelExt;
import org.jphototagger.lib.util.Bundle;

/**
 * All elements are {@code IptcEntry}s of <em>one</em>.
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

    public void setFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (IptcIgnoreCache.INSTANCE.isIgnore(file) || isUserDefinedFileType(file)) {
            return;
        }

        this.file = file;
        iptcEntries = IptcMetadata.getIptcEntries(file);
        IptcIgnoreCache.INSTANCE.setIgnore(file, iptcEntries.isEmpty());
        removeAllRows();
        addRows();
    }

    private boolean isUserDefinedFileType(File file) {
        UserDefinedFileTypesRepository repo = Lookup.getDefault().lookup(UserDefinedFileTypesRepository.class);
        String fileSuffix = FileUtil.getSuffix(file);
        return repo.existsUserDefinedFileTypeWithSuffix(fileSuffix);
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
