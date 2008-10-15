package de.elmar_baumann.imv.model;

import com.imagero.reader.tiff.IFDEntry;
import de.elmar_baumann.imv.event.ErrorEvent;
import de.elmar_baumann.imv.event.listener.ErrorListeners;
import de.elmar_baumann.imv.image.metadata.exif.ExifIfdEntryDisplayComparator;
import de.elmar_baumann.imv.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 * EXIF-Daten eines Bilds.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class TableModelExif extends DefaultTableModel {

    private File file;
    private List<IFDEntry> allEntries;

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
            Logger.getLogger(TableModelExif.class.getName()).log(Level.WARNING, ex.getMessage());
            ErrorListeners.getInstance().notifyErrorListener(new ErrorEvent(ex.toString(), ExifMetadata.class));
        }
    }

    /**
     * Entfernt alle EXIF-Daten.
     */
    public void removeAllElements() {
        getDataVector().removeAllElements();

    }

    private void setExifData() {
        ExifMetadata exifMetadata = new ExifMetadata();
        allEntries = exifMetadata.getMetadata(file);
        if (allEntries != null) {
            List<IFDEntry> entries = ExifMetadata.getDisplayableMetadata(allEntries);
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
        List<IFDEntry> row = new ArrayList<IFDEntry>();
        row.add(entry);
        row.add(entry);
        super.addRow(row.toArray(new IFDEntry[row.size()]));
    }
}
