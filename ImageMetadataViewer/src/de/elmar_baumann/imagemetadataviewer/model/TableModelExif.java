package de.elmar_baumann.imagemetadataviewer.model;

import com.imagero.reader.tiff.IFDEntry;
import de.elmar_baumann.imagemetadataviewer.image.metadata.exif.ExifIfdEntryDisplayComparator;
import de.elmar_baumann.imagemetadataviewer.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import java.util.Collections;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 * EXIF-Daten eines Bilds.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/02/19
 */
public class TableModelExif extends DefaultTableModel {

    private String filename;
    private ArrayList<IFDEntry> allEntries;

    public TableModelExif() {
        setRowHeaders();
    }

    private void setRowHeaders() {
        addColumn(Bundle.getString("TableModelExif.HeaderColumn.1")); // NOI18N
        addColumn(Bundle.getString("TableModelExif.HeaderColumn.2")); // NOI18N
    }

    /**
     * Liefert den Dateinamen des Bilds.
     * 
     * @return Dateiname. Null, wenn keiner gesetzt wurde.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Setzt den Dateinamen des Bilds. Der bisherige Inhalt wird ersetzt
     * durch die EXIF-Daten des Bilds.
     * 
     * @param filename Dateiname
     */
    public void setFilename(String filename) {
        this.filename = filename;
        removeAllElements();
        setExifData();
    }

    /**
     * Entfernt alle EXIF-Daten.
     */
    public void removeAllElements() {
        getDataVector().removeAllElements();
    }

    private void setExifData() {
        ExifMetadata exifMetadata = new ExifMetadata();
        allEntries = exifMetadata.getMetadata(filename);
        if (allEntries != null) {
            ArrayList<IFDEntry> entries = ExifMetadata.getDisplayableMetadata(allEntries);
            if (entries != null) {
                Collections.sort(entries, new ExifIfdEntryDisplayComparator());
                for (IFDEntry entry : entries) {
                    String value = entry.toString();
                    if (value.length() > 0) {
                        addRow(entry);
                    }
                }
            }
        }
    }

    private void addRow(IFDEntry entry) {
        ArrayList<IFDEntry> row = new ArrayList<IFDEntry>();
        row.add(entry);
        row.add(entry);
        super.addRow(row.toArray());
    }
}
