package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.image.metadata.exif.ExifGpsMetadata;
import de.elmar_baumann.imv.image.metadata.exif.ExifIfdEntryDisplayComparator;
import de.elmar_baumann.imv.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import de.elmar_baumann.imv.resource.Bundle;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * EXIF-Daten eines Bilds.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class TableModelExif extends DefaultTableModel {

    private File file;
    private ExifGpsMetadata gps;
    private List<IdfEntryProxy> allEntries;

    public TableModelExif() {
        setRowHeaders();
    }

    private void setRowHeaders() {
        addColumn(Bundle.getString("TableModelExif.HeaderColumn.1")); // NOI18N
        addColumn(Bundle.getString("TableModelExif.HeaderColumn.2")); // NOI18N
    }

    /**
     * Returns the file.
     * 
     * @return file or null if not set
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets the file with exif metadata to be displayed.
     * 
     * @param file file
     */
    public void setFile(File file) {
        this.file = file;
        removeAllElements();
        try {
            setExifData();
        } catch (Exception ex) {
            AppLog.logWarning(getClass(), ex);
        }
    }

    /**
     * Entfernt alle EXIF-Daten.
     */
    public void removeAllElements() {
        getDataVector().removeAllElements();

    }

    private void setExifData() {
        allEntries = ExifMetadata.getMetadata(file);
        if (allEntries != null) {
            List<IdfEntryProxy> entries = ExifMetadata.getDisplayableMetadata(allEntries);
            if (entries != null) {
                Collections.sort(entries, ExifIfdEntryDisplayComparator.INSTANCE);
                for (IdfEntryProxy entry : entries) {
                    String value = entry.toString();
                    if (value.length() > 0) {
                        addRow(entry);
                    }
                }
            }
            addGps();
        }
    }

    private void addGps() {
        gps = ExifMetadata.getGpsMetadata(allEntries);
        if (gps != null) {
        }
    }

    public ExifGpsMetadata getGps() {
        return gps;
    }

    private void addRow(IdfEntryProxy entry) {
        List<IdfEntryProxy> row = new ArrayList<IdfEntryProxy>();
        row.add(entry);
        row.add(entry);
        super.addRow(row.toArray(new IdfEntryProxy[row.size()]));
    }
}
