package de.elmar_baumann.imv.model;

import com.adobe.xmp.properties.XMPPropertyInfo;
import de.elmar_baumann.imv.resource.Bundle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * Ansammlung von XMPPropertyInfo-Objekten.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 * @see     com.adobe.xmp.properties.XMPPropertyInfo
 */
public final class TableModelXmp extends DefaultTableModel {

    private List<XMPPropertyInfo> propertyInfos;
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
        List<XMPPropertyInfo> propertyInfos) {
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
     *         {@link #setPropertyInfosOfFile(java.lang.String, java.util.List)}
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Entfernt alle XMP-Daten.
     */
    public void removeAllElements() {
        getDataVector().clear();
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
            List<XMPPropertyInfo> newRow = new ArrayList<XMPPropertyInfo>();
            newRow.add(xmpPropertyInfo);
            newRow.add(xmpPropertyInfo);
            super.addRow(newRow.toArray(new XMPPropertyInfo[newRow.size()]));
        }
    }

    private void setRowHeaders() {
        addColumn(Bundle.getString("TableModelXmp.HeaderColumn.1"));
        addColumn(Bundle.getString("TableModelXmp.HeaderColumn.2"));
    }
}
