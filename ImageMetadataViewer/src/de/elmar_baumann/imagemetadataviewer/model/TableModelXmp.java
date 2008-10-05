package de.elmar_baumann.imagemetadataviewer.model;

import com.adobe.xmp.properties.XMPPropertyInfo;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 * Ansammlung von XMPPropertyInfo-Objekten.
 * 
 * @author  Elmar Baumann
 * @version 2008/02/19
 * @see     com.adobe.xmp.properties.XMPPropertyInfo
 */
public class TableModelXmp extends DefaultTableModel {

    private ArrayList<XMPPropertyInfo> propertyInfos;
    private String filename;

    public TableModelXmp() {
        setRowHeaders();
    }

    /**
     * Setzt die Property-Infos, deren Daten übernommen werden.
     * 
     * @param filename       Name der Datei, aus der die Property-Infos
     *                       ermittelt wurden oder null, falls diese
     *                       Inforation unwichtig ist
     * @param propertyInfos  Property-Infos
     */
    public void setPropertyInfosOfFile(String filename,
        ArrayList<XMPPropertyInfo> propertyInfos) {
        this.filename = filename;
        this.propertyInfos = propertyInfos;
        removeAllElements();
        setXmpData();
    }

    /**
     * Liefert den Namen der Datei, deren Property-Infos das Model enthält.
     * 
     * @return Dateiname oder null, wenn die Property-Infos entfernt wurden
     *         oder null gesetzt wurde mit
     *         {@link #setPropertyInfosOfFile(java.lang.String, java.util.ArrayList)}
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Entfernt alle XMP-Daten.
     */
    public void removeAllElements() {
        getDataVector().removeAllElements();
        filename = null;
    }

    private void setXmpData() {
        if (propertyInfos != null) {
            for (XMPPropertyInfo xmpPropertyInfo : propertyInfos) {
                addRow(xmpPropertyInfo);
            }
        }
    }

    private void addRow(XMPPropertyInfo xmpPropertyInfo) {
        String path = xmpPropertyInfo.getPath();
        Object value = xmpPropertyInfo.getValue();
        if (path != null && value != null) {
            ArrayList<XMPPropertyInfo> newRow = new ArrayList<XMPPropertyInfo>();
            newRow.add(xmpPropertyInfo);
            newRow.add(xmpPropertyInfo);
            super.addRow(newRow.toArray());
        }
    }

    private void setRowHeaders() {
        addColumn(Bundle.getString("TableModelXmp.HeaderColumn.1"));
        addColumn(Bundle.getString("TableModelXmp.HeaderColumn.2"));
    }
}
